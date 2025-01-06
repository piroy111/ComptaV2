package step3statements.statements.abstractstatements;

import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.transactions.BKTransaction;
import step2loans.createloanstransactions.LNManager;
import step3statements.statements.main.STStatementGeneratorAbstract;
import step3statements.statements.statements.STWriteFileAmountMetalsStored;
import step3statements.statements.statements.STWriteFileBKBars;
import step3statements.statements.statements.STWriteFileBKBarsType;
import step3statements.statements.statements.STWriteFileBKHoldingClient;
import step3statements.statements.statements.STWriteFileBKLoans;
import step3statements.statements.statements.STWriteFileBKPvLClient;

public class STBKAccounts extends STStatementGeneratorAbstract {

	
	public STBKAccounts(LNManager _sLNManager) {
		super(_sLNManager);
	}
	
	/*
	 * Data
	 */
	private BKAccount pBKAccountCurrent;
	
	/**
	 * Keep all
	 */
	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return true;
	}

	/**
	 * One column per BKIncome
	 */
	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAccount().getpEmailAddress()
				+ "; " + _sBKTransaction.getpBKAccount().getpBKAssetCurrency().getpName();
	}

	@Override public void instantiateStatements() {
		declareNewSTStatementAbstract(new STWriteFileBKHoldingClient(this));
		declareNewSTStatementAbstract(new STWriteFileBKLoans(this));
		declareNewSTStatementAbstract(new STWriteFileBKPvLClient(this));
		declareNewSTStatementAbstract(new STWriteFileBKBars(this));
		declareNewSTStatementAbstract(new STWriteFileBKBarsType(this));
		declareNewSTStatementAbstract(new STWriteFileAmountMetalsStored(this));
	}
	
	/**
	 * @param _sKeyStr
	 * @return
	 */
	public BKAccount getpAndComputeBKAccountCurrent(BKHolder _sBKHolder) {
		String lKeyStr = _sBKHolder.getpKey();
		String lBKAccountStr = lKeyStr.split("; ", -1)[0];
		pBKAccountCurrent = BKAccountManager.getpBKAccount(lBKAccountStr);
		return pBKAccountCurrent;
	}

	/*
	 * Getters & Setters
	 */
	public final BKAccount getpBKAccountCurrent() {
		return pBKAccountCurrent;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
