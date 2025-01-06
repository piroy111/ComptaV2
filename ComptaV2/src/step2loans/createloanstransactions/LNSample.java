package step2loans.createloanstransactions;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiers;
import step1loadtransactions.transactions.BKTransaction;

public class LNSample {

	public static void main(String[] _sArgs) {
		/*
		 * run LNManager
		 */
		LNManager lLNManager = new LNManager();
		lLNManager.run(true);
		/*
		 * Write file
		 */
		String lHeader = "Date,BKAsset,Comment,Quantity,BKPrice,BKAccount,BKIncome,File origin";
		List<String> lListLineToWrite = new ArrayList<String>();
		for (BKTransaction lBKTransaction : lLNManager.getpBKTransactionManager().getpListBKTransaction()) {
			String lLine = lBKTransaction.getpDate()
					+ "," + lBKTransaction.getpBKAsset().getpName()
					+ "," + lBKTransaction.getpComment()
					+ "," + lBKTransaction.getpQuantity()
					+ "," + lBKTransaction.getpBKPrice()
					+ "," + lBKTransaction.getpBKAccount().getpEmailAddress()
					+ "," + lBKTransaction.getpBKIncome()
					+ "," + lBKTransaction.getpFileNameOrigin();
			lListLineToWrite.add(lLine);
		}
		BasicFichiers.writeFile("F:/BUNKER_V2/zz_old/", "dump_BKtransactions.csv", lHeader, lListLineToWrite);
	}
	
	
}
