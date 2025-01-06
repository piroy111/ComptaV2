package step0treatrawdata.brokers.interactivebrokers.reports;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataCash;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataClosePrice;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataManager;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataNav;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataTrade;
import step0treatrawdata.brokers.interactivebrokers.files.IBFile;
import step0treatrawdata.brokers.interactivebrokers.ibstatic.IBStatic;

public class IBReport {

	protected IBReport(IBReportManager _sIBReportManager, IBFile _sIBFileReport) {
		pIBReportManager = _sIBReportManager;
		pIBFile = _sIBFileReport;
	}
	
	/*
	 * Data
	 */
	private IBReportManager pIBReportManager;
	private IBFile pIBFile;
	private int pDateStart;
	private int pDateStop;
	private IBDataManager pIBDataManager;
	
	/**
	 * read & load the IBFile
	 */
	public final void loadFile() {
		pIBDataManager = new IBDataManager(pIBFile.getpDateStop());
		LitUnFichierEnLignes lReadFile = pIBFile.getpReadFile();
		List<List<String>> lListLineStr = lReadFile.getmContenuFichierListe();
		int lIdxLine = -1;
		/*
		 * Check dates
		 */
		checkDates(lListLineStr.get(++lIdxLine));
		/*
		 * Read all lines
		 */
		while (++lIdxLine < lListLineStr.size()) {
			List<String> lLineStr = lListLineStr.get(lIdxLine);
			if (lLineStr.get(0).equals("BOS")) {
				String lNameReport = lLineStr.get(2);
				++lIdxLine;
				/*
				 * Group all the lines of the same sub report
				 */
				List<List<String>> lListLineInSubReport = new ArrayList<>();
				while (!lListLineStr.get(++lIdxLine).get(0).equals("EOS")) {
					lListLineInSubReport.add(lListLineStr.get(lIdxLine));
				}
				/*
				 * Read sub-report
				 */
				if (lNameReport.equals(IBStatic.getREPORT_NAV())) {
					fillNAV(lListLineInSubReport);
				} else if (lNameReport.equals(IBStatic.getREPORT_MTM())) {
					fillCloseValues(lListLineInSubReport);
				} else if (lNameReport.equals(IBStatic.getREPORT_CASH())) {
					fillCashReport(lListLineInSubReport);
				} else if (lNameReport.equals(IBStatic.getREPORT_TRADES())) {
					fillTradesReport(lListLineInSubReport);
				} else {
					BasicPrintMsg.error("Unknown sub report; lNameReport= " + lNameReport);
				}
			}
		}
	}

	/**
	 * Fill and check the dates
	 */
	private void checkDates(List<String> _sLineStr) {
		pDateStart = pIBFile.getpDateStart();
		pDateStop = pIBFile.getpDateStop();
		int lDateStart = BasicString.getInt(_sLineStr.get(4));
		int lDateStop = BasicString.getInt(_sLineStr.get(5));
		if (lDateStart != pDateStart || lDateStop != pDateStop) {
			BasicPrintMsg.error("Date start and stop in the file dont match the name of the IBFile"
					+ "\nIBFile= " + pIBFile.getpPath().toString()
					+ "\nDateStart in IBFile= " + lDateStart
					+ "\nDateStop in IBFile= " + lDateStop);
		}
	}
	
	/**
	 * fillNAV
	 */
	private void fillNAV(List<List<String>> _sListLineStr) {
		for (List<String> lLineStr : _sListLineStr) {
			/*
			 * Load
			 */
			int lIdx = -1;
			int lReportDate = BasicString.getInt(lLineStr.get(++lIdx));
			double lInterest = BasicString.getDouble(lLineStr.get(++lIdx));
			double lTotal = BasicString.getDouble(lLineStr.get(++lIdx));
			/*
			 * Store data
			 */
			IBDataNav lIBDataNav = pIBDataManager.getpOrCreateIBDataNav(lReportDate);
			lIBDataNav.addNewDatas(lInterest, lTotal);
		}
	}
	
