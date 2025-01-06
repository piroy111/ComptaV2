package step3statements.statements.main;

import basicmethods.BasicPrintMsg;
import step0treatrawdata.clientsrefinersbrokers.TREATManager;
import step2loans.createloanstransactions.LNManager;
import step2storage.SGManager;
import step3statements.reports.manager.STReportManager;
import step3statements.statements.abstractstatements.STAll;
import step3statements.statements.abstractstatements.STBKAccounts;
import step3statements.statements.abstractstatements.STIncome;
import step5fiscalyearend.FYMain;
import uob.UOBMainManager;
import uob.staticdata.UOBStatic.UOB_DISPLAY;

public class STMain {

	public static void main(String[] _sArgs) {
		new STMain().run();
	}
	
	/**
	 * Write output files
	 */
	public final void run() {
		/*
		 * Initiate
		 */
		new TREATManager(true).run();
		LNManager lLNManager = new LNManager();
		lLNManager.run(true);
		new SGManager(lLNManager.getpBKTransactionManager()).run();
		/*
		 * Com
		 */
		BasicPrintMsg.displayTitle(this, " write various statements");
		/*
		 * All transaction
		 */
		new STAll(lLNManager).run();
		/*
		 * Income
		 */
		System.out.println();
		new STIncome(lLNManager).run();
		/*
		 * BKAssets
		 */
		System.out.println();
		new STBKAccounts(lLNManager).run();
		/*
		 * Fiscal year end
		 */
		System.out.println();
		new FYMain(lLNManager, new UOBMainManager(UOB_DISPLAY.Off)).run();
		/*
		 * Reports
		 */
		new STReportManager(lLNManager).run();
	}
	
	
	
}

