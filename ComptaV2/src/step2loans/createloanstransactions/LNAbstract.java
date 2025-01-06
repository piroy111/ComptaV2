package step2loans.createloanstransactions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicDir;
import basicmethods.BasicFichiers;
import basicmethods.BasicFile;
import staticdata.StaticBKIncome;
import staticdata.StaticDate;
import step0treatrawdata.objects.BKAsset;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.transactions.BKTransaction;

public abstract class LNAbstract extends BKHolderGenerator {

	public LNAbstract(String _sDir, String _sNameFileSuffix, LNManager _sLNManager) {
		super(_sLNManager.getpBKTransactionManager());
		pDir = _sDir;
		pNameFileSuffix = _sNameFileSuffix;
		pLNManager = _sLNManager;
		/*
		 * 
		 */
		pLNManager.declareNewLNAbstract(this);
	}

	/*
	 * Abstract
	 */
	public abstract void initiate();
	public abstract void createBKTransaction(int _sDate);
	/*
	 * Data
	 */
	protected LNManager pLNManager;
	private String pDir;
	private String pNameFileSuffix;
	private List<BKTransaction> pListBKTransactionToWriteInFile;
	private int pDateLimitToStart;
	private Integer pDateThisFile;

	/**
	 * 
	 */
	public final void initiateBackGround() {
		treatFilesAlreadyThere();
		generateBKHolder();
		pListBKTransactionToWriteInFile = new ArrayList<>();
		initiate();
	}
	
	/**
	 * 
	 * @param _sDate
	 */
	public final void createBKTransactionBackground(int _sDate) {
		if (_sDate > pDateLimitToStart) {
			createBKTransaction(_sDate);
		}
	}

	/**
	 * @return the date maximum of the files
	 * @param _sDir
	 * @param _sSuffix
	 * @return
	 */
	protected final void treatFilesAlreadyThere() {
		/*
		 * Load BasicDir
		 */
		BasicDir lBasicDir = new BasicDir(pDir, pNameFileSuffix);
		/*
		 * Check the date of the file we want to build today
		 */
		pDateThisFile = StaticDate.getDATE_MAX();
		if (pDateThisFile == -1) {
			pDateThisFile = BasicDateInt.getmToday();
		}
		/*
		 * Return the greatest date, knowing that we rewrite the last file
		 */
		pDateLimitToStart = -1;
		for (int lDate : lBasicDir.getmListDate()) {
			if (pDateLimitToStart < lDate && lDate < pDateThisFile) {
				pDateLimitToStart = lDate;
			}
		}
		/*
		 * Treat files
		 */
		for (BasicFile lBasicFile : lBasicDir.getmMapDateToBasicFile().values()) {
			if (lBasicFile.getmDate() <= pDateLimitToStart) {
				pLNManager.getpBKTransactionManager().readFile(lBasicFile.getmLitUnFichierEnLignes(), true);
			}
		}
	}

	/**
	 * Write file CSV of the new BKTransactions due to the loans
	 */
	public final void writeFileCsv() {
		/*
		 * Check the date
		 */
		int lDate = StaticDate.getDATE_MAX();
		if (lDate == -1) {
			lDate = BasicDateInt.getmToday();
		}
		/*
		 * Sort
		 */
		Collections.sort(pListBKTransactionToWriteInFile);
		/*
		 * Header
		 */
		String lHeader = "Date,Comment,BKAsset,Amount,BKAccount,BKIncome,BKPrice";
		String lNameFile = pDateThisFile + pNameFileSuffix;
		/*
		 * Content
		 */
		List<String> lListLineToWrite = new ArrayList<String>();
		Collections.sort(pListBKTransactionToWriteInFile);
		for (BKTransaction lBKTransaction : pListBKTransactionToWriteInFile) {
			String lLine = lBKTransaction.getpDate()
					+ "," + lBKTransaction.getpComment()
					+ "," + lBKTransaction.getpBKAsset()
					+ "," + lBKTransaction.getpQuantity()
					+ "," + lBKTransaction.getpBKAccount().getpEmailAddress()
					+ "," + lBKTransaction.getpBKIncome()
					+ ",NaN";
			lListLineToWrite.add(lLine);
		}
		/*
		 * Write File
		 */
		BasicFichiers.writeFile(pDir, lNameFile, lHeader, lListLineToWrite);
	}

	/**
	 * Create the BKTransaction and add it to the list
	 * @param _sDate
	 * @param _sComment
	 * @param _sBKAsset
	 * @param _sQuantity
	 * @param _sBKAccount
	 * @param _sBKIncome
	 */
	protected final void createBKTransaction(int _sDate, BKAsset _sBKAsset, String _sComment, 
			double _sQuantity, BKAccount _sBKAccount, String _sBKIncome) {
		if (_sBKIncome == null) {
			_sBKIncome = StaticBKIncome.getLOAN(_sBKAsset);
		}
		BKTransaction lBKTransaction = pLNManager.getpBKTransactionManager()
				.createBKTransaction(_sDate, _sComment, _sBKAsset, _sQuantity, 
						_sBKAccount, _sBKIncome, Double.NaN, this.getClass().getSimpleName());
		if (lBKTransaction != null) {
			pListBKTransactionToWriteInFile.add(lBKTransaction);
			pLNManager.declareNewBKTransaction(lBKTransaction);
		}
	}

	/*
	 * Getters & Setters
	 */
	public final int getpDateLimitToStart() {
		return pDateLimitToStart;
	}



}
