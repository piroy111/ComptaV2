package conf.bkoperationalmargin;

public class BKBeaconOpMargin implements Comparable<BKBeaconOpMargin> {

	protected BKBeaconOpMargin(int _sDate, double _sOpMargin) {
		pDate = _sDate;
		pOpMargin = _sOpMargin;
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private double pOpMargin;
	
	
	@Override public int compareTo(BKBeaconOpMargin _sBKBeacon) {
		return Integer.compare(pDate, _sBKBeacon.pDate);
	}

	/*
	 * Getters & Setter
	 */
	public final int getpDate() {
		return pDate;
	}
	public final double getpOpMargin() {
		return pOpMargin;
	}
	
}
