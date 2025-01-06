package step5fiscalyearend.balancesheet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import staticdata.StaticNames;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.inventory.BKInventory;
import step5fiscalyearend.debug.FYDebugFile;
import step5fiscalyearend.debug.FYDebugFileManager;

public class FYTreatClient {

	protected FYTreatClient(FYBalanceSheet _sFYBalanceSheet, String _sBKAccountStr) {
		pFYBalanceSheet = _sFYBalanceSheet;
		pBKAccountStr = _sBKAccountStr;
		pMapBKCurrencyToAmount = new HashMap<BKAsset, Double>();
		pFYDebugFileManager = _sFYBalanceSheet.getpFYMain().getpFYDebugFileManager();
		/*
		 * Initiate file debug
		 */
		String lNameFileDebug = pFYBalanceSheet.getpDateNameFile()
				+ StaticNames.getOUTPUT_FY_DEBUG_CASH_CLIENTS();
		pFYDebugFile = pFYDebugFileManager.getpOrCreateFYFile(lNameFileDebug);
		pFYDebugFile.setpHeader("DatFY,BKAccount,Currency,Key of BKInventory,Amount,Amount in USD");
	}

	/*
	 * Data
	 */
	private String pBKAccountStr;
	private FYBalanceSheet pFYBalanceSheet;
	private Map<BKAsset, Double> pMapBKCurrencyToAmount;
	private FYDebugFileManager pFYDebugFileManager;
	private FYDebugFile pFYDebugFile;

	/**
	 * Compute the data for the DataFY
	 * @param _sDateFY
	 */
	protected final void run(String _sBKAccountStr, BKInventory _sBKInventory) {
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAsset()) {
			if (lBKAsset.getpIsCurrency()) {
				Double lQty = _sBKInventory.getpMapBKAssetToQuantity().get(lBKAsset);
				if (lQty != null) {
					Double lQuantity = pMapBKCurrencyToAmount.get(lBKAsset);
					if (lQuantity == null || Double.isNaN(lQuantity)) {
						lQuantity = lQty;
					} else {
						lQuantity += lQty;
					}
					pMapBKCurrencyToAmount.put(lBKAsset, lQuantity);
					/*
					 * Write line in the debug file
					 */
					String lLine = pFYBalanceSheet.getpDateFY()
							+ "," + _sBKAccountStr
							+ "," + lBKAsset.getpName()
							+ "," + _sBKInventory.getpIDTrackFrom()
							+ "," + lQty
							+ "," + (lQty / BKAssetManager.getpForexReference(pFYBalanceSheet.getpDateFY(), lBKAsset.getpName()));
					pFYDebugFile.addNewLine(lLine);
					Collections.sort(pFYDebugFile.getpListLineToWrite());
				}
			}
		}
	}

	/*
	 * Getters & Setters
	 */
	public final FYBalanceSheet getpFYBalanceSheet() {
		return pFYBalanceSheet;
	}
	public final Map<BKAsset, Double> getpMapBKCurrencyToAmount() {
		return pMapBKCurrencyToAmount;
	}
	public final String getpBKAccountStr() {
		return pBKAccountStr;
	}

























}
