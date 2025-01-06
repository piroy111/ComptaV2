package step0treatrawdata.brokers.oanda.reconciliation;

import step0treatrawdata.brokers.oanda.OAManager;

public class OAReconcilationManager {

	public OAReconcilationManager(OAManager _sOAManager) {
		pOAManager = _sOAManager;
	}
	
	/*
	 * Data
	 */
	private OAManager pOAManager;
	private OAFileChecker pOAFileChecker;
	private OAFileReader pOAFileReader;
	private OAPositionsChecker pOAPositionsChecker;
	
	/**
	 * 
	 */
	public final void run() {
		/*
		 * Instantiate
		 */
		pOAFileChecker = new OAFileChecker(this);
		pOAFileReader = new OAFileReader(this);
		pOAPositionsChecker = new OAPositionsChecker(this);
		/*
		 * Initiate
		 */
		pOAFileChecker.initiate();
		pOAFileReader.initiate();
		pOAPositionsChecker.initiate();
		/*
		 * Run
		 */
		pOAFileChecker.run();
		pOAFileReader.run();
		pOAPositionsChecker.run();
	}

	/*
	 * Getters & Setters
	 */
	public final OAManager getpOAManager() {
		return pOAManager;
	}
	public final OAFileChecker getpOAFileChecker() {
		return pOAFileChecker;
	}
	public final OAFileReader getpOAFileReader() {
		return pOAFileReader;
	}
	public final OAPositionsChecker getpOAPositionsChecker() {
		return pOAPositionsChecker;
	}
	
}
