package step5fiscalyearend.balancesheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import step0treatrawdata.objects.BKAssetManager;
import uob.step1objects.transactions.UOBTransaction;
import uob.step1objects.uobaccount.UOBAccount;

class FYTreatUOBCash {

	protected FYTreatUOBCash(FYBalanceSheet _sFYBalanceSheet) {
		pFYBalanceSheet = _sFYBalanceSheet;
	}

	/*
	 * Data
	 */
	private FYBalanceSheet pFYBalanceSheet;
	private Map<String, Double> pMapCurrencyToCashAtUOB;
	private Map<UOBAccount, FYUOBCash> pMapUOBAccountToFYUOBCash;

	/**
	 * Extract the cash ledger for the FYdate 
	 */
	protected final void run() {
		/*
		 * Reset the Maps
		 */
		pMapCurrencyToCashAtUOB = new HashMap<String, Double>();
		pMapUOBAccountToFYUOBCash = new HashMap<UOBAccount, FYUOBCash>();
		List<UOBTransaction> lListUOBTransaction = new ArrayList<>(pFYBalanceSheet.getpFYMain()
				.getpUOBMainManager().getmUOBTransactionManager().getmListUOBTransaction());
		Collections.sort(lListUOBTransaction);
		/*
		 * Look for the oldest ledger balance before the DateFY
		 */
		for (UOBTransaction lUOBTransaction : lListUOBTransaction) {
			if (lUOBTransaction.getmValueDate() <= pFYBalanceSheet.getpDateFY()) {
				UOBAccount lUOBAccount = lUOBTransaction.getmUOBAccount();
				FYUOBCash lFYUOBCash = pMapUOBAccountToFYUOBCash.get(lUOBAccount);
				if (lFYUOBCash == null) {
					lFYUOBCash = new FYUOBCash(lUOBAccount);
					pMapUOBAccountToFYUOBCash.put(lUOBAccount, lFYUOBCash);
				}
				lFYUOBCash.updateNewDate(lUOBTransaction);
			}
		}
		/*
		 * Compute the Ledger Balance per currency
		 */
		for (UOBAccount lUOBAccount : pMapUOBAccountToFYUOBCash.keySet()) {
			String lCurrency = lUOBAccount.getmCurrency();
			Double lCash = pMapCurrencyToCashAtUOB.get(lCurrency);
			if (lCash == null) {
				lCash = 0.;
			}
			lCash += pMapUOBAccountToFYUOBCash.get(lUOBAccount).getpLedger()
					/ BKAssetManager.getpForexReference(pFYBalanceSheet.getpDateFY(), lCurrency);
			pMapCurrencyToCashAtUOB.put(lCurrency, lCash);
		}
		/*
		 * Round the cash at 4 digits after the comma
		 */
		for (String lCurrency : pMapCurrencyToCashAtUOB.keySet()) {
			Double lCash = pMapCurrencyToCashAtUOB.get(lCurrency);
			lCash = Math.round(lCash * 1e4) / 1e4;
			pMapCurrencyToCashAtUOB.put(lCurrency, lCash);
		}
	}

	/*
	 * Getters & Setters
	 */
	public final Map<String, Double> getpMapCurrencyToCashAtUOB() {
		return pMapCurrencyToCashAtUOB;
	}





}
