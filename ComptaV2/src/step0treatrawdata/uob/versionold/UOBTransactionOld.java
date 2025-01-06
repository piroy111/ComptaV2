package step0treatrawdata.uob.versionold;

import step0treatrawdata.objects.BKAssetManager;

public class UOBTransactionOld implements Comparable<UOBTransactionOld> {

	protected UOBTransactionOld(UOBAccountOld _sUOBAccount,
			int _sValueDate,
			int _sDate,
			long _sTime,
			String _sDescription,
			String _sYourReference,
			String _sOurReference,
			String _sChequeNumber,
			String _sRemarks,
			double _sDeposit,
			double _sWithdrawal,
			double _sLedgerBalance) {
		mUOBAccount = _sUOBAccount;
		mValueDate = _sValueDate;
		mDate = _sDate;
		mTime = _sTime;
		mDescription = _sDescription;
		mYourReference = _sYourReference;
		mOurReference = _sOurReference;
		mChequeNumber = _sChequeNumber;
		mRemarks = _sRemarks;
		mDeposit = _sDeposit;
		mWithdrawal = _sWithdrawal;
		mLedgerBalance = _sLedgerBalance;
	}
	
	
	/*
	 * Intrinsic from UOB
	 */
	private UOBAccountOld mUOBAccount;
	private int mValueDate;
	private int mDate;
	private long mTime;
	private String mDescription;
	private String mYourReference;
	private String mOurReference;
	private String mChequeNumber;
	private String mRemarks;
	private double mDeposit;
	private double mWithdrawal;
	private double mLedgerBalance;
	/*
	 * Data
	 */
	private String mAccount;
	private String mCategory;
	private String mComment;
	
	public final String toString() {
		return "Date= " + mValueDate
				+ "; Comment= " + getmComment()
				+ "; Amount= " + getmAmount() + " " + mUOBAccount.getmCurrency()
				+ "; AmountUSD= " + getmAmountUSD();
	}
	
	public final String toString2() {
		return "Date= " + mValueDate
				+ "; mDescription= " + mDescription
				+ "; mYourReference= " + mYourReference
				+ "; mOurReference= " + mOurReference
				+ "; mRemarks= " + mRemarks
				+ "; Amount= " + getmAmount() + " " + mUOBAccount.getmCurrency()
				+ "; AmountUSD= " + getmAmountUSD();
	}
	
	@Override
	public int compareTo(UOBTransactionOld _sUOBTransaction) {
		int lKey1 = Integer.compare(mDate, _sUOBTransaction.mDate);
		if (lKey1 != 0) {
			return lKey1;
		}
		return Long.compare(mTime, _sUOBTransaction.mTime);
	}	
	
//	/**
//	 * @return Comment = Remarks or description if remarks si empty
//	 */
//	public final String getmComment() {
//		String lComment = mRemarks;
//		if (lComment.equals("")) {
//			lComment = mDescription;
//		}
//		return lComment;
//	}
	
	/**
	 * @return Cash flow
	 */
	public final double getmAmount() {
		return mDeposit - mWithdrawal;
	}
	
	/**
	 * @return Cash flow
	 */
	public final double getmAmountUSD() {
		return getmAmount() / BKAssetManager.getpForexReference(mDate, mUOBAccount.getmCurrency());
	}
	
	/*
	 * Getters & Setters
	 */
	public final UOBAccountOld getmUOBAccount() {
		return mUOBAccount;
	}
	public final int getmValueDate() {
		return mValueDate;
	}
	public final int getmDate() {
		return mDate;
	}
	public final long getmTime() {
		return mTime;
	}
	public final String getmDescription() {
		return mDescription;
	}
	public final String getmYourReference() {
		return mYourReference;
	}
	public final String getmOurReference() {
		return mOurReference;
	}
	public final String getmChequeNumber() {
		return mChequeNumber;
	}
	public final String getmRemarks() {
		return mRemarks;
	}
	public final double getmDeposit() {
		return mDeposit;
	}
	public final double getmWithdrawal() {
		return mWithdrawal;
	}
	public final double getmLedgerBalance() {
		return mLedgerBalance;
	}
	public final String getmCategory() {
		return mCategory;
	}
	protected final void setmCategory(String mCategory) {
		this.mCategory = mCategory;
	}
	protected final String getmAccount() {
		return mAccount;
	}
	protected final void setmAccount(String mAccount) {
		this.mAccount = mAccount;
	}
	public final String getmComment() {
		return mComment;
	}
	protected final void setmComment(String mComment) {
		this.mComment = mComment;
	}
	
	
}
