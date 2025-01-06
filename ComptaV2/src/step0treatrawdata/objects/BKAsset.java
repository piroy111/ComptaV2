package step0treatrawdata.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.StaticAsset;

public class BKAsset implements Comparable<BKAsset> {

	/**
	 * can be a currency (USD, SGD, etc.) or a physical bar, or a paper asset (XAG, XAU, XPT)
	 * @param _sName
	 */
	protected BKAsset(String _sName) {
		pName = _sName;
		/*
		 * Initiate
		 */
		pMapDateToPrice = new HashMap<Integer, Double>();
		pCategoryForSort = getpAndComputeCategory(this);
	}
	
	/**
	 * clone a BKObject
	 * @param _sName
	 * @param _sBKObject
	 */
	protected BKAsset(String _sName, BKAsset _sBKObject) {
		this(_sName);
		pMapDateToPrice = new HashMap<Integer, Double>(_sBKObject.getpMapDateToPrice());
	}
	
	/*
	 * Data
	 */
	private String pName;
	private Map<Integer, Double> pMapDateToPrice;
	private int pCategoryForSort;
	
	/**
	 * load price historical
	 * @param _sDate
	 * @param _sPrice
	 */
	protected final void loadPriceHisto(int _sDate, double _sPrice) {
		pMapDateToPrice.put(_sDate, _sPrice);
	}

	/**
	 * Fill the missing date in the map with the rule of continuity
	 */
	protected final void fillVoidInMap() {
		List<Integer> lListDate = new ArrayList<Integer>(pMapDateToPrice.keySet());
		Collections.sort(lListDate);
		double lPreviousPrice = Double.NaN;
		for (int lDate = lListDate.get(0); lDate <= BasicDateInt.getmToday(); 
				lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
			if (!lListDate.contains(lDate)) {
				pMapDateToPrice.put(lDate, lPreviousPrice);
			} else {
				lPreviousPrice = pMapDateToPrice.get(lDate);
			}
		}
	}
	
	/**
	 * get the price USD for the date
	 * @param _sDate
	 * @return
	 */
	public final double getpPriceUSD(int _sDate) {
		if (!pMapDateToPrice.containsKey(_sDate)) {
			BasicPrintMsg.error("There is no price for the date; Date= " + _sDate + "; BKAsset= " + pName); 
		}
		return pMapDateToPrice.get(_sDate);
	}
	
	/**
	 * Says if the object is a physical bar
	 * @return
	 */
	public final boolean getpIsBar() {
		return pName.contains("BAR OZ");
	}

	/**
	 * 
	 * @return
	 */
	protected final String getpNameLoanFromBar() {
		return pName.replaceAll("BAR", "LOAN");
	}
	
	/**
	 * gives the name of the metal of a bar or a bar loan
	 * @return
	 */
	public final String getpMetalName() {
		if (getpIsBar()) {
			return getpName().split(" ", -1)[0];
		} else if (getpIsBarLoan()) {
			return getpName().split(" ", -1)[0];
		} else {
			BasicPrintMsg.error("This is not a bar nor a bar loan. So there is no metal associated"
					+ "\nBKAsset= " + pName);
			return null;
		}
	}
	
	/**
	 * get the paper name. To be used only if the object is a bar
	 * @return
	 */
	public final BKAsset getpLoanFromBar() {
		if (!getpIsBar()) {
			BasicPrintMsg.error("This is not a bar; this= " + toString());
			return null;
		} else {
			return BKAssetManager.getpAndCheckBKAsset(getpNameLoanFromBar());
		}
	}

	/**
	 * get the paper name. To be used only if the object is a bar
	 * @return
	 */
	public final BKAsset getpBarFromLoan() {
		if (!getpIsBarLoan()) {
			BasicPrintMsg.error("This is not a bar loan; this= " + toString());
			return null;
		} else {
			return BKAssetManager.getpAndCheckBKAsset(pName.replaceAll("LOAN", "BAR"));
		}
	}

	/**
	 * Says if the Asset is a bar loan
	 * @return
	 */
	public final boolean getpIsBarLoan() {
		return pName.contains("LOAN");
	}
	
	/**
	 * Says if the object is a paper (in this case it is a future)
	 * @return
	 */
	public final boolean getpIsPaper() {
		return pName.startsWith("X");
	}
	
	/**
	 * Says if the object is a currency
	 * @return
	 */
	public final boolean getpIsCurrency() {
		return getpIsCurrency(pName);
	}

	/**
	 * Says if the object is a paper (in this case it is a future)
	 * @return
	 */
	public final boolean getpIsPaperCurrency() {
		return pName.startsWith("X")
				&& getpIsCurrency(pName.substring(1));
	}

	
	/**
	 * Says if the object is a currency
	 * @return
	 */
	private final boolean getpIsCurrency(String _sName) {
		return _sName.equals("SGD")
				|| _sName.equals("USD")
				|| _sName.equals("EUR")
				|| _sName.equals("JPY")
				|| _sName.equals("GBP");
	}

	/**
	 * @return true if name = "BCO"
	 */
	public final boolean getpIsOil() {
		return pName.equals(StaticAsset.getOIL());
	}
	
	/**
	 * Classic toString
	 */
	public final String toString() {
		return pName;
	}
	
	/**
	 * Sort comparable
	 */
	@Override public int compareTo(BKAsset _sBKAsset) {
		if (_sBKAsset.pCategoryForSort == pCategoryForSort) {
			return pName.compareTo(_sBKAsset.getpName());
		} else {
			return Integer.compare(pCategoryForSort, _sBKAsset.pCategoryForSort);
		}
	}
	
	/**
	 * For sort comparable
	 * @param _sBKAsset
	 * @return
	 */
	private int getpAndComputeCategory(BKAsset _sBKAsset) {
		if (_sBKAsset.getpIsBar()) {
			return 1;
		} else if (_sBKAsset.getpIsPaper()) {
			if (_sBKAsset.getpIsPaperCurrency()) {
				return 3;
			} else {
				return 2;
			}
		} else {
			return 4;
		}		
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final String getpName() {
		return pName;
	}
	public final Map<Integer, Double> getpMapDateToPrice() {
		return pMapDateToPrice;
	}

	
	
}
