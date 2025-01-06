package step3statements.statements.statements;

import basicmethods.BasicDateInt;
import staticdata.StaticBKIncome;
import staticdata.StaticDate;
import staticdata.StaticDir;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.transactions.BKTransaction;
import step3statements.statements.abstractstatements.STAll;
import step3statements.statements.main.STStatementAbstract;

public class STWriteFileBKTransactionsCommercial extends STStatementAbstract {

	public STWriteFileBKTransactionsCommercial(STAll _sSTAll) {
		super(_sSTAll);
	}
	
	@Override public String getpDirName() {
		return StaticDir.getOUTPUT_BKTRANSACTIONS_COMMERCIAL();
	}

	@Override public void createLines() {
		addToHeader("Date,BKAsset,Comment,Amount,Value $");
		/*
		 * Check date start and end: we take the last month
		 */
		int lDateEnd = StaticDate.getDATE_MAX();
		if (lDateEnd == -1) {
			lDateEnd = BasicDateInt.getmToday();
		}
		int lDateBegin = BasicDateInt.getmPlusDay(BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(lDateEnd, -1)), 1);
		/*
		 * File content
		 */
		for (BKHolder lBKHolder : pSTStatementGeneratorAbstract.getpListBKHolder()) {			
			for (BKTransaction lBKTransaction : lBKHolder.getpListBKTransaction()) {
				if (lDateBegin <= lBKTransaction.getpDate() && lBKTransaction.getpDate() <= lDateEnd) {
					if (lBKTransaction.getpBKIncome().equals(StaticBKIncome.getCOST_SPENDING_COMMERCIAL())
							|| lBKTransaction.getpBKIncome().equals(StaticBKIncome.getCOST_PHONE())) {
						String lLine = lBKTransaction.getpDate()
								+ "," + lBKTransaction.getpBKAsset().toString()
								+ "," + lBKTransaction.getpComment()
								+ "," + lBKTransaction.getpQuantity()
								+ "," + lBKTransaction.getpValueUSD();
						addToListLine(lLine);
					}
				}
			}
		}
		super.sortListOfLineToWrite();
	}








}
