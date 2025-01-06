package step0treatrawdata.ifb;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import ifb.IFBManager;
import ifb.transactions.IFBTransaction;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step1loadtransactions.accounts.BKAccountManager;

public class IFBTreatedManager {

	public IFBTreatedManager() {
		pIFBManager = new IFBManager();
	}
	
	/*
	 * Data
	 */
	private IFBManager pIFBManager;
	
	/**
	 * 
	 */
	public final void writeFileTreated() {
		/*
		 * Read files import IFB and load list of IFBTransaction which are relevant for the accounting
		 */
		pIFBManager.run();
		List<IFBTransaction> lListIFBTransaction = pIFBManager.getpListIFBTransaction();
		/*
		 * Write file content
		 */
		BasicPrintMsg.displayTitle(this, "Write file treated for IFB (Condor)");
		List<String> lListLineToWrite = new ArrayList<>();
		for (IFBTransaction lIFBTransaction : lListIFBTransaction) {
			String lLine = lIFBTransaction.getpDate()
					+ "," + lIFBTransaction.getpComment()
					+ "," + lIFBTransaction.getpCurrency()
					+ "," + lIFBTransaction.getpAmount()
					+ "," + BKAccountManager.getpBKAccountBunker().getpEmailAddress()
					+ "," + lIFBTransaction.getpBKIncome()
					+ "," + "NaN";
			lListLineToWrite.add(lLine);
		}
		/*
		 * Write file
		 */
		String lDir = StaticDir.getIFB_ALL_TRANSACTIONS();
		String lNameFile = StaticNames.getIFB_ALL_TRANSACTIONS();
		String lHeader = "Date,Comment,BKAsset,Amount,BKAccount,BKIncome,BKPrice";
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
		BasicPrintMsg.display(this, "File written successfully: '" + lDir + lNameFile + "'");
	}
	
}
