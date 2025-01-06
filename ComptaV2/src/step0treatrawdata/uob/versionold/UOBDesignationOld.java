package step0treatrawdata.uob.versionold;

class UOBDesignationOld {

	protected UOBDesignationOld(String _sDesignation, String _sColumn, String _sCurrency) {
		mDesignation = _sDesignation;
		mColumn = _sColumn;
		mCurrency = _sCurrency;
	}
	
	/*
	 * Data ID
	 */
	private String mDesignation;
	private String mColumn;
	private String mCurrency;
	/*
	 * Data
	 */
	private String mAccount;
	private String mBKCategory;
	/*
	 * Static
	 */
	private static String COLUMN_REMARKS = "Remarks";
	private static String COLUMN_YOURREFERENCE = "Your Reference";
	private static String COLUMN_OURREFERENCE = "Our Reference";
	private static String COLUMN_DESCRIPTION = "Description";
	
	/**
	 * @return true if the designation is the one of the UOBTransaction
	 * @param _sUOBTransaction
	 */
	public final boolean getmIsMatch(UOBTransactionOld _sUOBTransaction) {
		String lDesignation = null;
		/*
		 * Find the designation in the right column
		 */
		if (mColumn.equals(COLUMN_REMARKS)) {
			lDesignation = _sUOBTransaction.getmRemarks();
		} else if (mColumn.equals(COLUMN_YOURREFERENCE)) {
			lDesignation = _sUOBTransaction.getmYourReference();
		} else if (mColumn.equals(COLUMN_OURREFERENCE)) {
			lDesignation = _sUOBTransaction.getmOurReference();
		} else if (mColumn.equals(COLUMN_DESCRIPTION)) {
			lDesignation = _sUOBTransaction.getmDescription();
		}
		/*
		 * Decide if the designation is known
		 */
		lDesignation = lDesignation.trim();
		boolean lIsMatch = true;
		if (lIsMatch) {
			lIsMatch = lIsMatch && (lDesignation != null && lDesignation.equals(mDesignation));
		}
		if (lIsMatch) {
			lIsMatch = lIsMatch && (mCurrency.equals("") || _sUOBTransaction.getmUOBAccount()
					.getmCurrency().equals(mCurrency));
		}
		return lIsMatch;
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getmDesignation() {
		return mDesignation;
	}
	public final String getmColumn() {
		return mColumn;
	}
	public final String getmCurrency() {
		return mCurrency;
	}
	public final String getmAccount() {
		return mAccount;
	}
	public final String getmBKCategory() {
		return mBKCategory;
	}
	public final void setmAccount(String mAccount) {
		this.mAccount = mAccount;
	}
	public final void setmBKCategory(String mBKCategory) {
		this.mBKCategory = mBKCategory;
	}
	
}
