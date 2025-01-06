package step0treatrawdata.brokers.interactivebrokers.data;

public class IBDataNav implements Comparable<IBDataNav> {

	protected IBDataNav(int _sDate) {
		pDate = _sDate;
		/*
		 * Initiate
		 */
		pInterests = 0;
		pNav = 0;
	}

	/*
	 * Data
	 */
	private int pDate;
	private double pInterests;
	private double pNav;
	
	/**
	 * 
	 * @param _sInterests
	 * @param _sNav
	 */
	public final void addNewDatas(double _sInterests, double _sNav) {
		pInterests += _sInterests;
		pNav += _sNav;
	}
	
	@Override public int compareTo(IBDataNav _sIBDataNav) {
		return Integer.compare(pDate, _sIBDataNav.getpDate());
	}
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final double getpInterests() {
		return pInterests;
	}
	public final double getpNav() {
		return pNav;
	}
	public final void setpInterests(double pInterests) {
		this.pInterests = pInterests;
	}
	public final void setpNav(double pNav) {
		this.pNav = pNav;
	}

	

}
