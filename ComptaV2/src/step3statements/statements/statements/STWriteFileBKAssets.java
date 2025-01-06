package step3statements.statements.statements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step0treatrawdata.objects.BKAssetSort;
import step1loadtransactions.holder.BKHolder;
import step3statements.statements.abstractstatements.STAll;
import step3statements.statements.main.STStatementAbstract;

public class STWriteFileBKAssets extends STStatementAbstract {

	public STWriteFileBKAssets(STAll _sSTAll) {
		super(_sSTAll);
	}
	
	@Override public String getpDirName() {
		return StaticDir.getOUTPUT_DEBUG();
	}

	@Override public void createLines() {
		/*
		 * Sort the BKAsset
		 */
		List<BKAsset> lListBKAsset = new ArrayList<BKAsset>(BKAssetManager.getpListBKAsset());
		Collections.sort(lListBKAsset, new BKAssetSort());
		/*
		 * Header
		 */
		String lHeader1 = "#Date";
		String lHeader2 = "#Date";
		/*
		 * Sum of all account
		 */
		for (BKAsset lBKAsset : lListBKAsset) {
			lHeader1 += ",Total";
			lHeader2 += "," + lBKAsset.getpName();
		}
		/*
		 * Each Account
		 */
		for (BKHolder lBKHolder : pSTStatementGeneratorAbstract.getpListBKHolder()) {
			for (BKAsset lBKAsset : lListBKAsset) {
				lHeader1 += "," + lBKHolder.getpKey();
				lHeader2 += "," + lBKAsset.getpName();
			}
		}
		/*
		 * File content
		 */
		addToListLine(lHeader1);
		addToListLine(lHeader2);
		for (int lDate : pSTStatementGeneratorAbstract.getpListDate()) {
			String lLine = "";
			Map<BKAsset, Double> lMapBKAssetToTotalQuantity = new HashMap<BKAsset, Double>();
			/*
			 * Write the BKAssets for all the accounts
			 */
			for (BKHolder lBKHolder : pSTStatementGeneratorAbstract.getpListBKHolder()) {
				for (BKAsset lBKAsset : lListBKAsset) {
					/*
					 * Write the quantity of the looped BKAsset for the looped date and the looped BKAccount
					 */
					lLine += ",";
					double lQuantity = 0.;
					if (lBKHolder.getpMapDateToBKInventory().containsKey(lDate)) {
						lQuantity = lBKHolder.getpMapDateToBKInventory().get(lDate).getpQuantity(lBKAsset);
					}
					lLine += lQuantity;
					/*
					 * Add to sum
					 */
					Double lQuantityTotal = lMapBKAssetToTotalQuantity.get(lBKAsset);
					if (lQuantityTotal == null) {
						lQuantityTotal = 0.;
					}
					lQuantityTotal += lQuantity;
					lMapBKAssetToTotalQuantity.put(lBKAsset, lQuantityTotal);
				}
			}
			/*
			 * Write the sum of all BKAccounts
			 */
			String lLineBegin = "" + lDate;
			for (BKAsset lBKAsset : lListBKAsset) {
				Double lQuantityTotal = lMapBKAssetToTotalQuantity.get(lBKAsset);
				lLineBegin += ",";
				if (lQuantityTotal != null) {
					lLineBegin += lQuantityTotal;
				}
			}
			lLine = lLineBegin + lLine;
			/*
			 * Write Line
			 */
			addToListLine(lLine);
		}
	}

	
	
	
}
