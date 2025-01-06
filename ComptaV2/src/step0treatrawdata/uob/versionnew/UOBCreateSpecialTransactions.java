package step0treatrawdata.uob.versionnew;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.StaticBKIncome;
import staticdata.StaticDir;
import uob.step1objects.transactions.UOBTransaction;

/**
 * @deprecated
 *
 */
public class UOBCreateSpecialTransactions {

	public UOBCreateSpecialTransactions(UOBTreatedManager _sUOBTreatedManager) {
		pUOBTreatedManager = _sUOBTreatedManager;
	}
	
	/*
	 * Data
	 */
	private UOBTreatedManager pUOBTreatedManager;
	private List<String> pListLineToWrite;

	/**
	 * 
	 */
	public final void run() {
		/*
		 * Initiate
		 */
		pListLineToWrite = new ArrayList<>();
		List<UOBTransaction> lListUOBTransaction = pUOBTreatedManager
				.getpUOBMainManager().getmUOBTransactionManager().getmListUOBTransaction();
		/*
		 * Loop over the transactions
		 */
		for (UOBTransaction lUOBTransaction : lListUOBTransaction) {
			if (lUOBTransaction.getmBKIncome().equals(StaticBKIncome.getCONDOR_OUT())) {
				/*
				 * Load data
				 */
				int lDate = lUOBTransaction.getmValueDate();
				String lComment = "Virtual transaction to offset Condor movements";
				double lAmount = -(lUOBTransaction.getmDeposit() - lUOBTransaction.getmWithdrawal());
				String lCurrency = lUOBTransaction.getmUOBAccount().getmCurrency();
				double lAmountUSD = lUOBTransaction.getmAmountUSD();
				String lAccount = lUOBTransaction.getmAccount();
				String lCategory = StaticBKIncome.getCONDOR_IN();
				/*
				 * Write in file
				 * 
				 */
				String lLine = lDate
						+ "," + lComment
						+ "," + lCurrency
						+ "," + lAmount
						+ "," + lAccount
						+ "," + lCategory
						+ "," + lAmountUSD
						+ "," + Double.NaN;
				pListLineToWrite.add(lLine);
				/*
				 * Communication
				 */
				BasicPrintMsg.display(this, "New virtual UOBTransaction created= " + lLine);
			}
		}
		/*
		 * Write file
		 */
		String lDir = StaticDir.getUOB_ALL_TRANSACTIONS();
		String lName = ""; // StaticNames.getCONDOR();
		String lHeader = "Date,Comment,BKAsset,Amount,BKAccount,BKIncome,AmountUSD,BKPrice";
		BasicFichiers.writeFile(lDir, lName, lHeader, pListLineToWrite);
	}

	/*
	 * Getters & Setters
	 */
	public final UOBTreatedManager getpUOBTreatedManager() {
		return pUOBTreatedManager;
	}
	public final List<String> getpListLineToWrite() {
		return pListLineToWrite;
	}
	
	
}
