package step4reconciliation.main;

public abstract class RNReconciliatorRoot {

	public RNReconciliatorRoot(RNMain _sRNMain) {
		mRNMain = _sRNMain;
	}
	
	/*
	 * Abstract
	 */
	public abstract int getmDateMaximum();
	public abstract void run();
	/*
	 * Data
	 */
	protected RNMain mRNMain;
	
}
