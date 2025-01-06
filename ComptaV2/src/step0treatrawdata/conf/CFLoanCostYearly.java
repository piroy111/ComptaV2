package step0treatrawdata.conf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticDir;
import staticdata.StaticNames;

public class CFLoanCostYearly {

	/*
	 * Data static
	 */
	private static Map<Integer, Map<String, Double>> pMapDateToMapMetalToCostLoanYearly = new HashMap<Integer, Map<String, Double>>();
	private static List<Integer> pListDateMax = new ArrayList<Integer>();
	/*
	 * Data
	 */
	private static boolean pIsLoaded;
	
	/**
	 * Load the conf file into the map
	 */
	private static void readFileConf() {
		if (pIsLoaded) {
			return;
		} else {
			pIsLoaded = true;
		}
		/*
		 * Load file
		 */
		LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(StaticDir.getCONF_COMPTA(), 
				StaticNames.getCONF_COST_LOAN_YEALRY(), true);
		for (List<String> lLineStr : lReadFile.getmContenuFichierListe()) {
			/*
			 * Load
			 */
			int lIdx = -1;
			int lDateMax = BasicString.getInt(lLineStr.get(++lIdx));
			double lGainSilver = BasicString.getDouble(lLineStr.get(++lIdx));
			double lGainGold = BasicString.getDouble(lLineStr.get(++lIdx));
			double lGainPlatinum = BasicString.getDouble(lLineStr.get(++lIdx));
			/*
			 * Fill Map
			 */
			if (lDateMax == -1) {
				lDateMax = BasicDateInt.getmToday();
			}
			pListDateMax.add(lDateMax);
			Map<String, Double> lMapMetalToCostLoan = new HashMap<String, Double>();
			pMapDateToMapMetalToCostLoanYearly.put(lDateMax, lMapMetalToCostLoan);
			lMapMetalToCostLoan.put("SILVER LOAN OZ", lGainSilver);
			lMapMetalToCostLoan.put("GOLD LOAN OZ", lGainGold);
			lMapMetalToCostLoan.put("PLATINUM LOAN OZ", lGainPlatinum);
		}
		/*
		 * Sort list of date
		 */
		Collections.sort(pListDateMax);
	}
	
	/**
	 * give the cost loan in % of the amount (1%, etc.)
	 * @param _sMetal = "GOLD", "SILVER", "PLATINUM"
	 * @param _sDate : date at which we need to know the cost loan
	 * @return
	 */
	public static final double getpCostLoan(String _sMetal, int _sDate) {
		readFileConf();
		/*
		 * Find date max
		 */
		int lIdx = 0;
		while (lIdx < pListDateMax.size() 
				&& _sDate > pListDateMax.get(lIdx)) {
			lIdx++;
		}
		int lDateMax = pListDateMax.get(lIdx);
		/*
		 * Find cost of loan
		 */
		double lCostLoan = pMapDateToMapMetalToCostLoanYearly.get(lDateMax).get(_sMetal);
		return lCostLoan;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
