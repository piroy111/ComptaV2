package step0treatrawdata.argentor;

import java.util.ArrayList;
import java.util.List;

import argentor.ARGManager;
import argentor.transactions.ARGTransaction;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticbkincome.hedging.BKIncomeHedgingManager;
import staticdata.StaticBKIncome.BKI_HEDGING_BROKERS;
import staticdata.StaticBKIncome.BKI_HEDGING_SUB;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step1loadtransactions.accounts.BKAccountManager;

public class ARGTreatedWriter {

	public static void main(String[] _sArgs) {
		new ARGTreatedWriter().writeFileTreated();
	}
	
	public ARGTreatedWriter() {
		pARGManager = new ARGManager();
	}
	
	/*
	 * Data
	 */
	private ARGManager pARGManager;
	
	/**
	 * Write the file treated from the ARGTransactions loaded
	 */
	public final void writeFileTreated() {
		/////////////////////////////////////////////////////////////////////////
		if (true) {
			return;
		}
		/*
		 * We dont know how the file of Argentor is modelized. 
		 * It is not normal that when I bought XAU, I had US$ in exchange. 
		 * It is normal if we settle the XAU. If we dont, we should not have an US$ in-flow
		 */
		/////////////////////////////////////////////////////////////////////////
		
		pARGManager.run();
		/*
		 * Initiate
		 */
		String lDir = StaticDir.getARGENTOR_ALL_TRANSACTIONS();
		String lNameFile = StaticNames.getARGENTOR_ALL_TRANSACTIONS();
		List<ARGTransaction> lListARGTransaction = pARGManager.getpARGTransactionLoader().getpListARGTransaction();
		List<String> lListLineToWrite = new ArrayList<>();
		/*
		 * Communicate
		 */
		BasicPrintMsg.displayTitle(this, "Write file treated for " + BKI_HEDGING_BROKERS.Argentor);
		BasicPrintMsg.display(this, "Directory of file treated= '" + lDir + "'");
		/*
		 * Write RHBTransaction
		 */
		for (ARGTransaction lARGTransaction : lListARGTransaction) {
			String lLine = lARGTransaction.getpDate()
					+ "," + lARGTransaction.getpComment()
					+ "," + lARGTransaction.getpCurrency()
					+ "," + lARGTransaction.getpAmount()
					+ "," + BKAccountManager.getpBKAccountBunker().getpEmailAddress()
					+ "," + BKIncomeHedgingManager.getBKIncomeStr(BKI_HEDGING_BROKERS.Argentor, BKI_HEDGING_SUB.Trades)
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
