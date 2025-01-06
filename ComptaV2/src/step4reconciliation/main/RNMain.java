package step4reconciliation.main;

import java.util.ArrayList;
import java.util.List;

import step4reconciliation.reconciliators.RNUOBAccountV2;
import uob.UOBMainManager;
import uob.staticdata.UOBStatic.UOB_DISPLAY;

public class RNMain {

	public static void main(String[] _sArgs) {
		new RNMain().run();
	}
	
	/*
	 * Data
	 */
	private UOBMainManager mUOBMainManager;
	private List<RNReconciliatorRoot> mListRNReconciliatorRoot;
	private RNUOBAccountV2 mRNUOBAccountV2;
	
	/**
	 * Write output files
	 */
	public final void run() {
		/*
		 * Instantiate
		 */
		mUOBMainManager = new UOBMainManager(UOB_DISPLAY.Off);
		mListRNReconciliatorRoot = new ArrayList<RNReconciliatorRoot>();
		instantiateReconciliator();
		/*
		 * Initiate
		 */
		mUOBMainManager.run();		
		/*
		 * Run RECONCILIATIORS
		 */
		for (RNReconciliatorRoot lRNReconciliatorRoot : mListRNReconciliatorRoot) {
			lRNReconciliatorRoot.run();
		}
		mRNUOBAccountV2.run();
	}
	
	private void instantiateReconciliator() {
		mRNUOBAccountV2 = new RNUOBAccountV2(this);
	}

	/*
	 * Getters & Setters
	 */
	public final UOBMainManager getmUOBMainManager() {
		return mUOBMainManager;
	}
	
}
