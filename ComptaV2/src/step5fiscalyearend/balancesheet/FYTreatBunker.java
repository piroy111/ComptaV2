package step5fiscalyearend.balancesheet;

import java.util.HashMap;
import java.util.Map;

import staticbkincome.hedging.BKIncomeHedgingManager;
import staticdata.StaticBKIncome;
import staticdata.StaticNames;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.bars.BKBar;
import step1loadtransactions.inventory.BKInventory;

class FYTreatBunker {

	protected FYTreatBunker(FYBalanceSheet _sFYBalanceSheet) {
		pFYBalanceSheet = _sFYBalanceSheet;
		pNetIncomeUSD = Double.NaN;
		pCapitalUSD = Double.NaN;
		pMapLoanToValueUSD = new HashMap<String, Double>();
	}

	/*
	 * Data
	 */
	private FYBalanceSheet pFYBalanceSheet;
	private double pCapitalUSD;
	private double pNetIncomeUSD;
	private Map<String, Double> pMapLoanToValueUSD;
	
	/**
	 * Compute the DATAS for the DataFY
	 * @param _sDateFY
	 */
	protected final void run(BKInventory _sBKInventory, String _sFileNameOrigin, String _sBKIncomeStr) {
		/*
		 * Capital
		 */
		if (_sBKIncomeStr.equals(StaticBKIncome.getCAPITAL())) {
			pCapitalUSD = _sBKInventory.getpValueUSD();
		} else {
			/*
			 * Income FY
			 */
			if (Double.isNaN(pNetIncomeUSD)) {
				pNetIncomeUSD = 0.;
			}
			pNetIncomeUSD += _sBKInventory.getpValueUSD();
			/*
			 * Bars held by bunker
			 */
			for (BKBar lBKBar : _sBKInventory.getpMapBKBarToHolding().keySet()) {
				int lHolding = _sBKInventory.getpMapBKBarToHolding().get(lBKBar);
				double lValueUSD = lHolding * lBKBar.getpWeightOz() * lBKBar.getpBKAsset().getpPriceUSD(_sBKInventory.getpDate());
				String lNameFYAsset = "Bars held by Bunker in " + lBKBar.getpBKBarType().getmMetal();
				pFYBalanceSheet.getpOrCreateFYAsset(lNameFYAsset).addValue(lValueUSD);
			}
			/*
			 * Hedging Account
			 */
			if (_sBKIncomeStr.startsWith(StaticBKIncome.getHEDGING())
					&& !_sFileNameOrigin.endsWith(StaticNames.getUOB_ALL_TRANSACTIONS())
					&& !_sFileNameOrigin.contains(BKAccountManager.getpBKAccountPierreRoy().getpEmailAddress())) {
				double lValueUSD = _sBKInventory.getpValueUSD();
				String lNameFYAsset = "Hedging broker account " + BKIncomeHedgingManager.getBKIncomeHedging(_sBKIncomeStr).getpBroker();
				pFYBalanceSheet.getpOrCreateFYAsset(lNameFYAsset).addValue(lValueUSD);
			}
			/*
			 * CryptoCurrencies
			 */
			else if (_sBKIncomeStr.startsWith(StaticBKIncome.getCRYPTOS())
					&& !_sFileNameOrigin.endsWith(StaticNames.getUOB_ALL_TRANSACTIONS())) {
				double lValueUSD = _sBKInventory.getpValueUSD();
				String lNameFYAsset = _sBKIncomeStr;
				pFYBalanceSheet.getpOrCreateFYAsset(lNameFYAsset).addValue(lValueUSD);
			}
			/*
			 * Loans
			 */
			else if (_sBKIncomeStr.startsWith(StaticBKIncome.getLOAN())) {
				pMapLoanToValueUSD.put(_sBKIncomeStr, -_sBKInventory.getpValueUSD());
			}
		}
	}

	/*
	 * Getters & Setters
	 */
	public final double getpCapitalUSD() {
		return pCapitalUSD;
	}
	public final double getpNetIncomeUSD() {
		return pNetIncomeUSD;
	}
	public final Map<String, Double> getpMapLoanToValueUSD() {
		return pMapLoanToValueUSD;
	}
	public final FYBalanceSheet getpFYBalanceSheet() {
		return pFYBalanceSheet;
	}
	
}
