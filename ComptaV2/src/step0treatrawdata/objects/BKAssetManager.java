package step0treatrawdata.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticDir;
import staticdata.StaticNames;

public class BKAssetManager {

	
	/*
	 * Data static
	 */
	private static Map<String, BKAsset> pMapNameToBKAsset = new HashMap<String, BKAsset>();
	private static List<BKAsset> pListBKAsset = new ArrayList<BKAsset>();
	private static BKAsset pBKAssetCurrencyReference;
	private static List<BKAsset> pListCurrency = new ArrayList<BKAsset>();
	private static List<Integer> pListDatePriceChange = new ArrayList<Integer>();
	private static List<BKAsset> pListBKBar = new ArrayList<>();
	/*
	 * Data
	 */
	private static boolean pIsLoaded = false;
	
	/**
	 * Create all the possible BKAsset from the header of the CSV file price HISTO<br>
	 * Load the historical prices in a map in all BKAssets<br>
	 * Fill the missing date with the continuity rule<br>
	 */
	public final static void loadFileHisto() {
		if (pIsLoaded) {
			return;
		} else {
			pIsLoaded = true;
		}
		/*
		 * Load from file
		 */
		String lDir = StaticDir.getPRICES_HISTO();
		String lNameFile = StaticNames.getPRICES_HISTO();
		LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(lDir,  lNameFile, true);
		/*
		 * Identify Assets
		 */
		for (int lIdx = 1; lIdx < lReadFile.getmListColumnNames().size(); lIdx++) {
			String lAssetName = lReadFile.getmListColumnNames().get(lIdx);
			BKAsset lBKAsset = new BKAsset(lAssetName);
			pMapNameToBKAsset.put(lAssetName, lBKAsset);
			pListBKAsset.add(lBKAsset);
			/*
			 * Case of currency reference
			 */
			if (lBKAsset.getpName().equals(StaticNames.getCURRENCY_REFERENCE())) {
				pBKAssetCurrencyReference = lBKAsset;
			}
		}
		/*
		 * Fill HISTO
		 */
		for (List<String> lLineStr : lReadFile.getmContenuFichierListe()) {
			int lDate = BasicString.getInt(lLineStr.get(0));
			for (int lIdx = 1; lIdx < lLineStr.size(); lIdx++) {
				BKAsset lBKAsset = pListBKAsset.get(lIdx - 1);
				double lPrice = BasicString.getDouble(lLineStr.get(lIdx));
				lBKAsset.loadPriceHisto(lDate, lPrice);
			}
			pListDatePriceChange.add(lDate);
		}
		/*
		 * Fill the missing dates
		 */
		for (BKAsset lBKAsset : pMapNameToBKAsset.values()) {
			lBKAsset.fillVoidInMap();
		}
		/*
		 * Create the BAR LOAN
		 */
		List<BKAsset> lListBKAssetLoan = new ArrayList<BKAsset>();
		for (BKAsset lBKAsset : pListBKAsset) {
			if (lBKAsset.getpName().contains("BAR OZ")) {
				String lNameLoan = lBKAsset.getpNameLoanFromBar();
				BKAsset lBKAssetLoan = new BKAsset(lNameLoan, lBKAsset);
				lListBKAssetLoan.add(lBKAssetLoan);
			}
		}
		for (BKAsset lBKAssetLoan : lListBKAssetLoan) {
			pListBKAsset.add(lBKAssetLoan);
			pMapNameToBKAsset.put(lBKAssetLoan.getpName(), lBKAssetLoan);
		}
		/*
		 * Create the future on currency: X...
		 */
		List<BKAsset> lListBKAsset = new ArrayList<BKAsset>(pListBKAsset);
		for (BKAsset lBKAsset : lListBKAsset) {
			if (lBKAsset.getpIsCurrency() && !lBKAsset.getpName().equals("USD")) {
				/*
				 * Create the BKAsset
				 */
				BKAsset lBKAssetX = new BKAsset("X" + lBKAsset.getpName());
				pMapNameToBKAsset.put(lBKAssetX.getpName(), lBKAssetX);
				pListBKAsset.add(lBKAssetX);
				/*
				 * Copy the prices
				 */
				for (int lDate : lBKAsset.getpMapDateToPrice().keySet()) {
					lBKAssetX.loadPriceHisto(lDate, lBKAsset.getpMapDateToPrice().get(lDate));
				}
			}
		}
		/*
		 * Fill the list of Currency
		 */
		pListCurrency = new ArrayList<BKAsset>();
		for (BKAsset lBKAsset : pListBKAsset) {
			if (lBKAsset.getpIsCurrency()) {
				pListCurrency.add(lBKAsset);
			}
		}
		/*
		 * Fill the list of bars (non loan)
		 */
		pListBKBar = new ArrayList<BKAsset>();
		for (BKAsset lBKAsset : pListBKAsset) {
			if (lBKAsset.getpIsBar() && ! lBKAsset.getpIsBarLoan()) {
				pListBKBar.add(lBKAsset);
			}
		}
	}
	
