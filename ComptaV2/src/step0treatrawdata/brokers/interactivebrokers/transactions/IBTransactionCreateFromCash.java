package step0treatrawdata.brokers.interactivebrokers.transactions;

import basicmethods.BasicPrintMsg;
import staticdata.StaticBKIncome.BKI_HEDGING_SUB;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataCash;
import step0treatrawdata.brokers.interactivebrokers.ibstatic.IBStatic;
import step0treatrawdata.brokers.interactivebrokers.reports.IBReport;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;

public class IBTransactionCreateFromCash {

	protected IBTransactionCreateFromCash(IBTransactionManager _sIBTransactionManager) {
		pIBTransactionManager = _sIBTransactionManager;
	}
	
	/*
	 * Data
	 */
	private IBTransactionManager pIBTransactionManager;
	
	/**
	 * 
	 * @param _sIBReport0
	 * @param _sIBReport1
	 */
	protected final void createFromCashReport(IBReport _sIBReport0, IBReport _sIBReport1) {
		int lDate = _sIBReport1.getpDateStop();
		/*
		 * Case there is no overlap --> we take the raw data
		 */
		if (_sIBReport0 == null || _sIBReport1.getpDateStart() > _sIBReport0.getpDateStop()) {
			for (IBDataCash lIBDataCash : _sIBReport1.getpIBDataManager().getpMapSymbolToIBDataCash().values()) {
				BKAsset lBKAsset = BKAssetManager.getpBKCurrency(lIBDataCash.getpSymbol());
				/*
				 * Create
				 */
				pIBTransactionManager.createIBTransactionFromNonTrade(lDate, IBStatic.getCOMMENT_DEPOSITS(), lBKAsset, lIBDataCash.getpDeposits(), Double.NaN, BKI_HEDGING_SUB.Cash_wire_in);
				pIBTransactionManager.createIBTransactionFromNonTrade(lDate, IBStatic.getCOMMENT_INTERESTS(), lBKAsset, lIBDataCash.getpBrokerInterests(), Double.NaN, BKI_HEDGING_SUB.Interests);
				pIBTransactionManager.createIBTransactionFromNonTrade(lDate, IBStatic.getCOMMENT_COMMISSIONS(), lBKAsset, lIBDataCash.getpOtherFees(), Double.NaN, BKI_HEDGING_SUB.Commissions);
			}
		} 
		/*
		 * Case there is an overlap --> we need to take the differential
		 */
		else if (_sIBReport0.getpDateStart() == _sIBReport1.getpDateStart()) {
			for (IBDataCash lIBDataCash1 : _sIBReport1.getpIBDataManager().getpMapSymbolToIBDataCash().values()) {
				/*
				 * Load
				 */
				BKAsset lBKAsset = BKAssetManager.getpBKCurrency(lIBDataCash1.getpSymbol());
				IBDataCash lIBDataCash0 = _sIBReport0.getpIBDataManager().getpMapSymbolToIBDataCash().get(lIBDataCash1.getpSymbol());
				/*
				 * Compute Quantities
				 */
				double lAmountDeposit = lIBDataCash1.getpDeposits();
				double lAmountInterest = lIBDataCash1.getpBrokerInterests();
				double lAmountFees = lIBDataCash1.getpOtherFees();
				if (lIBDataCash0 != null) {
					lAmountDeposit += -lIBDataCash0.getpDeposits();
					lAmountInterest += -lIBDataCash0.getpBrokerInterests();
					lAmountFees += -lIBDataCash0.getpOtherFees();
				}
				/*
				 * Create IBTransactions
				 */
				pIBTransactionManager.createIBTransactionFromNonTrade(lDate, IBStatic.getCOMMENT_DEPOSITS(), lBKAsset, lAmountDeposit, Double.NaN, BKI_HEDGING_SUB.Cash_wire_in);
				pIBTransactionManager.createIBTransactionFromNonTrade(lDate, IBStatic.getCOMMENT_INTERESTS(), lBKAsset, lAmountInterest, Double.NaN, BKI_HEDGING_SUB.Interests);
				pIBTransactionManager.createIBTransactionFromNonTrade(lDate, IBStatic.getCOMMENT_COMMISSIONS(), lBKAsset, lAmountFees, Double.NaN, BKI_HEDGING_SUB.Commissions);
			}
		}
		/*
		 * Conflict
		 */
		else {
			BasicPrintMsg.error("Error: the report must be strictly consecutive or overlapping with DateStart"
					+ "\nEither DateStart(Report1) > DateStop(Report0) or DateStart(Report1) == DateStart(Report0)"
					+ "\n_sIBReport0= '" + _sIBReport0 + "'"
					+ "\n_sIBReport1= '" + _sIBReport1 + "'");
		}
	}
	
}
