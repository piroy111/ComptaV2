package step0treatrawdata.brokers.interactivebrokers.data;

import basicmethods.BasicPrintMsg;

public class IBDataTrade {

	protected IBDataTrade(String _sID) {
		pID = _sID;
	}
	
	/*
	 * Data
	 */
	private String pID;
	private String pAssetClass;
	private String pSymbol;
	private int pDate;
	private double pQuantityExec;
	private double pPriceExec;
	private double pCommissions;
	private String pCommissionsCurrency;
	
	/**
	 * 
	 * @param _sAssetClass
	 * @param _sSymbol
	 * @param _sDate
	 * @param _sQuantityExec
	 * @param _sPriceExec
	 */
	public final void addNewData(String _sAssetClass, String _sSymbol, 
			int _sDate, double _sQuantityExec, double _sPriceExec,
			double _sCommissions, String _sCommissionCurrency) {
		if (pAssetClass != null) {
			BasicPrintMsg.error("Error");
		}
		pAssetClass = _sAssetClass;
		pSymbol = _sSymbol;
		pDate = _sDate;
		pQuantityExec = _sQuantityExec;
		pPriceExec = _sPriceExec;
		pCommissions = _sCommissions;
		pCommissionsCurrency = _sCommissionCurrency;
	}

	/**
	 * 
	 * @param _sIBDataTrade
	 */
	protected final void clone(IBDataTrade _sIBDataTrade) {
		if (pAssetClass != null) {
			BasicPrintMsg.error("Error");
		}
		pAssetClass = _sIBDataTrade.getpAssetClass();
		pSymbol = _sIBDataTrade.getpSymbol();
		pDate = _sIBDataTrade.getpDate();
		pQuantityExec = _sIBDataTrade.getpQuantityExec();
		pPriceExec = _sIBDataTrade.getpPriceExec();
		pCommissions = _sIBDataTrade.getpCommissions();
		pCommissionsCurrency = _sIBDataTrade.getpCommissionsCurrency();
	}
	
	/**
	 * Classic toString
	 */
	public String toString() {
		return "pAssetClass= " + pAssetClass
				+ "; pSymbol= " + pSymbol
				+ "; pDate= " + pDate
				+ "; pQuantityExec= " + pQuantityExec
				+ "; pPriceExec= " + pPriceExec
				+ "; pCommissions= " + pCommissions
				+ "; pCommissionsCurrency= " + pCommissionsCurrency;
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpID() {
		return pID;
	}
	public final String getpAssetClass() {
		return pAssetClass;
	}
	public final String getpSymbol() {
		return pSymbol;
	}
	public final int getpDate() {
		return pDate;
	}
	public final double getpQuantityExec() {
		return pQuantityExec;
	}
	public final double getpPriceExec() {
		return pPriceExec;
	}
	public final double getpCommissions() {
		return pCommissions;
	}
	public final String getpCommissionsCurrency() {
		return pCommissionsCurrency;
	}
	
}
