package step5fiscalyearend.incomestatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicPrintMsg;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.transactions.BKTransaction;
import step2loans.createloanstransactions.LNManager;

public class FYIncomeStatementGenerator extends BKHolderGenerator {

	public FYIncomeStatementGenerator(LNManager _sLNManager) {
		super(_sLNManager.getpBKTransactionManager());
	}

	/**
	 * Keep only the BKTransactions of Bunker
	 */
	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAccount().equals(BKAccountManager.getpBKAccountBunker());
	}

	/**
	 * One column per BKIncome
	 */
	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKIncome();
	}

	/*
	 * Data
	 */
	private List<FYIncomeStatement> pListFYToIncomeStatement;
	private List<FYIncomeStatementDelta> pListFYToIncomeStatementDelta;
	
	/**
	 * Write the files for the income statements from inception and the ones for year on year
	 * @param _sListDateFY
	 */
	public final void run(List<Integer> _sListDateFY) {
		/*
		 * Income statements since the creation of the company
		 */
		BasicPrintMsg.displayTitle(this, "Compute FY Income and write files");
		pListFYToIncomeStatement = new ArrayList<>();
		for (int lDateFY : _sListDateFY) {
			FYIncomeStatement lFYIncomeStatement = new FYIncomeStatement(this, lDateFY) ;
			pListFYToIncomeStatement.add(lFYIncomeStatement);
			lFYIncomeStatement.writeFile();
		}
		Collections.sort(pListFYToIncomeStatement);
		/*
		 * Income Statement Year per Year
		 */
		BasicPrintMsg.displayTitle(this, "Compute FY Income year on year and write files");
		pListFYToIncomeStatementDelta = new ArrayList<>();
		FYIncomeStatement lFYIncomeStatementM1 = null;
		for (FYIncomeStatement lFYIncomeStatement : pListFYToIncomeStatement) {
			FYIncomeStatementDelta lFYIncomeStatementDelta = new FYIncomeStatementDelta(lFYIncomeStatementM1, lFYIncomeStatement);
			lFYIncomeStatementDelta.writeFile();
			pListFYToIncomeStatementDelta.add(lFYIncomeStatementDelta);
			lFYIncomeStatementM1 = lFYIncomeStatement;
		}
	}

	/*
	 * Getters & Setters
	 */
	public final List<FYIncomeStatement> getpListFYToIncomeStatement() {
		return pListFYToIncomeStatement;
	}
	public final List<FYIncomeStatementDelta> getpListFYToIncomeStatementDelta() {
		return pListFYToIncomeStatementDelta;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
