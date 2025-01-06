package step2loans.createloanstransactions;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import step1loadtransactions.transactions.BKTransaction;
import step1loadtransactions.transactions.BKTransactionManager;
import step2loans.computeloancostscurrencies.LNCYManager;
import step2loans.computeloancostsyearly.LNCCManager;

public class LNManager {

	public LNManager() {
		pBKTransactionManager = new BKTransactionManager();
		pBKTransactionManager.loadBKTransactionsExcludingLoans();
		pListLNAbstract = new ArrayList<LNAbstract>();
		/*
		 * Create LNAbstract
		 */
		pLNCreateLoan = new LNCreateLoan(this);
		pLNOffsetLoan = new LNOffsetLoan(this);
		pLNCCManager = new LNCCManager(pBKTransactionManager);
		pLNCYManager = new LNCYManager(pBKTransactionManager);
	}

	/*
	 * Data
	 */
	private BKTransactionManager pBKTransactionManager;
	private LNCreateLoan pLNCreateLoan;
	private LNOffsetLoan pLNOffsetLoan;
	private int pDateLimitToStart;
	private List<LNAbstract> pListLNAbstract;
	private List<BKTransaction> pListBKTransactionNew;
	private LNCCManager pLNCCManager;
	private LNCYManager pLNCYManager;

	/**
	 * 
	 */
	public final void run(boolean _sIsWriteFile) {
		BasicPrintMsg.displayTitle(this, " create loans and offset them");
		/*
		 * Delete existing files of current month. Leave intact fields of previous months
		 */
		LNDeleteFilesCurrentMonth.run();
		pLNCCManager.initiate();
		pLNCYManager.initiate();
		/*
		 * Fill in the first BKTransactions from the client files, refiner, etc.
		 */
		System.out.println("From files already here");
		Integer lDateStart = null;
		for (LNAbstract lLNAbstract : pListLNAbstract) {
			lLNAbstract.initiateBackGround();
			if (lDateStart == null || lDateStart > lLNAbstract.getpDateLimitToStart()) {
				lDateStart = lLNAbstract.getpDateLimitToStart();
			}
		}
		if (lDateStart == null) {
			return;
		}
		/*
		 * Generate the BKInventory with the first BKTransactions
		 */
		for (LNAbstract lLNAbstract : pListLNAbstract) {
			lLNAbstract.generateBKHolder();
		}
		/*
		 * Initiate
		 */
		for (LNAbstract lLNAbstract : pListLNAbstract) {
			lLNAbstract.initiate();
		}
		/*
		 * Create and offset loans
		 */
		System.out.println("From computation");
		pListBKTransactionNew = new ArrayList<>();
		for (int lDate = lDateStart; lDate <=BasicDateInt.getmToday(); lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
			for (LNAbstract lLNAbstract : pListLNAbstract) {
				/*
				 * Reset
				 */
				pListBKTransactionNew.clear();
				/*
				 * Create new BKTransaction
				 */
				lLNAbstract.createBKTransactionBackground(lDate);
				/*
				 * Update present and future BKInventory
				 */
				if (pListBKTransactionNew.size() > 0) {
					for (LNAbstract lLNAbstractToUpdate : pListLNAbstract) {
						lLNAbstractToUpdate.declareNewBKTransactionAndRecomputeBKInventory(pListBKTransactionNew);
					}
				}
			}
		}
		/*
		 * Write files
		 */
		if (_sIsWriteFile) {
			for (LNAbstract lLNAbstract : pListLNAbstract) {
				lLNAbstract.writeFileCsv();
			}
		}
		/*
		 * Compute cost of loans yearly, load the transactions, and write the new files
		 */
		pLNCCManager.run();
		pLNCYManager.run();
	}

	/**
	 * 
	 * @param _sLNAbstract
	 */
	protected final void declareNewLNAbstract(LNAbstract _sLNAbstract) {
		pListLNAbstract.add(_sLNAbstract);
	}

	/**
	 * 
	 * @param _sBKTransaction
	 */
	protected final void declareNewBKTransaction(BKTransaction _sBKTransaction) {
		pListBKTransactionNew.add(_sBKTransaction);
	}

	/*
	 * Getters & Setters
	 */
	public final BKTransactionManager getpBKTransactionManager() {
		return pBKTransactionManager;
	}
	public final LNCreateLoan getpLNCreateLoan() {
		return pLNCreateLoan;
	}
	public final LNOffsetLoan getpLNOffsetLoan() {
		return pLNOffsetLoan;
	}
	public final int getpDateLimitToStart() {
		return pDateLimitToStart;
	}












}

