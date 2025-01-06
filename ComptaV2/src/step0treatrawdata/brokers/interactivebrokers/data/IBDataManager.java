package step0treatrawdata.brokers.interactivebrokers.data;

import java.util.HashMap;
import java.util.Map;

import basicmethods.BasicPrintMsg;

public class IBDataManager {

	public IBDataManager(int _sDate) {
		pDate = _sDate;
		/*
		 * Initiate
		 */
		pMapDateToIBDataNav = new HashMap<>();
		pMapSymbolToIBDataClosePrice = new HashMap<>();
		pMapSymbolToIBDataCash = new HashMap<>();
		pMapIDToIBDataTrade = new HashMap<>();
	}

	/*
	 * Data
	 */
	private int pDate;
	private Map<Integer, IBDataNav> pMapDateToIBDataNav;
	private Map<String, IBDataClosePrice> pMapSymbolToIBDataClosePrice;
	private Map<String, IBDataCash> pMapSymbolToIBDataCash;
	private Map<String, IBDataTrade> pMapIDToIBDataTrade;

	/**
	 * update the data with the more recent IBDataManager
	 * @param _sIBDataManager
	 */
	public final void updateWithMoreRecent(IBDataManager _sIBDataManager) {
		if (_sIBDataManager != null) {
			/*
			 * Check
			 */
			if (_sIBDataManager.getpDate() < pDate) {
				BasicPrintMsg.error("Error");
			}
			/*
			 * NAV
			 */
			for (IBDataNav lIBDataNavNew : _sIBDataManager.getpMapDateToIBDataNav().values()) {
				if (!pMapDateToIBDataNav.containsKey(lIBDataNavNew.getpDate())) {
					IBDataNav lIBDataNav = getpOrCreateIBDataNav(lIBDataNavNew.getpDate());
					lIBDataNav.setpInterests(lIBDataNavNew.getpInterests());
					lIBDataNav.setpNav(lIBDataNavNew.getpNav());
				}
			}
			/*
			 * MTM
			 */
			pMapSymbolToIBDataClosePrice = new HashMap<>(_sIBDataManager.getpMapSymbolToIBDataClosePrice());
			/*
			 * Trades
			 */
			for (IBDataTrade lIBDataTradeNew : _sIBDataManager.getpMapIDToIBDataTrade().values()) {
				if (pMapIDToIBDataTrade.containsKey(lIBDataTradeNew.getpID())) {
					IBDataTrade lIBDataTrade = getpOrCreateIBDataTrade(lIBDataTradeNew.getpID());
					lIBDataTrade.clone(lIBDataTradeNew);
				}
			}
			/*
			 * Date
			 */
			pDate = _sIBDataManager.getpDate();
		}
	}


	/*
	 * Classic get or create
	 */
	public final IBDataNav getpOrCreateIBDataNav(int _sDate) {
		IBDataNav lIBDataNav = pMapDateToIBDataNav.get(_sDate);
		if (lIBDataNav == null) {
			lIBDataNav = new IBDataNav(_sDate);
			pMapDateToIBDataNav.put(_sDate, lIBDataNav);
		}
		return lIBDataNav;
	}
	public final IBDataClosePrice getpOrCreateIBDataClosePrice(String _sSymbol) {
		IBDataClosePrice lIBDataClosePrice = pMapSymbolToIBDataClosePrice.get(_sSymbol);
		if (lIBDataClosePrice == null) {
			lIBDataClosePrice = new IBDataClosePrice( _sSymbol);
			pMapSymbolToIBDataClosePrice.put(_sSymbol, lIBDataClosePrice);
		}
		return lIBDataClosePrice;
	}
	public final IBDataCash getpOrCreateIBDataCash(String _sSymbol) {
		IBDataCash lIBDataCash = pMapSymbolToIBDataCash.get(_sSymbol);
		if (lIBDataCash == null) {
			lIBDataCash = new IBDataCash( _sSymbol);
			pMapSymbolToIBDataCash.put(_sSymbol, lIBDataCash);
		}
		return lIBDataCash;
	}
	public final IBDataTrade getpOrCreateIBDataTrade(String _sTradeID) {
		IBDataTrade lIBDataTrade = pMapIDToIBDataTrade.get(_sTradeID);
		if (lIBDataTrade == null) {
			lIBDataTrade = new IBDataTrade( _sTradeID);
			pMapIDToIBDataTrade.put(_sTradeID, lIBDataTrade);
		}
		return lIBDataTrade;
	}



	/*
	 * Getters & Setters
	 */
	public final Map<Integer, IBDataNav> getpMapDateToIBDataNav() {
		return pMapDateToIBDataNav;
	}
	public final Map<String, IBDataClosePrice> getpMapSymbolToIBDataClosePrice() {
		return pMapSymbolToIBDataClosePrice;
	}
	public final Map<String, IBDataCash> getpMapSymbolToIBDataCash() {
		return pMapSymbolToIBDataCash;
	}
	public final Map<String, IBDataTrade> getpMapIDToIBDataTrade() {
		return pMapIDToIBDataTrade;
	}
	public final int getpDate() {
		return pDate;
	}

}
