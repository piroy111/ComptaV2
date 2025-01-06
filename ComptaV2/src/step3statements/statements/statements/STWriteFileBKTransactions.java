package step3statements.statements.statements;

import staticdata.StaticDir;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.transactions.BKTransaction;
import step3statements.statements.abstractstatements.STAll;
import step3statements.statements.main.STStatementAbstract;

public class STWriteFileBKTransactions extends STStatementAbstract {

	public STWriteFileBKTransactions(STAll _sSTAll) {
		super(_sSTAll);
	}
	
	@Override public String getpDirName() {
		return StaticDir.getOUTPUT_BKTRANSACTIONS();
	}

	@Override public void createLines() {
		addToHeader("Date,BKAsset,Comment,Quantity,BKPrice,BKAccount,FileNameOrigin,BKIncome,ValueUSD (Quantity*PriceBKAsset(Date))");
		for (BKHolder lBKHolder : pSTStatementGeneratorAbstract.getpListBKHolder()) {			
			for (BKTransaction lBKTransaction : lBKHolder.getpListBKTransaction()) {
				String lLine = lBKTransaction.getpDate()
						+ "," + lBKTransaction.getpBKAsset().toString()
						+ "," + lBKTransaction.getpComment()
						+ "," + lBKTransaction.getpQuantity()
						+ "," + lBKTransaction.getpBKPrice()
						+ "," + lBKTransaction.getpBKAccount().toString()
						+ "," + lBKTransaction.getpFileNameOrigin()
						+ "," + lBKTransaction.getpBKIncome()
						+ "," + lBKTransaction.getpValueUSD();
				addToListLine(lLine);
			}
		}
		super.sortListOfLineToWrite();
	}

	
	
	
	
	
	
	
	
}
