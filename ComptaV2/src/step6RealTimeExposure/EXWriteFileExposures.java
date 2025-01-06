package step6RealTimeExposure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.AMNumberTools;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.StaticDate;
import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step0treatrawdata.objects.BKAssetSort2;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step1loadtransactions.transactions.BKTransactionManager;
import step3statements.statements.main.STSortBKHolder;
import step3statements.statements.main.STSortBKHolderIfBKAccount;

public class EXWriteFileExposures extends BKHolderGenerator {

	public EXWriteFileExposures(BKTransactionManager _sBKTransactionManager) {
		super(_sBKTransactionManager);
	}

	/*
	 * Filter
	 */
	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return true;
	}
	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAccount().getpEmailAddress()
					+ "; " + _sBKTransaction.getpBKAccount().getpBKAssetCurrency().getpName();
	}
	
	
	
	
	/*
	 * Write file
	 */
	public final void writeFile() {
		generateBKHolder();
		Collections.sort(pListBKHolder, new STSortBKHolder());
		/*
		 * Sort the BKHolder in case they are BKAssets
		 */
		List<BKHolder> lListBKHolder = new ArrayList<BKHolder>(getpListBKHolder());
		List<BKAsset> lListBKAsset = new ArrayList<BKAsset>(BKAssetManager.getpListBKAsset());
		Collections.sort(lListBKAsset, new BKAssetSort2());
		/*
		 * Header
		 */
		String lHeader = "Name,Currency,Total value USD";
		for (BKAsset lBKAsset : lListBKAsset) {
			lHeader += "," + lBKAsset.getpName();
		}
		/*
		 * Find the last date
		 */
		int lDateMax = -1;
		for (BKHolder lBKHolder : lListBKHolder) {
			int lDate = lBKHolder.getpListDate().get(lBKHolder.getpListDate().size() - 1);
			if (lDateMax == -1) {
				lDateMax = lDate;
			} else {
				lDateMax = Math.min(lDateMax, lDate);
			}
		}
		/*
		 * Sort the BKHolder
		 */
		Collections.sort(lListBKHolder, new STSortBKHolderIfBKAccount(lDateMax));
		/*
		 * Content
		 */
		List<String> lListLineToWrite = new ArrayList<String>();
		/*
		 * Write the prices of the BKAssets
		 */
		String lLine = ",,Price USD";
		for (BKAsset lBKAsset : lListBKAsset) {
			lLine += "," + lBKAsset.getpMapDateToPrice().get(lDateMax);
		}
		lListLineToWrite.add(lLine);
		/*
		 * File Content
		 */		
		for (BKHolder lBKHolder : lListBKHolder) {
			BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDateMax);
			String lKey = lBKHolder.getpKey();
			if (lKey.contains("; ")) {
				lKey = lKey.replaceAll("; ",",");
			} else {
				lKey += ",";
			}
			String lLineStr = lKey + "," + lBKInventory.getpValueUSD();
			for (BKAsset lBKAsset : lListBKAsset) {
				lLineStr += ",";
				Double lQuantity = lBKInventory.getpMapBKAssetToQuantity().get(lBKAsset);
				if (lQuantity != null && AMNumberTools.isNotNull(lQuantity)) {
					lLineStr += lQuantity;
				}
			}
			lListLineToWrite.add(lLineStr);
		}
		/*
		 * Write file
		 */
		String lDir = StaticDir.getOUTPUT_EXPOSURE_REAL_TIME();
		String lName = StaticDate.getDATE_MAX() + "_" + this.getClass().getSimpleName() + ".csv";
		BasicFichiers.writeFile(lDir, lName, lHeader, lListLineToWrite);
		BasicPrintMsg.display(this, "File written: " + lDir + lName);
	}


	
	
	
	
	
}