	/**
	 * fillCloseValues
	 * @param _sListLineStr
	 */
	private void fillCloseValues(List<List<String>> _sListLineStr) {
		for (List<String> lLineStr : _sListLineStr) {
			/*
			 * Load
			 */
			int lIdx = -1;
			String lSymbol = lLineStr.get(++lIdx);
			double lClosePrice = BasicString.getDouble(lLineStr.get(++lIdx));
			/*
			 * Store data
			 */
			IBDataClosePrice lIBDataClosePrice = pIBDataManager.getpOrCreateIBDataClosePrice(lSymbol);
			lIBDataClosePrice.setpValue(lClosePrice);
			lIBDataClosePrice.setpDate(pDateStop);
		}
	}
	
	/**
	 * Fill values of cash report
	 */
	private void fillCashReport(List<List<String>> _sListLineStr) {
		for (List<String> lLineStr : _sListLineStr) {
			/*
			 * Load
			 */
			int lIdx = -1;
			String lCurrency = lLineStr.get(++lIdx);
			double lDeposits = BasicString.getDouble(lLineStr.get(++lIdx));
			double lBrokerInterests = BasicString.getDouble(lLineStr.get(++lIdx));
			double lOtherFees = BasicString.getDouble(lLineStr.get(++lIdx));
			double lSalesTax = getValue(lLineStr, ++lIdx);
			double lCFDCharges = getValue(lLineStr, ++lIdx);
			/*
			 * Special skip
			 */
			if (lCurrency.equals(IBStatic.getBASE())) {
				continue;
			}
			/*
			 * Store data
			 */
			IBDataCash lIBDataCash = pIBDataManager.getpOrCreateIBDataCash(lCurrency);
			lIBDataCash.addNewDatas(lDeposits, lBrokerInterests, lOtherFees, lSalesTax, lCFDCharges);
		}
	}

	/**
	 * 
	 * @param _sListString
	 * @param _sIdx
	 * @return
	 */
	private double getValue(List<String> _sListString, int _sIdx) {
		if (_sIdx < _sListString.size()) {
			return BasicString.getDouble(_sListString.get(_sIdx));
		} else {
			return 0.;
		}
	}
	
	/**
	 * fillTradesReport
	 * @param _sListLineStr
	 */
	private void fillTradesReport(List<List<String>> _sListLineStr) {
		for (List<String> lLineStr : _sListLineStr) {
			/*
			 * Load
			 */
			int lIdx = -1;
			String lAssetClass = lLineStr.get(++lIdx);
			String lSymbol = lLineStr.get(++lIdx);
			String lTradeID = lLineStr.get(++lIdx);
			int lTradeDate = BasicString.getInt(lLineStr.get(++lIdx));
			double lQuantity = BasicString.getDouble(lLineStr.get(++lIdx));
			double lPrice = BasicString.getDouble(lLineStr.get(++lIdx));
			double lCommissions = BasicString.getDouble(lLineStr.get(++lIdx));
			String lCommissionCurrency = lLineStr.get(++lIdx);
			/*
			 * Case not supported
			 */
			if (!lCommissionCurrency.equals("USD")) {
				BasicPrintMsg.error("This case is not supported. We need a new dev to support currencies in USD");
			}
			/*
			 * Store Trades
			 */
			IBDataTrade lIBDataTrade = pIBDataManager.getpOrCreateIBDataTrade(lTradeID);
			lIBDataTrade.addNewData(lAssetClass, lSymbol, lTradeDate, lQuantity, lPrice, 
					lCommissions, lCommissionCurrency);
		}
	}
	
	/**
	 * Classic toString
	 */
	public final String toString() {
		if (pIBFile == null) {
			return null;
		}
		return pIBFile.getpPath().getFileName().toString();
	}
	
	/*
	 * Getters & Setters
	 */
	public final IBReportManager getpIBReportManager() {
		return pIBReportManager;
	}
	public final IBFile getpIBFile() {
		return pIBFile;
	}
	public final int getpDateStart() {
		return pDateStart;
	}
	public final int getpDateStop() {
		return pDateStop;
	}
	public final IBDataManager getpIBDataManager() {
		return pIBDataManager;
	}
	
	
}
