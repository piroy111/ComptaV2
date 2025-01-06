package step0treatrawdata.brokers.interactivebrokers.transactions;

import staticbkincome.hedging.BKIncomeHedgingManager;
import staticdata.StaticBKIncome.BKI_HEDGING_BROKERS;
import staticdata.StaticBKIncome.BKI_HEDGING_SUB;
import step0treatrawdata.brokers.interactivebrokers.ibstatic.IBStatic;
import step0treatrawdata.objects.BKAsset;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;

public class IBTransaction implements Comparable<IBTransaction> {

	protected IBTransaction(int _sDate, String _sComment, 
			BKAsset _sBKAsset, double _sAmount, double _sPrice,  BKI_HEDGING_SUB _sBKIncome) {
		pDate = _sDate;
		pComment = _sComment;
		pBKAsset = _sBKAsset;
		pAmount = _sAmount;
		pPrice = _sPrice;
		/*
		 * Add to BKIncome
		 */
		pBKIncome = BKIncomeHedgingManager.getBKIncomeStr(BKI_HEDGING_BROKERS.InteractiveBrokers, _sBKIncome);
		/*
		 * Constant
		 */
		pBKAccount = BKAccountManager.getpBKAccountBunker();
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private String pComment;
	private BKAsset pBKAsset;
	private double pAmount;
	private double pPrice;
	private BKAccount pBKAccount;
	private String pBKIncome;
	
	/**
	 * Classic toString
	 */
	public final String toString() {
		return "pDate= " + pDate
				+ "; pComment= " + pComment
				+ "; pBKAsset= " + pBKAsset
				+ "; pAmount= " + pAmount
				+ "; pPrice= " + pPrice
				+ "; pBKIncome= " + pBKIncome;
	}
	
	@Override public int compareTo(IBTransaction _sIBTransaction) {
		return Integer.compare(pDate, _sIBTransaction.getpDate());
	}
	
	/**
	 * 
	 * @return
	 */
	public final int getpMultiplier() {
		if (pBKAsset.getpIsOil()) {
			return IBStatic.getOIL_MULTIPLIER();
		} else {
			return 1;
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final String getpComment() {
		return pComment;
	}
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final double getpAmount() {
		return pAmount;
	}
	public final double getpPrice() {
		return pPrice;
	}
	public final BKAccount getpBKAccount() {
		return pBKAccount;
	}
	public final String getpBKIncome() {
		return pBKIncome;
	}

	
	
	
}
