package step0treatrawdata.brokers.interactivebrokers.data;

public class IBDataCash {

	protected IBDataCash(String _sSymbol) {
		pSymbol = _sSymbol;
		/*
		 * Initiate
		 */
		pDate = -1;
		pDeposits = 0;
		pBrokerInterests = 0;
		pOtherFees = 0;
	}

	/*
	 * Data
	 */
	private String pSymbol;
	private int pDate;
	private double pDeposits;
	private double pBrokerInterests;
	private double pOtherFees;
	
	/**
	 * 
	 * @param _sInterests
	 * @param _sNav
	 */
	public final void addNewDatas(double _sDeposits, double _sBrokerInterests, double _sOtherFees, 
			double _sSalesTaxe, double _sCFDCharges) {
		pDeposits += _sDeposits;
		pBrokerInterests += _sBrokerInterests;
		pOtherFees += _sOtherFees + _sSalesTaxe + _sCFDCharges;
	}
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final void setpDate(int pDate) {
		this.pDate = pDate;
	}
	public final String getpSymbol() {
		return pSymbol;
	}
	public final double getpDeposits() {
		return pDeposits;
	}
	public final double getpBrokerInterests() {
		return pBrokerInterests;
	}
	public final double getpOtherFees() {
		return pOtherFees;
	}

}
