package step5fiscalyearend.balancesheetv2.writefiledebug;

import java.util.ArrayList;
import java.util.List;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step5fiscalyearend.balancesheetv2.BSManager;
import step5fiscalyearend.balancesheetv2.assets.BSAsset;
import step5fiscalyearend.balancesheetv2.assets.BSAssetGenerator;
import step5fiscalyearend.balancesheetv2.assets.BSAssetGenerator.BS_TYPE;

public class BSAssetWriteFileDebugMirrorTransactions {

	/**
	 * Write a file debug in case there are some mirror BKTransactions whose added value is not zero
	 */
	public BSAssetWriteFileDebugMirrorTransactions(BSManager _sBSManager) {
		pBSManager = _sBSManager;
		/*
		 * 
		 */
		BasicFichiers.getOrCreateDirectory(StaticDir.getOUTPUT_FY_DEBUG_BS_MIRROR());
	}

	/*
	 * Data
	 */
	private BSManager pBSManager;

	/**
	 * 
	 */
	public final void run() {
		for (BSAsset lBSAsset : pBSManager.getpBSAssetManager().getpListBSAsset()) {
			if (lBSAsset.getpBSType() == BS_TYPE.Mirror) {
				for (int lDateFY : pBSManager.getpFYMain().getpListDateFYToDo()) {
					Double lValueUSD = lBSAsset.getpAndComputeValueUSD(lDateFY);
					if (lValueUSD != null && !AMNumberTools.isEqual(lValueUSD, 0.)) {
						/*
						 * We found a bug because for mirror assets, the value should always be zero --> we write a file
						 */
						List<String> lListLineToWrite = new ArrayList<>();
						for (BKHolder lBKHolder : lBSAsset.getpListBKHolder()) {
							BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory()
									.get(Math.min(BasicDateInt.getmToday(),  lDateFY));
							if (lBKInventory != null) {
								BKInventory lBKInventoryLoop = lBKInventory;
								String lLine0 = lBSAsset.getpKeyStr()
										+ "," + lBSAsset.getpBSType()
										+ "," + lBSAsset.getpTitle();
								while (lBKInventoryLoop != null) {
									for (BKTransaction lBKTransaction : lBKInventoryLoop.getpListBKTransactionToday()) {
										String lLine = lLine0
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
						/*
						 * Header
						 */
						String lHeader = "BSAsset KeyStr,BSAsset BSType,BSAsset Title"
								+ ",BKTransaction " + "BKAccount"
								+ ",BKTransaction " + "comment"
								+ ",BKTransaction " + "BKAsset"
								+ ",BKTransaction " + "Quantity"
								+ ",BKTransaction " + "Price"
								+ ",BKTransaction " + "Value USD"
								+ ",BKTransaction " + "BKIncome"
								+ ",BKTransaction " + "FileNameOrigin";
						/*
						 * Write file debug
						 */
						String lDir = StaticDir.getOUTPUT_FY_DEBUG_BS_MIRROR();
						String lNameFile = BasicDateInt.getmToday() 
								+ StaticNames.getOUTPUT_FY_DEBUG_BS_MIRROR();
						BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
						/*
						 * Communication, error and kill
						 */
						BasicPrintMsg.error("There is a mirror asset which has a non zero value -> I write a file debug"
								+ "\nBSAsset= " + lBSAsset.getpKeyStr()
								+ "\nValue which should be zero= " + lValueUSD
								+ "\nFile debug= '" + lDir + lNameFile + "'"
								+ "\n\nIt is very important to understand that if a transaction is not identified in a physical file above,"
								+ "\nthen this transaction cannot create an asset for Bunker. It must be part of a mirror transaction"
								+ "\nIf the value$ of the mirror transactions is not 0, then it means it is either a bug or we need to create a new physical account"
								+ "\nCreate a new physical account means creating a new line in the 'ASSETS' of the balance sheet"
								+ "\nIn order to create a new line of 'ASSETS', you must go to the method '" + BSAssetGenerator.class.getSimpleName() + "' and add a case depending of the file origin of the transaction (the file origin determines the physical account)");
					}
				}
			}
		}
	}

}
