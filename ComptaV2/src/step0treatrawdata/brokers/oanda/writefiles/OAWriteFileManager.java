package step0treatrawdata.brokers.oanda.writefiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticbkincome.hedging.BKIncomeHedgingManager;
import staticdata.StaticBKIncome;
import staticdata.StaticBKIncome.BKI_CRYPTO_BROKERS;
import staticdata.StaticBKIncome.BKI_HEDGING_BROKERS;
import staticdata.StaticBKIncome.BKI_HEDGING_SUB;
import step0treatrawdata.brokers.oanda.report.OAReport;
import step0treatrawdata.brokers.oanda.report.OATransaction;
import step1loadtransactions.accounts.BKAccountManager;

public class OAWriteFileManager {

	public OAWriteFileManager() {

	}

	/**
	 * 
	 */
	public final void run(List<OAReport> _sListOAReport, String _sDir, String _sSuffixNameFile) {
		/*
		 * Group the OATransactions
		 */
		List<OATransaction> lListOATransaction = new ArrayList<>();
		int lDate = -1;
		for (OAReport lOAReport : _sListOAReport) {
			lListOATransaction.addAll(lOAReport.getpListOATransaction());
			lDate = Math.max(lDate, lOAReport.getpDate());
		}
		Collections.sort(lListOATransaction);
		/*
		 * Check if CRYPTOS
		 */
		boolean lIsCrypto = _sSuffixNameFile.contains("Crypto");
		/*
		 * Write file content
		 */
		List<String> lListLineToWrite = new ArrayList<String>();
		for (OATransaction lOATransaction : lListOATransaction) {
			/*
			 * BKIncome
			 */
			String lBKIncome;
			if (lIsCrypto) {
				lBKIncome = StaticBKIncome.getCRYPTOS() + " " + BKI_CRYPTO_BROKERS.OANDA.toString();
			} else {
				BKI_HEDGING_SUB lBkiComment;
				if (lOATransaction.getpIsTradeOrCashFlow()) {
					lBkiComment = BKI_HEDGING_SUB.Trades;
				} else if (lOATransaction.getpComment().contains("Deposit")) {
					lBkiComment = BKI_HEDGING_SUB.Cash_wire_in;
				}  else if (lOATransaction.getpComment().contains("Commissions")) {
					lBkiComment = BKI_HEDGING_SUB.Commissions;
				} else {
					lBkiComment = BKI_HEDGING_SUB.Interests;
				}
				lBKIncome = BKIncomeHedgingManager.getBKIncomeStr(BKI_HEDGING_BROKERS.OANDA, lBkiComment);
			}
			/*
			 * Build List
			 */
			String lLineStr = lOATransaction.getpDate()
					+ "," + lOATransaction.getpComment()
					+ "," + lOATransaction.getpBKAsset()
					+ "," + lOATransaction.getpQuantity()
					+ "," + lOATransaction.getpPrice()
					+ "," + BKAccountManager.getpBKAccountBunker().getpEmailAddress()
					+ "," + lBKIncome;
			lListLineToWrite.add(lLineStr);
		}
		/*
		 * Write file
		 */
		if (lListLineToWrite.size() > 0) {
			String lNameFile = _sSuffixNameFile;
			String lHeader = "#Date,Comment,BKAsset,Amount,BKPrice,BKAccount,BKIncome";
			BasicFichiers.writeFile(_sDir, lNameFile, lHeader, lListLineToWrite);
			BasicPrintMsg.display(this,  "new file BKTransaction written: " + lNameFile);
		}
	}

	/*
	 * Getters & Setters
	 */

}
