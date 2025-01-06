package step0treatrawdata.brokers.interactivebrokers.data;

import basicmethods.AMNumberTools;

public class IBDataClosePrice {

	protected IBDataClosePrice(String _sSymbol) {
		pSymbol = _sSymbol;
		/*
		 * 
		 */
		pDate = -1;
		pValue = Double.NaN;
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private String pSymbol;
	private double pValue;
	
	/**
	 * 
	 * @param pValue
	 */
	public final void setpValue(double _sValue) {
		if (!AMNumberTools.isNaNOrZero(_sValue)) {
			pValue = _sValue;
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final double getpValue() {
		return pValue;
	}
	public final int getpDate() {
		return pDate;
	}
	public final String getpSymbol() {
		return pSymbol;
	}

	public final void setpDate(int pDate) {
		this.pDate = pDate;
	}
	
	
	
}
