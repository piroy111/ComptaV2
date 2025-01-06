package step5fiscalyearend.incomestatement.loadfilesprevious;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;

public class INFileFY {

	protected INFileFY(int _sDateFY, INLoadFilesPreviousFY _sINLoadFilesPreviousFY) {
		pDateFY = _sDateFY;
		pINLoadFilesPreviousFY = _sINLoadFilesPreviousFY;
		/*
		 * 
		 */
		pMapBKIncomeStrToValueUSD = new HashMap<>();		
	}

	/*
	 * Data
	 */
	private int pDateFY;
	private LitUnFichierEnLignes pReadFile;
	private INLoadFilesPreviousFY pINLoadFilesPreviousFY;
	private Map<String, Double> pMapBKIncomeStrToValueUSD;

	/**
	 * 
	 * @param _sReadFile
	 */
	protected final void fillData(LitUnFichierEnLignes _sReadFile) {
		pReadFile = _sReadFile;
		for (List<String> lLineStr : pReadFile.getmContenuFichierListe()) {
			if (lLineStr.size() > 2) {
				/*
				 * Load line
				 */
				String lBKIncome = lLineStr.get(1);
				double lValueUSD = BasicString.getDouble(lLineStr.get(2));
				/*
				 * Put in Map
				 */
				pMapBKIncomeStrToValueUSD.put(lBKIncome, lValueUSD);
			}
		}
	}


	/*
	 * Getters & Setters
	 */
	public final int getpDateFY() {
		return pDateFY;
	}
	public final LitUnFichierEnLignes getpReadFile() {
		return pReadFile;
	}
	public final INLoadFilesPreviousFY getpINLoadFilesPreviousFY() {
		return pINLoadFilesPreviousFY;
	}
	public final Map<String, Double> getpMapBKIncomeStrToValueUSD() {
		return pMapBKIncomeStrToValueUSD;
	}

}