	/**
	 * get the BKAsset but kill the program if the BKAsset does not exist<br>
	 * @param _sName
	 * @return
	 */
	public static final BKAsset getpAndCheckBKAsset(String _sName) {
		loadFileHisto();
		BKAsset lBKAsset = pMapNameToBKAsset.get(_sName);
		if (lBKAsset == null) {
			BasicPrintMsg.error("The BKAsset does not exist in the Map; Name= " + _sName);
		}
		return lBKAsset;
	}
	
	/**
	 * 
	 * @param _sDate : date at which we take the forex rate
	 * @param _sForex : in the form of YYYXXX which means that 1 YYY = x% XXXX
	 * @return
	 */
	public static double getpForex(int _sDate, String _sForex) {
		loadFileHisto();
		if (_sForex.length() != 6) {
			BasicPrintMsg.error("The forex must be in the form of 'YYYXXX' like 'USDSGD';Forex = " + _sForex);
		}
		String lCurrency1 = _sForex.substring(0, 3);
		String lCurrency2 = _sForex.substring(3, 6);
		BKAsset lBKAsset1 = pMapNameToBKAsset.get(lCurrency1);
		BKAsset lBKAsset2 = pMapNameToBKAsset.get(lCurrency2);
		double lForex = lBKAsset1.getpMapDateToPrice().get(_sDate)
				/ lBKAsset2.getpMapDateToPrice().get(_sDate);
		return lForex;
	}

	/**
	 * We divide by the result to be in currency of reference
	 * @param _sDate : date at which we take the forex rate
	 * @param _sForex : in the form of YYY
	 * @return
	 */
	public static double getpForexReference(int _sDate, String _sForex) {
		loadFileHisto();
		String lForex = pBKAssetCurrencyReference.getpName() + _sForex;
		return getpForex(_sDate, lForex);
	}

	/**
	 * @return the BKAsset currency matching the name, or null if there is no BKAsset currency of that name
	 * @param _sCurrency
	 */
	public static final BKAsset getpBKCurrency(String _sCurrency) {
		for (BKAsset lBKAsset : pListCurrency) {
			if (lBKAsset.getpName().equals(_sCurrency)) {
				return lBKAsset;
			}
		}
		return null;
	}
	
	/*
	 * Getters & Setters
	 */
	public static final Map<String, BKAsset> getpMapNameToBKAsset() {
		loadFileHisto();
		return pMapNameToBKAsset;
	}
	public static final List<BKAsset> getpListBKAsset() {
		loadFileHisto();
		return pListBKAsset;
	}
	public static final BKAsset getpBKAssetCurrencyReference() {
		loadFileHisto();
		return pBKAssetCurrencyReference;
	}
	public static final List<BKAsset> getpListCurrency() {
		loadFileHisto();
		return pListCurrency;
	}
	public static final List<Integer> getpListDatePriceChange() {
		loadFileHisto();
		return pListDatePriceChange;
	}
	public static final List<BKAsset> getpListBKBar() {
		return pListBKBar;
	}
	
	
	
	
	
	
	
	
	
}
