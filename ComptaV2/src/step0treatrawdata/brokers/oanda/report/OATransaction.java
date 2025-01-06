package step0treatrawdata.brokers.oanda.report;

import step0treatrawdata.objects.BKAsset;

public class OATransaction implements Comparable<OATransaction> {

	protected OATransaction(String _sId,
			String _sComment, 
			BKAsset _sBKAsset, 
			double _sQuantity,
			int _sDate,
			double _sPrice,
			boolean _sIsTradeOrCashFlow,
			String _sSymbolOANDA,
			double _sQauntityOANDA) {
		pId = _sId;
		pComment = _sComment;
		pBKAsset = _sBKAsset;
		pQuantity = _sQuantity;
		pDate = _sDate;
		pPrice = _sPrice;
		pIsTradeOrCashFlow = _sIsTradeOrCashFlow;
		pSymbolOANDA = _sSymbolOANDA;
		pQuantityOANDA = _sQauntityOANDA;
	}

	/*
	 * Data
	 */
	private String pId;
	private String pComment;
	private BKAsset pBKAsset;
	private double pQuantity;
	private int pDate;
	private double pPrice;
	private boolean pIsTradeOrCashFlow;
	private String pSymbolOANDA;
	private double pQuantityOANDA;
	
	@Override public int compareTo(OATransaction _sOATransaction) {
		return Integer.compare(pDate, _sOATransaction.getpDate());
	}

	/**
	 * Classic toString
	 */
	public String toString() {
		return "Date= " + pDate
				+ "; Id= " + pId
				+ "; pBKAsset= " + pBKAsset
				+ "; pQuantity= " + pQuantity
				+ "; pPrice= " + pPrice;
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpComment() {
		return pComment;
	}
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final double getpQuantity() {
		return pQuantity;
	}
	public final int getpDate() {
		return pDate;
	}
	public final double getpPrice() {
		return pPrice;
	}
	public final boolean getpIsTradeOrCashFlow() {
		return pIsTradeOrCashFlow;
	}

	public final String getpId() {
		return pId;
	}

	public final String getpSymbolOANDA() {
		return pSymbolOANDA;
	}

	public final double getpQuantityOANDA() {
		return pQuantityOANDA;
	}
	
}
