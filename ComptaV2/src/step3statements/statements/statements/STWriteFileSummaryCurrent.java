package step3statements.statements.statements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.AMNumberTools;
import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step0treatrawdata.objects.BKAssetSort2;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step3statements.statements.abstractstatements.STAll;
import step3statements.statements.main.STSortBKHolderIfBKAccount;
import step3statements.statements.main.STStatementAbstract;

public class STWriteFileSummaryCurrent extends STStatementAbstract {

	public STWriteFileSummaryCurrent(STAll _sSTAll) {
		super(_sSTAll);
	}
	
	@Override public String getpDirName() {
		return StaticDir.getOUTPUT_DEBUG();
	}

	@Override public void createLines() {
		/*
		 * Sort the BKHolder in case they are BKAssets
		 */
		List<BKHolder> lListBKHolder = new ArrayList<BKHolder>(pSTStatementGeneratorAbstract.getpListBKHolder());
		List<BKAsset> lListBKAsset = new ArrayList<BKAsset>(BKAssetManager.getpListBKAsset());
		Collections.sort(lListBKAsset, new BKAssetSort2());
		/*
		 * Header
		 */
		addToHeader("Name,Currency,Total value USD");
		for (BKAsset lBKAsset : lListBKAsset) {
			addToHeader(lBKAsset.getpName());
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
		 * Write the prices of the BKAssets
		 */
		String lLine = ",,Price USD";
		for (BKAsset lBKAsset : lListBKAsset) {
			lLine += "," + lBKAsset.getpMapDateToPrice().get(lDateMax);
		}
		addToListLine(lLine);
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
			addToListLine(lLineStr);
		}
	}
	
	
	
	
	
	
	
}
