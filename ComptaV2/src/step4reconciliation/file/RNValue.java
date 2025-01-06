package step4reconciliation.file;

public class RNValue implements Comparable<RNValue> {

	protected RNValue(Double _sValue, int _sDate, int _sDateSort, long _sTimeSort) {
		mValue = _sValue;
		mDateSort = _sDateSort;
		mTimeSort = _sTimeSort;
	}
	
	/*
	 * Data
	 */
	private Double mValue;
	private int mDate;
	private int mDateSort;
	private long mTimeSort;
	
	
	/**
	 * Sort 
	 * @param _sRNValue
	 */
	@Override public int compareTo(RNValue _sRNValue) {
		int lCompareDate = Integer.compare(mDateSort, _sRNValue.mDateSort);
		if (lCompareDate == 0) {
			return Long.compare(mTimeSort, _sRNValue.mTimeSort);
		} else {
			return lCompareDate;
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final Double getmValue() {
		return mValue;
	}
	public final int getmDate() {
		return mDate;
	}
	
	
}
