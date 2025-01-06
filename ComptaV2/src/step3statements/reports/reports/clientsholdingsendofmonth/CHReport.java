package step3statements.reports.reports.clientsholdingsendofmonth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.StaticNames;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;

class CHReport {

	protected CHReport(int _sDate) {
		pDate = _sDate;
		/*
		 * 
		 */
		pMapBKAccountToMapBKAssetToHolding = new HashMap<>();
		pListBKAccount = new ArrayList<>();
		pListBKAsset = new ArrayList<>();
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private Map<BKAccount, Map<BKAsset, Double>> pMapBKAccountToMapBKAssetToHolding;
	private List<BKAccount> pListBKAccount;
	private List<BKAsset> pListBKAsset;
	
	/**
	 * 
	 * @param _sBKAccount
	 * @param _sBKAsset
	 * @param _sHolding
	 */
	public final void putNewHolding(BKAccount _sBKAccount, BKAsset _sBKAsset, double _sHolding) {
		Map<BKAsset, Double> lMapBKAssetToHolding = pMapBKAccountToMapBKAssetToHolding.get(_sBKAccount);
		if (lMapBKAssetToHolding == null) {
			lMapBKAssetToHolding = new HashMap<>();
			pMapBKAccountToMapBKAssetToHolding.put(_sBKAccount, lMapBKAssetToHolding);
			/*
			 * 
			 */
			pListBKAccount.add(_sBKAccount);
			Collections.sort(pListBKAccount);
		}
		if (!pListBKAsset.contains(_sBKAsset)) {
			pListBKAsset.add(_sBKAsset);
			Collections.sort(pListBKAsset);
		}
		lMapBKAssetToHolding.put(_sBKAsset, _sHolding);
	}	
	
	/**
	 * @return Holding not null
	 * @param _sBKAccount
	 * @param _sBKAsset
	 */
	public final double getpHolding(BKAccount _sBKAccount, BKAsset _sBKAsset) {
		Map<BKAsset, Double> lMapBKAssetToHolding = pMapBKAccountToMapBKAssetToHolding.get(_sBKAccount);
		if (lMapBKAssetToHolding == null) {
			return 0.;
		}
		Double lHolding = lMapBKAssetToHolding.get(_sBKAsset);
		if (lHolding == null) {
			return 0.;
		}
		return lHolding;
	}
	
	/**
	 * @return List in the form of a String: "BKAccount,BKAsset,ValueReport,ValueReportCompare" when there is a difference<br>
	 * returns an empty list when there is no difference
	 */
	public final List<String> compare(CHReport _sCHReport) {
		List<String> lListErrors = new ArrayList<>();
		List<BKAccount> lListBKAccount = new ArrayList<>(BKAccountManager.getpMapNameToBKAccount().values());
		List<BKAsset> lListBKAsset = new ArrayList<>(BKAssetManager.getpListBKAsset());
		for (BKAccount lBKAccount : lListBKAccount) {
			for (BKAsset lBKAsset : lListBKAsset) {
				double lHoldingMain = getpHolding(lBKAccount, lBKAsset);
				double lHoldingCompare = _sCHReport.getpHolding(lBKAccount, lBKAsset);
				if (!AMNumberTools.isEqual(lHoldingMain, lHoldingCompare)) {
					String lLine = lBKAccount.getpEmailAddress()
							+ "," + lBKAsset.getpName()
							+ "," + lHoldingMain
							+ "," + lHoldingCompare;
					lListErrors.add(lLine);
				}
			}
		}
		return lListErrors;
	}

	/**
	 * 
	 */
	public final void writeReport(String _sDir) {
		String lNameReport = pDate + StaticNames.getOUTPUT_HOLDING_CLIENTS_END_OF_MONTH();
		/*
		 * Header
		 */
		String lHeader = BKAccount.class.getSimpleName();
		for (BKAsset lBKAsset : pListBKAsset) {
			lHeader += "," + lBKAsset.getpName();
		}
		/*
		 * Content
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		for (BKAccount lBKAccount : pListBKAccount) {
			String lLine = lBKAccount.getpEmailAddress();
			for (BKAsset lBKAsset : pListBKAsset) {
				double lHolding = getpHolding(lBKAccount, lBKAsset);
				lLine += "," + lHolding;
			}
			lListLineToWrite.add(lLine);
		}
		/*
		 * Write file
		 */
		BasicFichiers.writeFile(_sDir, lNameReport, lHeader, lListLineToWrite);
		BasicPrintMsg.display(this, "CHFile written in '" + _sDir + lNameReport + "'");
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final Map<BKAccount, Map<BKAsset, Double>> getpMapBKAccountToMapBKAssetToHolding() {
		return pMapBKAccountToMapBKAssetToHolding;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
