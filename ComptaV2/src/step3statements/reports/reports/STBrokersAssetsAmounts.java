package step3statements.reports.reports;

import staticbkincome.hedging.BKIncomeHedgingManager;
import staticdata.StaticBKIncome;
import staticdata.StaticBKIncome.BKI_HEDGING_SUB;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step3statements.reports.manager.STAbstractArray;
import step3statements.reports.manager.STReportManager;

public class STBrokersAssetsAmounts extends STAbstractArray {

	public STBrokersAssetsAmounts(STReportManager _sSTReportManager) {		
		super(_sSTReportManager.getpBKTransactionManager(), 
				StaticDir.getOUTPUT_VALUATIONS(),
				StaticNames.getOUTPUT_BROKERS_AMOUNTS());

	}

	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAccount().equals(BKAccountManager.getpBKAccountBunker())
				&& _sBKTransaction.getpBKIncome().startsWith(StaticBKIncome.getHEDGING())
				&& !_sBKTransaction.getpBKIncome().endsWith(BKI_HEDGING_SUB.Cash_wire_out.toString().replaceAll("_", " "))
//				&& !_sBKTransaction.getpBKIncome().endsWith(BKI_HEDGING_SUB.Directional.toString().replaceAll("_", " "))
				;
	}

	@Override public String getpHeaderRow(BKTransaction _sBKTransaction) {
		return BKIncomeHedgingManager.getBKIncomeHedging(_sBKTransaction.getpBKIncome()).getpBroker();
	}

	@Override public String getpHeaderColumn(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAsset().toString();
	}

	@Override public Integer comparatorHeaderRow(String _sHeaderRow1, String _sHeaderRow2) {
		return null;
	}

	@Override public Integer comparatorHeaderColumn(String _sHeaderColumn1, String _sHeaderColumn2) {
		return null;
	}

	@Override public String valueToDisplay(String _sHeaderRow, String _sHeaderColumn, BKInventory _sBKInventory) {
		BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(_sHeaderColumn);
		return "" + _sBKInventory.getpBKAssetQty(lBKAsset);
	}

}
