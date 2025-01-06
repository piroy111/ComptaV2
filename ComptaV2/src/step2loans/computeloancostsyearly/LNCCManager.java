package step2loans.computeloancostsyearly;

import step1loadtransactions.transactions.BKTransactionManager;

public class LNCCManager {

	public LNCCManager(BKTransactionManager _sBKTransactionManager) {
		pBKTransactionManager = _sBKTransactionManager;
		/*
		 * 
		 */
		pLNCCDeleteCurrentFile = new LNCCDeleteCurrentFile(this);
		pLNCCComputeLoanCosts = new LNCCComputeLoanCosts(this);
	}
	
	/*
	 * Data
	 */
	private BKTransactionManager pBKTransactionManager;
	private LNCCComputeLoanCosts pLNCCComputeLoanCosts;
	private LNCCDeleteCurrentFile pLNCCDeleteCurrentFile;

	/**
	 * 
	 */
	public final void initiate() {
		pLNCCDeleteCurrentFile.run();
	}
	
	/**
	 * 
	 */
	public final void run() {
		pLNCCComputeLoanCosts.run();
		pLNCCDeleteCurrentFile.writeFiles();
	}
	
	/*
	 * Getters & Setters
	 */
	public final BKTransactionManager getpBKTransactionManager() {
		return pBKTransactionManager;
	}
	public final LNCCComputeLoanCosts getpLNCCComputeLoanCosts() {
		return pLNCCComputeLoanCosts;
	}
	public final LNCCDeleteCurrentFile getpLNCCDeleteCurrentFile() {
		return pLNCCDeleteCurrentFile;
	}

}
