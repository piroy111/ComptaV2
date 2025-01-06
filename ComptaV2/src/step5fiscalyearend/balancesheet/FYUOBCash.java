package step5fiscalyearend.balancesheet;

import uob.step1objects.transactions.UOBTransaction;
import uob.step1objects.uobaccount.UOBAccount;

class FYUOBCash {

	protected FYUOBCash(UOBAccount _sUOBAccount) {
		pUOBAccount = _sUOBAccount;
		pLedger = 0.;
	}
	
	/*
	 * Data
	 */
	private double pLedger;
	private UOBAccount pUOBAccount;
	
	/**
	 * update new data if needed
	 * @param _sUOBTransaction
	 */
	protected final void updateNewDate(UOBTransaction _sUOBTransaction) {
		pLedger += _sUOBTransaction.getmAmount();
	}
	
	/**
	 * Classic toString
	 */
	public final String toString() {
		return "pCurrency= " + getpCurrency()
				+ "; BKAccount= " + pUOBAccount.getmNumber()
				+ "; pLedger= " + pLedger;
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpCurrency() {
		return pUOBAccount.getmCurrency();
	}
	public final double getpLedger() {
		return pLedger;
	}
	public final void setpLedger(double pLedger) {
		this.pLedger = pLedger;
	}
	public final UOBAccount getpUOBAccount() {
		return pUOBAccount;
	}
	
}
