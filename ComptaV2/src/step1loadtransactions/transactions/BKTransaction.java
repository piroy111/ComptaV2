package step1loadtransactions.transactions;

import basicmethods.BasicPrintMsg;
import step0treatrawdata.objects.BKAsset;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.bars.BKBarManager;

public class BKTransaction implements Comparable<BKTransaction> {

	/**
	 * A BKTransaction is a financial flow of a BKAsset (Cash, generic bar, etc.) for a given date
	 * @param _sDate
	 * @param _sBKAsset
	 * @param _sComment
	 * @param _sQuantity
	 * @param _sBKAccount
	 * @param _sBKIncome
	 * @param _sBKPrice
	 * @param _sFileNameOrigin
	 */
	public BKTransaction(int _sDate,
			BKAsset _sBKAsset,
			String _sComment,
			double _sQuantity,
			BKAccount _sBKAccount,
			String _sBKIncome,
			double _sBKPrice,
			String _sFileNameOrigin) {
		pBKAsset = _sBKAsset;
		pComment = _sComment;
		pQuantity = _sQuantity;
		pDate = _sDate;
		pBKAccount = _sBKAccount;
		pBKIncome = _sBKIncome;
		pBKPrice = _sBKPrice;
		pFileNameOrigin = _sFileNameOrigin;
		/*
		 * Initiate
		 */
		pValueUSD = pQuantity * pBKAsset.getpPriceUSD(pDate);
		BKBarManager.loadIfBKBar(this);
	}

	/*
	 * Data
	 */
	private int pDate;
	private BKAsset pBKAsset;
	private String pComment;
	private double pValueUSD;
	private double pQuantity;
	private BKAccount pBKAccount;
	private String pBKIncome;
	private String pFileNameOrigin;
	private double pBKPrice;

	/**
	 * Unique ID for the Map
	 * @param _sBKSubAccount
	 * @param _sDate
	 * @param _sBKAsset
	 * @param _sQuantity
	 */
	public static String getIDStr(int _sDate, BKAsset _sBKAsset, double _sQuantity) {
		return _sDate + ";;" + _sBKAsset.toString()
				+ ";;" + BasicPrintMsg.afficheDouble(_sQuantity, 6);				
	}

	/**
	 * Sort in ascending order of transaction date. Natural order
	 */
	@Override public int compareTo(BKTransaction _sBKTransaction) {
		return Integer.compare(pDate, _sBKTransaction.getpDate());
	}

	/**
	 * Classic to String
	 */
	public final String toString() {
		return "pDate= " + pDate
		+ "; pBKAsset= " + pBKAsset.getpName()
		+ "; pComment= " + pComment
		+ "; pQuantity= " + pQuantity
		+ "; pPrice= " + pBKPrice
		+ "; BKAccount= " + pBKAccount.getpEmailAddress();
	}

	/**
	 * 
	 * @return
	 */
	public final String getpLineInFile() {
		return pDate 
				+ "," + pBKAsset.getpName()
				+ "," + pComment
				+ "," + pQuantity
				+ "," + pBKAccount.getpEmailAddress()
				+ "," + pBKIncome
				+ "," + pFileNameOrigin
				+ "," + pBKPrice
				+ "," + pValueUSD;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getHeaderForFile() {
		return "Date,BKAsset,Comment,Quantity,BKAccount,BKIncome,File name origin,BKPrice,Value USD";
	}
	
	/*
	 * Getters & Setters
	 */	
	protected final void setpComment(String pComment) {
		this.pComment = pComment;
	}
	public final int getpDate() {
		return pDate;
	}
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final String getpComment() {
		return pComment;
	}
	public final double getpQuantity() {
		return pQuantity;
	}
	public final double getpValueUSD() {
		return pValueUSD;
	}
	public final String getpFileNameOrigin() {
		return pFileNameOrigin;
	}
	public final BKAccount getpBKAccount() {
		return pBKAccount;
	}
	public final String getpBKIncome() {
		return pBKIncome;
	}
	public final double getpBKPrice() {
		return pBKPrice;
	}






























}
