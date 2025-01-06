package step0treatrawdata.brokers.interactivebrokers.writefile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step0treatrawdata.brokers.interactivebrokers.IBManager;
import step0treatrawdata.brokers.interactivebrokers.transactions.IBTransaction;

public class IBWriteFileManager {

	public IBWriteFileManager(IBManager _sIBManager) {
		pIBManager = _sIBManager;
	}

	/*
	 * Data
	 */
	private IBManager pIBManager;

	/**
	 * 
	 */
	public final void write() {
		int lDate = pIBManager.getpIBTransactionManager().getpDateStop();
		if (lDate > 0) {
			List<String> lListLineToWrite = new ArrayList<String>();
			List<IBTransaction> lListIBTransaction = pIBManager.getpIBTransactionManager().getpListIBTransaction();
			Collections.sort(lListIBTransaction);
			for (IBTransaction lIBTransaction : lListIBTransaction) {
				String lLine = lIBTransaction.getpDate()
						+ "," + lIBTransaction.getpComment()
						+ "," + lIBTransaction.getpBKAsset().getpName()
						+ "," + (lIBTransaction.getpAmount() * lIBTransaction.getpMultiplier())
						+ "," + lIBTransaction.getpPrice()
						+ "," + lIBTransaction.getpBKAccount().getpEmailAddress()
						+ "," + lIBTransaction.getpBKIncome();
				lListLineToWrite.add(lLine);
			}
			/*
			 * Write file
			 */
			String lHeader = "#Date,Comment,BKAsset,Amount,BKPrice,BKAccount,BKIncome";
			String lDir = StaticDir.getTREATED_BROKERS_IB();
			String lNameFile = lDate + StaticNames.getTREATED_INTERACTIVEBROKERS();
			BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
			/*
			 * Communication
			 */
			BasicPrintMsg.display(this, "File written: " + lDir + lNameFile);
		}
	}

}
