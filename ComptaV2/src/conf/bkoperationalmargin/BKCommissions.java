package conf.bkoperationalmargin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;

public class BKCommissions {

	public static void main(String[] _sArgs) {
		System.out.println(getpCommission(BKAssetManager.getpAndCheckBKAsset("XAG"), 20171231));
		System.out.println(getpCommission(BKAssetManager.getpAndCheckBKAsset("XAU"), BasicDateInt.getmToday()));
	}
	
	
	/*
	 * Data
	 */
	private static Map<BKAsset, List<BKBeaconOpMargin>> pMapBKAssetToListBKBeaconOpMargin;
	
	/**
	 * Load pMapBKAssetToMapDateToOpMargin
	 */
	private static void loadFileCsv() {
		if (pMapBKAssetToListBKBeaconOpMargin == null) {
			pMapBKAssetToListBKBeaconOpMargin = new HashMap<BKAsset, List<BKBeaconOpMargin>>();
			/*
			 * Read File
			 */
			String lDir = StaticDir.getCONF_COMPTA();
			String lNameFile = StaticNames.getCONF_OPERATIONAL_MARGIN();
			LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(lDir, lNameFile, true);
			/*
			 * Fill Map
			 */
			for (List<String> lLineStr : lReadFile.getmContenuFichierListe()) {
				/*
				 * Load
				 */
				int lIdx = -1;
				int lDate = BasicString.getInt(lLineStr.get(++lIdx));
				String lNameMetal = lLineStr.get(++lIdx);
				double lOpMargin = BasicString.getDouble(lLineStr.get(++lIdx));
				/*
				 * Fill
				 */
				if (lDate == -1) {
					lDate = BasicDateInt.getmToday();
				}
				BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lNameMetal);
				List<BKBeaconOpMargin> lListBKBeaconOpMargin = pMapBKAssetToListBKBeaconOpMargin.get(lBKAsset);
				if (lListBKBeaconOpMargin == null) {
					lListBKBeaconOpMargin = new ArrayList<BKBeaconOpMargin>();
					pMapBKAssetToListBKBeaconOpMargin.put(lBKAsset, lListBKBeaconOpMargin);
				}
				lListBKBeaconOpMargin.add(new BKBeaconOpMargin(lDate, lOpMargin));
			}
			/*
			 * Sort the list in ascending orders of the dates
			 */
			for (BKAsset lBKAsset : pMapBKAssetToListBKBeaconOpMargin.keySet()) {
				Collections.sort(pMapBKAssetToListBKBeaconOpMargin.get(lBKAsset));
			}
		}
	}

	/**
	 * 
	 * @param _sBKAsset
	 * @param _sDate
	 * @return
	 */
	public final static double getpCommission(BKAsset _sBKAsset, int _sDate) {
		loadFileCsv();
		/*
		 * Find BKAsset
		 */
		List<BKBeaconOpMargin> lListBKBeaconOpMargin = pMapBKAssetToListBKBeaconOpMargin.get(_sBKAsset);
		if (lListBKBeaconOpMargin == null || lListBKBeaconOpMargin.size() == 0) {
			BasicPrintMsg.error("The BKAsset is not in the conf file; BKAsset= " + _sBKAsset
					+ "; Conf file= " + StaticDir.getCONF_COMPTA() + StaticNames.getCONF_OPERATIONAL_MARGIN());
		}
		/*
		 * Find OpMargin
		 */
		int lIdx = 0;
		while (lListBKBeaconOpMargin.get(lIdx).getpDate() < _sDate) {
			lIdx++;
		}
		return lListBKBeaconOpMargin.get(lIdx).getpOpMargin();
	}
	
	
	
	
}
