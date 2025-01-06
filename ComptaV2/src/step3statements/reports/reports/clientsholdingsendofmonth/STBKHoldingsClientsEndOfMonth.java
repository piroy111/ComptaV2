package step3statements.reports.reports.clientsholdingsendofmonth;

import basicmethods.BasicPrintMsg;
import step1loadtransactions.transactions.BKTransactionManager;

public class STBKHoldingsClientsEndOfMonth {
	
	public STBKHoldingsClientsEndOfMonth(BKTransactionManager _sBKTransactionManager) {
		pBKTransactionManager = _sBKTransactionManager;
		/*
		 * Data
		 */
		pCHReportManagerFromBKHolder = new CHReportManagerFromBKHolder(this);
		pCHReportManagerFromImport = new CHReportManagerFromImport(this);
	}

	/*
	 * Data
	 */
	private BKTransactionManager pBKTransactionManager;
	private CHReportManagerFromBKHolder pCHReportManagerFromBKHolder;
	private CHReportManagerFromImport pCHReportManagerFromImport;
	
	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Check the BKHoldings are consistent with the website in the past for the clients");
		pCHReportManagerFromImport.loadExistingCHReports();
		pCHReportManagerFromBKHolder.createCHReport();
		pCHReportManagerFromImport.checkCHReport();
		pCHReportManagerFromBKHolder.writeCHReport();
	}
	
	/*
	 * Getters & Setters
	 */
	public final BKTransactionManager getpBKTransactionManager() {
		return pBKTransactionManager;
	}
	public final CHReportManagerFromBKHolder getpCHReportManagerFromBKHolder() {
		return pCHReportManagerFromBKHolder;
	}
	public final CHReportManagerFromImport getpCHReportManagerFromImport() {
		return pCHReportManagerFromImport;
	}
	
}
