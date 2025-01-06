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

public class CFLoanCostCurrency {

	/*
	 * Data static
	 */
	private static Map<Integer, Double> pMapDateToCostLoan = new HashMap<Integer, Double>();
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
				StaticNames.getCONF_COST_LOAN_CURRENCY(), true);
		for (List<String> lLineStr : lReadFile.getmContenuFichierListe()) {
			/*
			 * Load
			 */
			int lIdx = -1;
			int lDateMax = BasicString.getInt(lLineStr.get(++lIdx));
			double lCostLoan = BasicString.getDouble(lLineStr.get(++lIdx));
			/*
			 * Fill Map
			 */
			if (lDateMax == -1) {
				lDateMax = BasicDateInt.getmToday();
			}
			pListDateMax.add(lDateMax);
			pMapDateToCostLoan.put(lDateMax, lCostLoan);
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
	public static final double getpCostLoan(int _sDate) {
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
		double lCostLoan = pMapDateToCostLoan.get(lDateMax);
		return lCostLoan;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
