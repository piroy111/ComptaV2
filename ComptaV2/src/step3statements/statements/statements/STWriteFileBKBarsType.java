package step3statements.statements.statements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import conf.bkbartype.BKBarType;
import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.bars.BKBar;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step3statements.statements.abstractstatements.STBKAccounts;
import step3statements.statements.main.STSortBKHolderIfBKAccount;
import step3statements.statements.main.STStatementAbstract;

public class STWriteFileBKBarsType extends STStatementAbstract {

	public STWriteFileBKBarsType(STBKAccounts _sSTBKAccounts) {
		super(_sSTBKAccounts);
	}

	@Override public String getpDirName() {
		return StaticDir.getOUTPUT_BKBARS();
	}

	@Override public void createLines() {
		List<BKHolder> lListBKHolder = new ArrayList<BKHolder>(pSTStatementGeneratorAbstract.getpListBKHolder());
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
		 * Header
		 */
		List<BKBarType> lListBKBarType = new ArrayList<BKBarType>();
		for (BKHolder lBKHolder : lListBKHolder) {
			BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDateMax);
			for (BKBar lBKBar : lBKInventory.getpMapBKBarToHolding().keySet()) {
				Integer lQty = lBKInventory.getpBKBarHolding(lBKBar);
				if (lQty != null && lQty != 0 && !lListBKBarType.contains(lBKBar.getpBKBarType())) {
					lListBKBarType.add(lBKBar.getpBKBarType());
				}
			}
		}
		Collections.sort(lListBKBarType);
		addToHeader("Name,Currency,Total value USD");
		for (BKBarType lBKBarType : lListBKBarType) {
			addToHeader(lBKBarType.getmName());
		}
		/*
		 * Header avec les poids
		 */
		String lHeader2 = "#,,Oz";
		for (BKBarType lBKBarType : lListBKBarType) {
			lHeader2 += "," + lBKBarType.getmWeightOz();
		}
		addToListLine(lHeader2);
		String lHeader3 = "#,,ValueUSD";
		for (BKBarType lBKBarType : lListBKBarType) {
			for (BKAsset lBKAsset : BKAssetManager.getpListBKAsset()) {
				if (lBKAsset.getpIsBar() && lBKAsset.getpName().contains(lBKBarType.getmMetal())) {
					lHeader3 += "," + lBKBarType.getmWeightOz() * lBKAsset.getpPriceUSD(lDateMax);
				}
			}
		}
		addToListLine(lHeader3);
		/*
		 * File Content
		 */
		for (BKHolder lBKHolder : lListBKHolder) {
			BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDateMax);
			/*
			 * First columns: the ID of the BKHolder
			 */
			String lKey = lBKHolder.getpKey();
			if (lKey.contains("; ")) {
				lKey = lKey.replaceAll("; ",",");
			} else {
				lKey += ",";
			}
			String lLineStr = lKey + "," + lBKInventory.getpValueUSD();
			/*
			 * Count holdings
			 */
			Map<BKBarType, Integer> lMapBKBarTypeToHolding = new HashMap<BKBarType, Integer>();
			for (BKBar lBKBar : lBKInventory.getpMapBKBarToHolding().keySet()) {
				BKBarType lBKBarType = lBKBar.getpBKBarType();
				Integer lQty = lMapBKBarTypeToHolding.get(lBKBarType);
				if (lQty == null) {
					lQty = 0;
				}
				lQty += lBKInventory.getpBKBarHolding(lBKBar);
				lMapBKBarTypeToHolding.put(lBKBarType, lQty);
			}
			/*
			 * Write Line
			 */
			for (BKBarType lBKBarType : lListBKBarType) {
				Integer lQty = lMapBKBarTypeToHolding.get(lBKBarType);
				if (lQty == null) {
					lQty = 0;
				}
				lLineStr += "," + lQty;
			}
			addToListLine(lLineStr);
		}
	}




}
