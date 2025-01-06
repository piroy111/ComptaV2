package step3statements.statements.abstractstatements;

import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.transactions.BKTransaction;
import step2loans.createloanstransactions.LNManager;
import step3statements.statements.main.STStatementGeneratorAbstract;
import step3statements.statements.statements.STWriteFileAccountingPvL;
import step3statements.statements.statements.STWriteFileStorageMonthly;

public class STIncome extends STStatementGeneratorAbstract {

	public STIncome(LNManager _sLNManager) {
		super(_sLNManager);
	}

	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return true;
	}

	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		String lKeyStr = _sBKTransaction.getpBKAccount().getpEmailAddress()
				+ ";;" + _sBKTransaction.getpBKIncome();
		return lKeyStr;
	}

	@Override public void instantiateStatements() {
		declareNewSTStatementAbstract(new STWriteFileAccountingPvL(this));
		declareNewSTStatementAbstract(new STWriteFileStorageMonthly(this));
	}
	
	/**
	 * 
	 * @param _sBKHolder
	 * @return
	 */
	public final BKAccount getpBKAccount(BKHolder _sBKHolder) {
		String[] lArray = _sBKHolder.getpKey().split(";;");
		String lEmaiAddress = lArray[0];
		return BKAccountManager.getpBKAccount(lEmaiAddress);
	}
	
	/**
	 * 
	 * @param _sBKHolder
	 * @return
	 */
	public final String getpBKIncome(BKHolder _sBKHolder) {
		String[] lArray = _sBKHolder.getpKey().split(";;");
		String lBKIncomeStr = lArray[1];
		return lBKIncomeStr;
	}


	
	
	
}
