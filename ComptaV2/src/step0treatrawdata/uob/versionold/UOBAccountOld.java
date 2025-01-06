package step0treatrawdata.uob.versionold;

public class UOBAccountOld {

	protected UOBAccountOld(UOBManagerOld _sUOBManager, String _sCurrency, long _sNumber) {
		mUOBManager = _sUOBManager;
		mCurrency = _sCurrency;
		mNumber = _sNumber;
	}
	
	/*
	 * Data
	 */
	private String mCurrency;
	private long mNumber;
	private UOBManagerOld mUOBManager;
	

	
	/*
	 * Getters & Setters
	 */
	public final String getmCurrency() {
		return mCurrency;
	}
	public final long getmNumber() {
		return mNumber;
	}
	public final UOBManagerOld getmUOBManager() {
		return mUOBManager;
	}
	
}
