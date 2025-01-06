package step0treatrawdata.brokers.interactivebrokers;

import basicmethods.BasicPrintMsg;
import step0treatrawdata.brokers.interactivebrokers.files.IBFileManager;
import step0treatrawdata.brokers.interactivebrokers.reports.IBReportManager;
import step0treatrawdata.brokers.interactivebrokers.transactions.IBTransactionManager;
import step0treatrawdata.brokers.interactivebrokers.writefile.IBWriteFileManager;

public class IBManager {

	
	public static void main(String[] _sArray) {
		new IBManager().run();
	}
	
	public IBManager() {
		instantiate();
	}
	
	
	/*
	 * Data
	 */
	private IBFileManager pIBFileManager;
	private IBReportManager pIBReportManager;
	private IBTransactionManager pIBTransactionManager;
	private IBWriteFileManager pIBWriteFileManager;
	
	/**
	 * Classic
	 */
	public final void instantiate() {
		pIBFileManager = new IBFileManager(this);
		pIBReportManager = new IBReportManager(this);
		pIBTransactionManager = new IBTransactionManager(this);
		pIBWriteFileManager = new IBWriteFileManager(this);
	}

	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Read the reports of InteractiveBrokers and create file of latest transactions");
		runForStandAlone();
		pIBWriteFileManager.write();
	}
	
	/**
	 * Load the IBTransaction without writing a file treated
	 */
	public final void runForStandAlone() {
		pIBFileManager.checkFiles();
		pIBReportManager.run();
		pIBTransactionManager.createIBTransactions();
	}
	
	/*
	 * Getters & Setters
	 */
	public final IBFileManager getpIBFileManager() {
		return pIBFileManager;
	}
	public final IBReportManager getpIBReportManager() {
		return pIBReportManager;
	}
	public final IBTransactionManager getpIBTransactionManager() {
		return pIBTransactionManager;
	}
	public final IBWriteFileManager getpIBWriteFileManager() {
		return pIBWriteFileManager;
	}


	
	
	
	
}
