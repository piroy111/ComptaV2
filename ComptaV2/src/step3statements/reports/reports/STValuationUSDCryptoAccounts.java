package step3statements.reports.reports;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step3statements.reports.manager.STReportManager;

public class STValuationUSDCryptoAccounts extends BKHolderGenerator {

	public STValuationUSDCryptoAccounts(STReportManager _sSTReportManager) {
		super(_sSTReportManager.getpBKTransactionManager());

	}

	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAccount().equals(BKAccountManager.getpBKAccountBunker())
				&& _sBKTransaction.getpBKIncome().equals("Cryptos");
	}

	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpComment();
	}

	
	public final void run() {
		generateBKHolder();
		/*
		 * Loop on the BKHolder
		 */
		List<String> lListLineToWrite = new ArrayList<String>();
		for (BKHolder lBKHolder : getpListBKHolder()) {
			/*
			 * Load
			 */
			BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(BasicDateInt.getmToday());
			/*
			 * 
			 */
			String lLine = lBKHolder.getpKey()
					+ "," + lBKInventory.getpValueUSD();
			lListLineToWrite.add(lLine);
		}
		/*
		 * Write File
		 */
		String lDir = StaticDir.getOUTPUT_VALUATIONS();
		BasicFichiers.getOrCreateDirectory(lDir);
		String lNameFile = BasicDateInt.getmToday() + StaticNames.getOUTPUT_CRYPTOS_VALUATION();
		String lHeader = "Crypto account,Value USD";
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
	}
	
	
	
	
	
	
	
	
	
	
}
