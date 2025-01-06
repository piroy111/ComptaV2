package step0treatrawdata.brokers.interactivebrokers.transactions;

import java.util.ArrayList;
import java.util.List;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.StaticBKIncome.BKI_HEDGING_SUB;
import step0treatrawdata.brokers.interactivebrokers.IBManager;
import step0treatrawdata.brokers.interactivebrokers.reports.IBReport;
import step0treatrawdata.objects.BKAsset;

public class IBTransactionManager {

	public IBTransactionManager(IBManager _sIBManager) {
		pIBManager = _sIBManager;
		/*
		 * Instantiate
		 */
		pIBTransactionCreateFromNav = new IBTransactionCreateFromNav(this);
		pIBTransactionCreateFromCash = new IBTransactionCreateFromCash(this);
		pIBTransactionCreateFromTrades = new IBTransactionCreateFromTrades(this);
	}

	/*
	 * Data
	 */
	private IBManager pIBManager;
	private int pDateStop;
	private int pDateStartLocal;
	private int pDateStartLocalTrades;
	private int pDateStopLocal;
	private List<IBTransaction> pListIBTransaction;
	private IBTransactionCreateFromNav pIBTransactionCreateFromNav;
	private IBTransactionCreateFromCash pIBTransactionCreateFromCash;
	private IBTransactionCreateFromTrades pIBTransactionCreateFromTrades;

	/**
	 * 
	 */
	public final void createIBTransactions() {
		/*
		 * Initiate
		 */
		List<IBReport> lListIBReportNew = pIBManager.getpIBReportManager().getpListIBReportNew();
		checkDateConsistency();
		pListIBTransaction = new ArrayList<>();
		/*
		 * 
		 */
		if (lListIBReportNew.size() > 0) {
			/*
			 * 
			 */
			IBReport lIBReportPrevious = pIBManager.getpIBReportManager().getpIBReportPrevious();
			for (IBReport lIBReport : lListIBReportNew) {
				/*
				 * Date start to filter the IBTransactions
				 */
				pDateStartLocal = -1;
				pDateStartLocalTrades = -1;
				if (lIBReportPrevious != null) {
					pDateStartLocal = Math.max(BasicDateInt.getmPlusDay(lIBReportPrevious.getpDateStop(), 1), 
							lIBReport.getpDateStart());
					pDateStartLocalTrades = BasicDateInt.getmPlusDay(pDateStartLocal, -1);
					////////////////////////////////////////////////////////////////////////////////
					pDateStartLocalTrades = pDateStartLocal;
					////////////////////////////////////////////////////////////////////////////////
				}
				pDateStopLocal = lIBReport.getpDateStop();
				pDateStop = Math.max(pDateStop, pDateStopLocal);
				/*
				 * Create IBTransactions
				 */
				pIBTransactionCreateFromNav.createFromIBNav(lIBReport.getpIBDataManager());
				pIBTransactionCreateFromCash.createFromCashReport(lIBReportPrevious, lIBReport);
				pIBTransactionCreateFromTrades.createFromIBTrade(lIBReport.getpIBDataManager());
				/*
				 * Move on
				 */
				lIBReportPrevious = lIBReport;
			}
		}
	}

	/**
	 * Check if there is a conflict
	 */
	private void checkDateConsistency() {
		List<IBReport> lListIBReportNew = pIBManager.getpIBReportManager().getpListIBReportNew();
		for (int lIdx = 0; lIdx < lListIBReportNew.size() - 1; lIdx++) {
			IBReport lIBReport0 = lListIBReportNew.get(lIdx);
			IBReport lIBReport1 = lListIBReportNew.get(lIdx + 1);
			if (lIBReport0.getpDateStart() < lIBReport1.getpDateStart()
					&& lIBReport1.getpDateStart() < lIBReport0.getpDateStop()) {
				BasicPrintMsg.error("Error");
			}
			if (lIBReport1.getpDateStart() < lIBReport0.getpDateStop()
					&& lIBReport0.getpDateStart() != lIBReport1.getpDateStart()) {
				BasicPrintMsg.error("Error");
			}
		}
	}

	/**
	 * 
	 * @param _sDate
	 * @param _sComment
	 * @param _sBKAsset
	 * @param _sAmount
	 * @param _sPrice
	 */
	protected final void createIBTransactionFromTrade(int _sDate, String _sComment, BKAsset _sBKAsset, double _sAmount, double _sPrice, BKI_HEDGING_SUB _sBKIncome) {
		if (pDateStartLocalTrades <= _sDate	&& _sDate <= pDateStopLocal) {
			createIBTransaction(_sDate, _sComment, _sBKAsset, _sAmount, _sPrice, _sBKIncome);
		}
	}
	
	/**
	 * 
	 * @param _sDate
	 * @param _sComment
	 * @param _sBKAsset
	 * @param _sAmount
	 * @param _sPrice
	 */
	protected final void createIBTransactionFromNonTrade(int _sDate, String _sComment, BKAsset _sBKAsset, double _sAmount, double _sPrice, BKI_HEDGING_SUB _sBKIncome) {
		if (pDateStartLocal <= _sDate	&& _sDate <= pDateStopLocal) {
			createIBTransaction(_sDate, _sComment, _sBKAsset, _sAmount, _sPrice, _sBKIncome);
		}
	}
	
	/**
	 * 
	 * @param _sDate
	 * @param _sComment
	 * @param _sBKAsset
	 * @param _sAmount
	 * @param _sPrice
	 */
	private final void createIBTransaction(int _sDate, String _sComment, BKAsset _sBKAsset, double _sAmount, double _sPrice, BKI_HEDGING_SUB _sBKIncome) {
		if (!AMNumberTools.isNaNOrZero(_sAmount)) {
			/*
			 * Create
			 */
			IBTransaction lIBTransaction = new IBTransaction(_sDate, _sComment, _sBKAsset, _sAmount, _sPrice,  _sBKIncome);
			pListIBTransaction.add(lIBTransaction);
		}
	}

	/*
	 * Getters & Setters
	 */
	public final List<IBTransaction> getpListIBTransaction() {
		return pListIBTransaction;
	}
	public final int getpDateStop() {
		return pDateStop;
	}





}
