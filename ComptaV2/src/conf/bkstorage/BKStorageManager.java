package conf.bkstorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticDir;
import staticdata.StaticNames;

public class BKStorageManager {




	/*
	 * Data
	 */
	private static Map<String, Map<String, TreeMap<Integer, Double>>> pMapAccounToMapMetalToMapDateToStorageCost;

	/**
	 * Get the value of the storage cost from the Map
	 * @param _sAccount
	 * @param _sMetal
	 * @param _sDate
	 */
	public final static double getmStorageCost(String _sAccount, 
			String _sMetal, int _sDate) {
		loadFile();
		if (!pMapAccounToMapMetalToMapDateToStorageCost.containsKey(_sAccount)) {
			_sAccount = "";
		}
		Map<String, TreeMap<Integer, Double>> lMapMetalToMapDateToStorageCost = pMapAccounToMapMetalToMapDateToStorageCost.get(_sAccount);
		if (!lMapMetalToMapDateToStorageCost.containsKey(_sMetal)) {
			BasicPrintMsg.error("The conf file must contain the metal name as a column"
					+ "\nMetal name missing= " + _sMetal
					+ "\nFile conf= " + StaticDir.getCONF_COMPTA()
					+ StaticNames.getCONF_STORAGE());
		}
		TreeMap<Integer, Double> lMapDateToStorageCost = lMapMetalToMapDateToStorageCost.get(_sMetal);
		/*
		 * Case the date is under the minimum date, then we take the first date 
		 */
		if (_sDate <= lMapDateToStorageCost.firstKey()) {
			_sDate = lMapDateToStorageCost.firstKey();
		} 
		/*
		 * Case the date is above the last date, then we switch to the default account ""
		 */
		else if (_sDate > lMapDateToStorageCost.lastKey()) {
			if (_sAccount.equals("")) {
				_sDate = lMapDateToStorageCost.lastKey();
			} else {
				return getmStorageCost("", _sMetal, _sDate);
			}
		}
		/*
		 * Else find the key just above
		 */
		else {
			_sDate = lMapDateToStorageCost.ceilingKey(_sDate);
		}
		/*
		 * Find the storage cost
		 */
		return lMapDateToStorageCost.get(_sDate);
	}

	/**
	 * Load file if not loaded
	 */
	private static void loadFile() {
		if (pMapAccounToMapMetalToMapDateToStorageCost == null) {
			pMapAccounToMapMetalToMapDateToStorageCost = new HashMap<String, Map<String, TreeMap<Integer, Double>>>();
			/*
			 * ReadFile
			 */
			String lDir = StaticDir.getCONF_COMPTA();
			String lFileName = StaticNames.getCONF_STORAGE();
			LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(lDir, lFileName, true);
			/*
			 * Header
			 */
			List<String> lListMetal = new ArrayList<String>(lReadFile.getmHeadersAndCommentList().get(0));
			lListMetal.remove(0);
			lListMetal.remove(0);
			/*
			 * Fill Map from the file
			 */
			for (List<String> lListLine : lReadFile.getmContenuFichierListe()) {
				int lIdx = -1;
				/*
				 * Account
				 */
				String lAccount = lListLine.get(++lIdx);
				Map<String, TreeMap<Integer, Double>> lMapMetalToMapDateToStorageCost = pMapAccounToMapMetalToMapDateToStorageCost.get(lAccount);
				if (lMapMetalToMapDateToStorageCost == null) {
					lMapMetalToMapDateToStorageCost = new HashMap<>();
					pMapAccounToMapMetalToMapDateToStorageCost.put(lAccount, lMapMetalToMapDateToStorageCost);
				}
				/*
				 * Date
				 */
				int lDate = BasicString.getInt(lListLine.get(++lIdx));
				if (lDate == 0) {
					lDate = BasicDateInt.getmToday();
				}
				/*
				 * Metals & Cost of storage
				 */
				for (int lKdx = 0; lKdx < lListMetal.size(); lKdx++) {
					String lMetal = lListMetal.get(lKdx);
					TreeMap<Integer, Double> lMapDateToStorage = lMapMetalToMapDateToStorageCost.get(lMetal);
					if (lMapDateToStorage == null) {
						lMapDateToStorage = new TreeMap<>();
						lMapMetalToMapDateToStorageCost.put(lMetal, lMapDateToStorage);
					}
					Double lStorageCost = BasicString.getDouble(lListLine.get(++lIdx));
					lMapDateToStorage.put(lDate, lStorageCost);
				}
			}
		}
	}

















}
