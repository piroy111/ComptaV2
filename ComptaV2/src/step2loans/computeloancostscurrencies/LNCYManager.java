package step2loans.computeloancostscurrencies;

import step1loadtransactions.transactions.BKTransactionManager;

public class LNCYManager {

	public LNCYManager(BKTransactionManager _sBKTransactionManager) {
		pBKTransactionManager = _sBKTransactionManager;
		/*
		 * 
		 */
		pLNCYDeleteCurrentFile = new LNCYDeleteCurrentFile(this);
		pLNCYComputeLoanCosts = new LNCYComputeLoanCosts(this);
	}
	
	/*
	 * Data
	 */
	private BKTransactionManager pBKTransactionManager;
	private LNCYComputeLoanCosts pLNCYComputeLoanCosts;
	private LNCYDeleteCurrentFile pLNCYDeleteCurrentFile;

	/**
	 * 
	 */
	public final void initiate() {
		pLNCYDeleteCurrentFile.run();
	}
	
	/**
	 * 
	 */
	public final void run() {
		pLNCYComputeLoanCosts.run();
		pLNCYDeleteCurrentFile.writeFiles();
	}
	
	/*
	 * Getters & Setters
	 */
	public final BKTransactionManager getpBKTransactionManager() {
		return pBKTransactionManager;
	}
	public final LNCYComputeLoanCosts getpLNCYComputeLoanCosts() {
		return pLNCYComputeLoanCosts;
	}
	public final LNCYDeleteCurrentFile getpLNCYDeleteCurrentFile() {
		return pLNCYDeleteCurrentFile;
	}
	
}
