package step3statements.statements.statements;

import java.util.ArrayList;
import java.util.List;

import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step3statements.statements.abstractstatements.STIncome;
import step3statements.statements.main.STStatementAbstract;

public class STWriteFileAccountingPvL extends STStatementAbstract {

	public STWriteFileAccountingPvL(STIncome _sSTIncome) {
		super(_sSTIncome);
	}
	
	
	@Override public String getpDirName() {
		return StaticDir.getOUTPUT_PVL(); // 01
	}

	/*
	 * Write file
	 */
	@Override public final void createLines() {
		/*
		 * Filter the list of BKHolder which match only Bunker
		 */
		List<BKHolder> lListBKHolder = new ArrayList<>();
		for (BKHolder lBKHolder : pSTStatementGeneratorAbstract.getpListBKHolder()) {
			if (lBKHolder.getpKey().startsWith(BKAccountManager.getpBKAccountBunker().getpEmailAddress())) {
				lListBKHolder.add(lBKHolder);
			}
		}
		/*
		 * Header
		 */
		addToHeader("Date");
		for (BKHolder lBKHolder : lListBKHolder) {
			addToHeader(lBKHolder.getpKey());
		}
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAsset()) {
			addToHeader("BKAsset: " + lBKAsset.getpName());
		}
		for (BKHolder lBKHolder : lListBKHolder) {
			addToHeader("BKTransaction: " + lBKHolder.getpKey());
		}
		/*
		 * File content
		 */
		for (int lDate : pSTStatementGeneratorAbstract.getpListDate()) {
			String lLine0 = null;
			String lLine1 = "" + lDate;
			if (BKAssetManager.getpListDatePriceChange().contains(lDate)) {
				lLine0 = "" + lDate;
			}
			/*
			 * Value of the FOLIO
			 */
			for (BKHolder lBKHolder : lListBKHolder) {
				BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDate);
				if (lBKInventory == null) {
					lLine1 += ",0";
				} else {
					lLine1 += "," + lBKInventory.getpValueUSD();
				}
				if (lLine0 != null) {
					if (lBKInventory == null) {
						lLine0 += ",0";
					} else {
						lLine0 += "," + lBKInventory.getpValueUSDWithPriceYstD();
					}
				}
			}
			/*
			 * Write the BKAssets
			 */
			for (BKAsset lBKAsset : BKAssetManager.getpListBKAsset()) {
				/*
				 * Compute the quantity as the sum over all the BKHolder
				 */
				double lQuantity = Double.NaN;
				for (BKHolder lBKHolder : lListBKHolder) {
					BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDate);
					if (lBKInventory != null) {
						Double lQty = lBKInventory.getpMapBKAssetToQuantity().get(lBKAsset);
						if (lQty != null) {
							if (Double.isNaN(lQuantity)) {
								lQuantity = lQty;
							} else {
								lQuantity += lQty;
							}
						}
					}
				}
				/*
				 * Write the total quantity of the asset held
				 */
				String lWord = ",";
				if (!Double.isNaN(lQuantity)) {
					lWord += "" + lQuantity;
				}
				lLine1 += lWord;
				if (lLine0 != null) {
					lLine0 += lWord;
				}
			}
			/*
			 * Write the list of the BKTransaction
			 */
			for (BKHolder lBKHolder : lListBKHolder) {
				BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDate);
				String lWord = ",";
				if (lBKInventory != null) {
					lWord += toStringListBKTransaction(lBKInventory.getpListBKTransactionToday());
				}
				if (lLine0 != null) {
					lLine0 += lWord;
				} else {
					lLine1 += lWord;
				}
			}
			/*
			 * Add Line to the list to write in the file
			 */
			if (lLine0 != null) {
				addToListLine(lLine0);
			}
			addToListLine(lLine1);
		}
	}
	
	/**
	 * special to String to shorten the display
	 * @param _sListBKTransaction
	 * @return
	 */
	private String toStringListBKTransaction(List<BKTransaction> _sListBKTransaction) {
		String lToString = "[";
		for (int lIdx = 0; lIdx < _sListBKTransaction.size(); lIdx++) {
			BKTransaction lBKTransaction = _sListBKTransaction.get(lIdx);
			if (lIdx > 0) {
				lToString += " // ";
			}
			lToString += lBKTransaction.getpBKAsset().toString()
			+ "; " + lBKTransaction.getpQuantity()
			+ "; " + lBKTransaction.getpComment()
			+ "; " + lBKTransaction.getpBKAccount().getpEmailAddress();
		}
		lToString += "]";
		return lToString;
	}


	

	

	
}
