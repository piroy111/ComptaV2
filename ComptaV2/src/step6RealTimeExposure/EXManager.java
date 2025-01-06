package step6RealTimeExposure;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.StaticDate;
import step0treatrawdata.clientsrefinersbrokers.TREATManager;
import step2loans.createloanstransactions.LNManager;
import uob.UOBMainManager;
import uob.staticdata.UOBStatic.UOB_DISPLAY;

public class EXManager {

	
	public final void run() {
		/*
		 *	Change the date max to today
		 */
		StaticDate.setDATE_MAX(BasicDateInt.getmToday());
		/*
		 * Compute all the transactions, except the storage
		 */
		UOBMainManager lUOBManager = new UOBMainManager(UOB_DISPLAY.Off);
		lUOBManager.run();
		new TREATManager(true).run();
		/*
		 * Loan: does not create a file loan, since we are supposed to run it every day
		 */
		LNManager lLNManager = new LNManager();
		lLNManager.run(false);
		/*
		 * Write the file exposure
		 */
		BasicPrintMsg.displayTitle(this, " Write file csv for exposure reports");
		new EXWriteFileExposures(lLNManager.getpBKTransactionManager()).writeFile();
	}
	
	
}
