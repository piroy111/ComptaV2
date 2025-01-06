package step0treatrawdata.clientsrefinersbrokers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicFichiers;
import basicmethods.LitUnFichierEnLignes;
import staticbkincome.hedging.BKIncomeHedgingManager;
import staticdata.StaticBKIncome.BKI_HEDGING_BROKERS;
import staticdata.StaticNames;
import step0treatrawdata.postchecker.ColumnIdx;
import step1loadtransactions.accounts.BKAccount;

public class TREATBrokers extends TREATRoot {

	public TREATBrokers(TREATManager _sTREATManager, String _sDirImport, 
			String _sDirTreated, BKI_HEDGING_BROKERS _sBroker) {
		super(_sTREATManager, _sDirImport, _sDirTreated);
		pBroker = _sBroker;
	}

	/*
	 * Data
	 */
	private BKI_HEDGING_BROKERS pBroker;
	
	public final void run(LitUnFichierEnLignes _sReadFile) {
			}

	@Override public void treatAndWriteFile(LitUnFichierEnLignes _sReadFile) {
		/*
		 * Initiate for new file
		 */
		String lNameFile = _sReadFile.getmNomFichier();
		List<String> lListLineToWrite = new ArrayList<String>();
		Map<String, Integer> lMapHeaderToIdxColumn = ColumnIdx.getMapIdxColumns(_sReadFile);
		/*
		 * Header
		 */
		String lHeader = _sReadFile.getmHeadersAndComments().get(0);
		lHeader += "," + BKAccount.class.getSimpleName()
				+ "," + "BKIncome";
		/*
		 * Fill
		 */
		for (int lIdx = 0; lIdx < _sReadFile.getmContenuFichierLignes().size(); lIdx++) {
			List<String> lLineList = _sReadFile.getmContenuFichierListe().get(lIdx);
			/*
			 * Replace the comment by inserting the name of the broker
			 */
			
 			/*
 			 * Replacement in case of a future on currency
 			 */
 			int lIdxBKAsset = lMapHeaderToIdxColumn.get("BKAsset");
 			String lBKAssetName = lLineList.get(lIdxBKAsset);
 			if (lBKAssetName.length() == 6 && lBKAssetName.contains("USD")) {
 				/*
 				 * Case we need to change the price
 				 */
 				if (lBKAssetName.startsWith("USD")) {
 					int lIdxBKPrice = lMapHeaderToIdxColumn.get("BKPrice");
 					double lBKPrice = AMNumberTools.getDouble(lLineList.get(lIdxBKPrice));
 					lLineList.set(lIdxBKPrice, "" + 1 / lBKPrice);
 					int lIdxAmount = lMapHeaderToIdxColumn.get("Amount");
 					double lBKAmount = AMNumberTools.getDouble(lLineList.get(lIdxAmount))
 							* lBKPrice;
 					lLineList.set(lIdxAmount, "" + -lBKAmount);
 				}
 				/*
 				 * Replace the name of the asset
 				 */
 				lLineList.set(lIdxBKAsset, "X" + lBKAssetName.replaceAll("USD", ""));
 			}
 			/*
 			 * Add the BKAccount
 			 */
 			lLineList.add(StaticNames.getACCOUNT_BUNKER());
 			/*
 			 * BKIncome
 			 */
 			String lComment = lLineList.get(lMapHeaderToIdxColumn.get("Comment"));
 			String lBKIncome = BKIncomeHedgingManager.getBKIncomeStr(pBroker, lComment);
 			lLineList.add(lBKIncome);
 			/*
 			 * Build the line for the file and add to List of lines
 			 */
 			String lLineStr = lLineList.get(0);
			for (int lKdx = 1; lKdx < lLineList.size(); lKdx++) {
				lLineStr += "," + lLineList.get(lKdx);
			}
			lListLineToWrite.add(lLineStr);
		}
		/*
		 * Write file treated
		 */
		BasicFichiers.writeFile(pDirTreat, lNameFile, lHeader, lListLineToWrite);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
