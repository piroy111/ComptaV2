package step5fiscalyearend.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticDir;
import staticdata.StaticNames;

public class FYConfManager {

	/*
	 * Data
	 */
	private static Map<String, String> pMapBKIncomeToCategory;
	private static List<String> pListBKIncomeMissing;

	/**
	 * Load the conf file
	 */
	private static void loadFile() {
		if (pMapBKIncomeToCategory == null) {
			pMapBKIncomeToCategory = new HashMap<String, String>();
			pListBKIncomeMissing = new ArrayList<String>();
			String lDir = StaticDir.getCONF_COMPTA();
			String lNameFile = StaticNames.getCONF_FY_INCOME();
			LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(lDir, lNameFile, true);
			for (List<String> lLineStr : lReadFile.getmContenuFichierListe()) {
				int lIdx = -1;
				String lBKIncome = lLineStr.get(++lIdx);
				String lCategory = lLineStr.get(++lIdx);
				pMapBKIncomeToCategory.put(lBKIncome, lCategory);
			}
		}
	}

	/**
	 * Get the category matching the BKIncome and fill the missing list if the category is missing
	 * @param _sBKIncome
	 * @return
	 */
	public static final String getpCategory(String _sBKIncome) {
		loadFile();
		String lCategory = null;
		for (String lBKIncome : pMapBKIncomeToCategory.keySet()) {
			/*
			 * Check for Category
			 */
			if (_sBKIncome.startsWith(lBKIncome)) {
				if (lCategory != null) {
					BasicPrintMsg.error("The BKIncome can match 2 categories"
							+ "; BKIncome= " + _sBKIncome
							+ "\nCategory1= " + lCategory
							+ "\nCategory2= " + pMapBKIncomeToCategory.get(lBKIncome)
							+ "\nConf file= " + StaticDir.getCONF_COMPTA() + StaticNames.getCONF_FY_INCOME());
				} else {
					lCategory = pMapBKIncomeToCategory.get(lBKIncome);
				}
			}
		}
		/*
		 * Case the Category is found --> we return it
		 */
		if (lCategory != null) {
			return lCategory;
		}
		/*
		 * No Category --> we add the list of missing
		 */
		if (!pListBKIncomeMissing.contains(_sBKIncome)) {
			pListBKIncomeMissing.add(_sBKIncome);
		}
		return "";
	}

	/**
	 * Check for missing categories
	 */
	public final static void checkMissing() {
		loadFile();
		if (pListBKIncomeMissing.size() > 0) {
			BasicPrintMsg.error("Some BKIncome don't match any Category"
					+ ". You must add the following BKIncome with their relevant categories in the conf file."
					+ "\nList of BKIncome with unknown categories= " + pListBKIncomeMissing.toString()
					+ "\nConf file= " + StaticDir.getCONF_COMPTA() + StaticNames.getCONF_FY_INCOME());
		}
	}
	
	
	/*
	 * Getters & Setters
	 */
	public static final Map<String, String> getpMapBKIncomeToCategory() {
		loadFile();
		return pMapBKIncomeToCategory;
	}



}
