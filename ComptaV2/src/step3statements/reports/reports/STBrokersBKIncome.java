package step3statements.reports.reports;

import staticbkincome.hedging.BKIncomeHedgingManager;
import staticdata.StaticBKIncome;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step3statements.reports.manager.STAbstractArray;
import step3statements.reports.manager.STReportManager;

public class STBrokersBKIncome extends STAbstractArray {

	public STBrokersBKIncome(STReportManager _sSTReportManager) {		
		super(_sSTReportManager.getpBKTransactionManager(), 
				StaticDir.getOUTPUT_VALUATIONS(),
				StaticNames.getOUTPUT_BROKERS_BKINCOMES());

	}

	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAccount().equals(BKAccountManager.getpBKAccountBunker())
				&& _sBKTransaction.getpBKIncome().startsWith(StaticBKIncome.getHEDGING());
	}

	@Override public String getpHeaderRow(BKTransaction _sBKTransaction) {
		return BKIncomeHedgingManager.getBKIncomeHedging(_sBKTransaction.getpBKIncome()).getpBroker();
	}

	@Override public String getpHeaderColumn(BKTransaction _sBKTransaction) {
		return BKIncomeHedgingManager.getBKIncomeHedging(_sBKTransaction.getpBKIncome()).getpSubHedging();
	}
	
	@Override public Integer comparatorHeaderRow(String _sHeaderRow1, String _sHeaderRow2) {
		return null;
	}

	@Override public Integer comparatorHeaderColumn(String _sHeaderColumn1, String _sHeaderColumn2) {
		return null;
	}

	@Override public String valueToDisplay(String _sHeaderRow, String _sHeaderColumn, BKInventory _sBKInventory) {
		return "" + _sBKInventory.getpValueUSD();
	}

	

}
