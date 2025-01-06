package step4reconciliation.file;

import java.util.HashMap;
import java.util.Map;

public class RNColumnInFile {

	public RNColumnInFile(String _sTitle) {
		mTitle = _sTitle;
		/*
		 * Initiate
		 */
		mDateToRNValue = new HashMap<Integer, RNValue>();
	}
	
	/*
	 * Data
	 */
	private String mTitle;
	private Map<Integer, RNValue> mDateToRNValue;
	
	/**
	 * Add a new value to the date. Take the latest value regarding the time if the date is the same
	 * @param _sDate
	 * @param _sDateSort
	 * @param _sTimeSort
	 * @param _sValue
	 */
	public final void addNewValue(int _sDate, int _sDateSort, long _sTimeSort, Double _sValue) {
		RNValue lRNValueNew = new RNValue(_sValue, _sDate, _sDateSort, _sTimeSort);
		RNValue lRNValue = mDateToRNValue.get(_sDate);
		if (lRNValue != null
				&& lRNValue.compareTo(lRNValueNew) > 0) {
			 lRNValueNew = lRNValue;
		}
		mDateToRNValue.put(_sDate, lRNValueNew);
	}

	/*
	 * Getters & Setters
	 */
	public final String getmTitle() {
		return mTitle;
	}
	public final Map<Integer, RNValue> getmDateToRNValue() {
		return mDateToRNValue;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
