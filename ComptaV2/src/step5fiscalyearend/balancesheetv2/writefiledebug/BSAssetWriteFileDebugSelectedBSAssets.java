package step5fiscalyearend.balancesheetv2.writefiledebug;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step5fiscalyearend.balancesheetv2.BSManager;
import step5fiscalyearend.balancesheetv2.assets.BSAsset;

public class BSAssetWriteFileDebugSelectedBSAssets {

	/**
	 * Write a file debug in case there are some mirror BKTransactions whose added value is not zero
	 */
	public BSAssetWriteFileDebugSelectedBSAssets(BSManager _sBSManager) {
		pBSManager = _sBSManager;
		/*
		 * 
		 */
		BasicFichiers.getOrCreateDirectory(StaticDir.getOUTPUT_FY_DEBUG_BS_SELECTED());
	}

	/*
	 * Data
	 */
	private BSManager pBSManager;

	/**
	 * To be customize by the user manually
	 * @param _sBSAsset
	 * @return
	 */
	private static boolean getpIsKeepBSAsset(BSAsset _sBSAsset) {
		String lTitle;
		lTitle = "Clients holdings in USD";
		lTitle = "annesoline2001@yahoo.fr_00.csv -> EUR";
		return _sBSAsset.getpTitle().contains(lTitle);
	}

	/**
	 * 
	 */
	public final void run() {
		/*
		 * Build file content
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		for (BSAsset lBSAsset : pBSManager.getpBSAssetManager().getpListBSAsset()) {
			if (getpIsKeepBSAsset(lBSAsset)) {
				for (int lDateFY : pBSManager.getpFYMain().getpListDateFYToDo()) {
					for (BKHolder lBKHolder : lBSAsset.getpListBKHolder()) {
						/*
						 * Load
						 */
						BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory()
								.get(Math.min(BasicDateInt.getmToday(),  lDateFY));
						String lLine0 = lBSAsset.getpKeyStr()
								+ "," + lBSAsset.getpBSType()
								+ "," + lBSAsset.getpTitle()
								+ "," + lDateFY;
						/*
						 * Case the BKInventory is null -> we write null
						 */
						if (lBKInventory == null) {
							lListLineToWrite.add(lLine0 + ",null");
						}
						/*
						 * Otherwise we print all the BKTransactions
						 */
						else {
							BKInventory lBKInventoryLoop = lBKInventory;
							while (lBKInventoryLoop != null) {
								for (BKTransaction lBKTransaction : lBKInventoryLoop.getpListBKTransactionToday()) {
									String lLine = lLine0
											+ "," + lBKTransaction.getpDate()
											+ "," + lBKTransaction.getpBKAccount()
											+ "," + lBKTransaction.getpComment()
											+ "," + lBKTransaction.getpBKAsset()
											+ "," + lBKTransaction.getpQuantity()
											+ "," + lBKTransaction.getpBKPrice()
											+ "," + lBKTransaction.getpValueUSD()
											+ "," + lBKTransaction.getpBKIncome()
											+ "," + lBKTransaction.getpFileNameOrigin();
									lListLineToWrite.add(lLine);
								}
								lBKInventoryLoop = lBKInventoryLoop.getpBKInventoryPrevious();
							}
						}
						
					}
				}
			}
		}
		/*
		 * Header
		 */
		String lHeader = "BSAsset KeyStr,BSAsset BSType,BSAsset Title, DateFY"
				+ ",BKTransaction " + "Date"
				+ ",BKTransaction " + "BKAccount"
				+ ",BKTransaction " + "comment"
				+ ",BKTransaction " + "BKAsset"
				+ ",BKTransaction " + "Quantity"
				+ ",BKTransaction " + "Price"
				+ ",BKTransaction " + "Value USD"
				+ ",BKTransaction " + "BKIncome"
				+ ",BKTransaction " + "FileNameOrigin";
		/*
		 * Write file
		 */
		String lDir = StaticDir.getOUTPUT_FY_DEBUG_BS_SELECTED();
		String lNameFile = BasicDateInt.getmToday() 
				+ StaticNames.getOUTPUT_FY_DEBUG_BS_SELECTED();
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
	}



}
