package step3statements.reports.manager;

import step1loadtransactions.transactions.BKTransactionManager;
import step2loans.createloanstransactions.LNManager;
import step3statements.reports.reports.STBrokersAssetsAmounts;
import step3statements.reports.reports.STBrokersAssetsPvL;
import step3statements.reports.reports.STBrokersBKIncome;
import step3statements.reports.reports.STBrokersExposurePaperCash;
import step3statements.reports.reports.STClientsBuyingPerMonth;
import step3statements.reports.reports.STCryptoAssetsAmounts;
import step3statements.reports.reports.STValuationUSDCryptoAccounts;
import step3statements.reports.reports.clientsholdingsendofmonth.STBKHoldingsClientsEndOfMonth;

public class STReportManager {

	
	public STReportManager(LNManager _sLNManager) {
		pLNManager = _sLNManager;
		/*
		 * Instantiate
		 */
		pBKTransactionManager = pLNManager.getpBKTransactionManager();
		instantiate();
	}
	
	/*
	 * Data
	 */
	private LNManager pLNManager;
	private BKTransactionManager pBKTransactionManager;
	private STClientsBuyingPerMonth pSTClientsBuyingPerMonth;
	private STValuationUSDCryptoAccounts pSTValuationUSDCryptoAccounts;
	private STBrokersAssetsPvL pSTBrokersAssetsValuation;
	private STBrokersBKIncome pSTBrokersBKIncome;
	private STBrokersAssetsAmounts pSTBrokersAssetsAmounts;
	private STCryptoAssetsAmounts pSTCryptoAssetsAmounts;
	private STBrokersExposurePaperCash pSTBrokersExposurePaperCash;
	private STBKHoldingsClientsEndOfMonth pSTBKHoldingsClientsEndOfMonth;
	
	/**
	 * 
	 */
	private void instantiate() {
		pSTClientsBuyingPerMonth = new STClientsBuyingPerMonth(this);
		pSTValuationUSDCryptoAccounts = new STValuationUSDCryptoAccounts(this);
		pSTBrokersAssetsValuation = new STBrokersAssetsPvL(this);
		pSTBrokersBKIncome = new STBrokersBKIncome(this);
		pSTBrokersAssetsAmounts = new STBrokersAssetsAmounts(this);
		pSTCryptoAssetsAmounts = new STCryptoAssetsAmounts(this);
		pSTBrokersExposurePaperCash = new STBrokersExposurePaperCash(this);
		pSTBKHoldingsClientsEndOfMonth = new STBKHoldingsClientsEndOfMonth(pBKTransactionManager);
	}
	
	/**
	 * 
	 */
	public final void run() {
		pSTBKHoldingsClientsEndOfMonth.run();
		pSTClientsBuyingPerMonth.run();
		pSTValuationUSDCryptoAccounts.run();
		pSTBrokersAssetsValuation.run();
		pSTBrokersBKIncome.run();
		pSTBrokersAssetsAmounts.run();
		pSTCryptoAssetsAmounts.run();
		pSTBrokersExposurePaperCash.run();
	}

	/*
	 * Getters & Setters
	 */
	public final LNManager getpLNManager() {
		return pLNManager;
	}
	public final BKTransactionManager getpBKTransactionManager() {
		return pBKTransactionManager;
	}
	
}
