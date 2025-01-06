package step3statements.statements.abstractstatements;

import step1loadtransactions.transactions.BKTransaction;
import step2loans.createloanstransactions.LNManager;
import step3statements.statements.main.STStatementGeneratorAbstract;
import step3statements.statements.statements.STWriteFileBKAssets;
import step3statements.statements.statements.STWriteFileBKTransactions;
import step3statements.statements.statements.STWriteFileBKTransactionsCommercial;
import step3statements.statements.statements.STWriteFileBKValueCurrency;
import step3statements.statements.statements.STWriteFileSummaryCurrent;

public class STAll extends STStatementGeneratorAbstract {

	public STAll(LNManager _sLNManager) {
		super(_sLNManager);
	}

	/**
	 * Keep only Bunker
	 */
	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return true;
	}

	/**
	 * One column per BKIncome
	 */
	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		return "All";
	}

	@Override public void instantiateStatements() {
		declareNewSTStatementAbstract(new STWriteFileBKTransactions(this));
		declareNewSTStatementAbstract(new STWriteFileBKTransactionsCommercial(this));
		declareNewSTStatementAbstract(new STWriteFileBKAssets(this));
		declareNewSTStatementAbstract(new STWriteFileSummaryCurrent(this));
		declareNewSTStatementAbstract(new STWriteFileBKValueCurrency(this));
	}
	
}
