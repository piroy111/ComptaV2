package step1loadtransactions.storage;

import java.util.HashMap;
import java.util.Map;

import basicmethods.BasicString;
import basicmethods.ReadFileInLinesWithMap;
import staticdata.StaticDir;
import staticdata.StaticNames;

/**
 * @deprecated
 */
public class BKStorageManager {

	
	private static ReadFileInLinesWithMap mReadFileConf;
	
	/**
	 * Load file if it has not been loaded yet
	 */
	private static void loadFile() {
		if (mReadFileConf == null) {
			mReadFileConf = new ReadFileInLinesWithMap(StaticDir.getCONF_COMPTA(), StaticNames.getCONF_STORAGE(), true);
		}
	}
	
	/**
	 * @return a Map<Metal Name, Cost of Storage>. Metal Name = GOLD, SIVLER, PLATINUM. Cost of storage = 20%%,50%%,25%%
	 * @param _sAccount
	 * 
	 */
	public final static Map<String, Double> getMapMetalToCost(String _sAccount) {
		loadFile();
		/*
		 * Choose the account or default
		 */
		String lAccount;
		if (mReadFileConf.getmMapHeaderListItem().get("BKAccount").contains(_sAccount)) {
			lAccount = _sAccount;
		} else {
			lAccount = "Default";
		}
		/*
		 * Read the costs of storage
		 */
		Map<String, Double> lMapMetalToCost = new HashMap<String, Double>();
		String lMetal = "GOLD";
		lMapMetalToCost.put(lMetal, BasicString.getDouble(mReadFileConf.getmItem("BKAccount", lMetal, lAccount)));
		lMetal = "SILVER";
		lMapMetalToCost.put(lMetal, BasicString.getDouble(mReadFileConf.getmItem("BKAccount", lMetal, lAccount)));
		lMetal = "PLATINUM";
		lMapMetalToCost.put(lMetal, BasicString.getDouble(mReadFileConf.getmItem("BKAccount", lMetal, lAccount)));
		/*
		 * Return Map
		 */
		return lMapMetalToCost;
	}
	
	
	
	
	
	
	
}
