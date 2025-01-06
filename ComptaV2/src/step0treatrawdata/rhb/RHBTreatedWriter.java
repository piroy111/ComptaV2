package step0treatrawdata.rhb;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import rhb.RHBManager;
import rhb.transactions.RHBTransaction;
import staticdata.StaticBKIncome;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step1loadtransactions.accounts.BKAccountManager;

public class RHBTreatedWriter {

	public RHBTreatedWriter() {
		pRHBManager = new RHBManager();
	}
	
	/*
	 * Date
	 */
	private RHBManager pRHBManager;
	
	/**
	 * Write the file treated from the RHB transactions loaded
	 */
	public final void writeFileTreated() {
		pRHBManager.run();
		/*
		 * Initiate
		 */
		String lDir = StaticDir.getRHB_ALL_TRANSACTIONS();
		String lNameFile = StaticNames.getRHB_ALL_TRANSACTIONS();
		List<RHBTransaction> lListRHBTransaction = pRHBManager.getpRHBTransactionLoader().getpListRHBTransaction();
		List<String> lListLineToWrite = new ArrayList<>();
		/*
		 * Communicate
		 */
		BasicPrintMsg.displayTitle(this, "Write file treated for RHB");
		BasicPrintMsg.display(this, "Directory of file treated= '" + lDir + "'");
		/*
		 * Write RHBTransaction
		 */
		for (RHBTransaction lRHBTransaction : lListRHBTransaction) {
			String lLine = lRHBTransaction.getpDate()
					+ "," + lRHBTransaction.getpComment()
					+ "," + lRHBTransaction.getpCurrency()
					+ "," + lRHBTransaction.getpAmount()
					+ "," + BKAccountManager.getpBKAccountBunker().getpEmailAddress()
					+ "," + StaticBKIncome.getINTERNAL_TRANSFER()
					+ "," + "NaN";
			lListLineToWrite.add(lLine);
		}
		/*
		 * Write file
		 */
		String lHeader = "Date,Comment,BKAsset,Amount,BKAccount,BKIncome,BKPrice";
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
		BasicPrintMsg.display(this, "File treated written successfully= '" + lNameFile + "'");
	}
	
}
