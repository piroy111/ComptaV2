package step0treatrawdata.uob.versionnew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.StaticDir;
import staticdata.StaticNames;
import uob.step1objects.transactions.UOBTransaction;

class UOBTreatedWriter {

	protected UOBTreatedWriter(UOBTreatedManager _sUOBTreatedManager) {
		pUOBTreatedManager = _sUOBTreatedManager;
	}
	
	/*
	 * Data
	 */
	private UOBTreatedManager pUOBTreatedManager;
	
	/**
	 * There is only one file with all the UOBTransactions<br>
	 * We re-write it every time<br>
	 */
	public final void run() {
		/*
		 * Check we actually loaded the UOBTransactions from the raw file
		 */
		if (pUOBTreatedManager.getpUOBMainManager() == null) {
			BasicPrintMsg.error("The method 'UOBTreatedManager.loadFromRawFiles()' should have been launched before. Otherwise the list of UOBTransaction is empty");
		}
		/*
		 * Load the list of UOBTransaction from the independent module UOBMainManager<br>
		 * This module reads the raw files of UOB, check all transactions are assigned to an account, and creates the objects UOBTransaction<br>
		 */
		List<UOBTransaction> lListUOBTransaction = pUOBTreatedManager
				.getpUOBMainManager().getmUOBTransactionManager().getmListUOBTransaction();
		/*
		 * Prepare the content of the file to write
		 */
		List<String> lListLineToWrite = new ArrayList<String>();
		for (UOBTransaction lUOBTransaction : lListUOBTransaction) {
			/*
			 * Load data
			 */
			int lDate = lUOBTransaction.getmValueDate();
			String lComment = lUOBTransaction.getmComment();
			double lAmount = lUOBTransaction.getmDeposit() - lUOBTransaction.getmWithdrawal();
			String lCurrency = lUOBTransaction.getmUOBAccount().getmCurrency();
			double lAmountUSD = lUOBTransaction.getmAmountUSD();
			String lAccount = lUOBTransaction.getmAccount();
			String lCategory = lUOBTransaction.getmBKIncome();
			/*
			 * Write in file 
			 */
			String lLine = lDate
					+ "," + lComment
					+ "," + lCurrency
					+ "," + lAmount
					+ "," + lAccount
					+ "," + lCategory
					+ "," + lAmountUSD
					+ "," + Double.NaN;
			lListLineToWrite.add(lLine);
		}
		Collections.sort(lListLineToWrite);
		/*
		 * Write the file
		 */
		String lDir = StaticDir.getUOB_ALL_TRANSACTIONS();
		String lNameFile = StaticNames.getUOB_ALL_TRANSACTIONS();
		String lHeader = "Date,Comment,BKAsset,Amount,BKAccount,BKIncome,Amount USD,BKPrice";
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
	}
	
}
