package step4reconciliation.sources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.transactions.BKTransaction;
import step1loadtransactions.transactions.BKTransactionManager;

class RNWriteFileAssets {

	protected final static  void writeFileAsset(String _sDirTreated, String _sDirOutput) {
		List<String> lListDirTreated = new ArrayList<>();
		lListDirTreated.add(_sDirTreated);
		writeFileAsset(lListDirTreated, _sDirOutput);
	}
	
	protected final static  void writeFileAsset(List<String> _sListDirTreated, String _sDirOutput) {
		/*
		 * Load all transactions from files
		 */
		BKTransactionManager lBKTransactionManager = new BKTransactionManager();
		for (String lDirTreated : _sListDirTreated) {
			lBKTransactionManager.readFile(lDirTreated);
		}
		List<BKTransaction> lListBKTransaction = new ArrayList<BKTransaction>(lBKTransactionManager
				.getpListBKTransaction());
		Collections.sort(lListBKTransaction);
		/*
		 * Initiate for the writing the file content
		 */
		List<String> lListLineToWrite = new ArrayList<String>();
		int lDateMin = lListBKTransaction.get(0).getpDate();
		int lDateMax = BasicDateInt.getmToday();
		int lIdxBKTransaction = 0;
		BKTransaction lBKTransaction = lListBKTransaction.get(lIdxBKTransaction);
		Map<BKAsset, Double> lMapBKAssetToAmount = new HashMap<BKAsset, Double>();
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAsset()) {
			lMapBKAssetToAmount.put(lBKAsset, 0.);
		}
		/*
		 * Write one line per date
		 */
		for (int lDate = lDateMin; lDate <= lDateMax; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
			/*
			 * Case there is a BKTransaction
			 */
			while (lBKTransaction != null && lBKTransaction.getpDate() <= lDate) {
				/*
				 * Store the date of the BKTransaction
				 */
				BKAsset lBKAsset = lBKTransaction.getpBKAsset(); 
				double lAmount = lMapBKAssetToAmount.get(lBKAsset) + lBKTransaction.getpQuantity();
				lMapBKAssetToAmount.put(lBKAsset, lAmount);
				/*
				 * Move on to the next BKTransaction
				 */
				lIdxBKTransaction++;
				if (lIdxBKTransaction >= lListBKTransaction.size()) {
					lBKTransaction = null;
				} else {
					lBKTransaction = lListBKTransaction.get(lIdxBKTransaction);
				}
			}
			/*
			 * Write the line
			 */
			String lLineStr = "" + lDate;
			for (BKAsset lBKAsset : BKAssetManager.getpListBKAsset()) {
				lLineStr += "," + lMapBKAssetToAmount.get(lBKAsset);
			}
			lListLineToWrite.add(lLineStr);
		}
		/*
		 * Write the BKAsset in the headers
		 */
		String lHeader = "#Date";
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAsset()) {
			lHeader += "," + lBKAsset.getpName();
		}
		/*
		 * Name of the file
		 */
		String lNameFile = BasicDateInt.getmToday() + "_";
		if (_sListDirTreated.size() > 1) {
			lNameFile += "all";
		} else {
			String lNameLastDir = BasicFichiers.getLastDir(_sListDirTreated.get(0));
			if (lNameLastDir.charAt(2) == '_') {
				lNameLastDir = lNameLastDir.substring(3);
			}
			lNameFile += lNameLastDir;
		}
		lNameFile += "_assets.csv";
		/*
		 * Write File Assets
		 */
		BasicFichiers.getOrCreateDirectory(_sDirOutput);
		System.out.println("Write file: " + _sDirOutput + lNameFile);
		BasicFichiers.writeFile(_sDirOutput, lNameFile, lHeader, lListLineToWrite);
	}


}
