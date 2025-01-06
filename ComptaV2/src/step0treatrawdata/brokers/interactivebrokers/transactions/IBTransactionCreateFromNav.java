package step0treatrawdata.brokers.interactivebrokers.transactions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import staticdata.StaticBKIncome.BKI_HEDGING_SUB;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataManager;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataNav;
import step0treatrawdata.brokers.interactivebrokers.ibstatic.IBStatic;
import step0treatrawdata.objects.BKAssetManager;

public class IBTransactionCreateFromNav {

	protected IBTransactionCreateFromNav(IBTransactionManager _sIBTransactionManager) {
		pIBTransactionManager = _sIBTransactionManager;
	}
	
	/*
	 * Data
	 */
	private IBTransactionManager pIBTransactionManager;
	
	/**
	 * createIBTransactions
	 * @param _sIBDataManager
	 */
	protected final void createFromIBNav(IBDataManager _sIBDataManager) {
		List<IBDataNav> lListIBDataNav = new ArrayList<IBDataNav>(_sIBDataManager.getpMapDateToIBDataNav().values());
		Collections.sort(lListIBDataNav);
		for (int lIdx = 1; lIdx < lListIBDataNav.size(); lIdx++) {
			IBDataNav lIBDataNav0 = lListIBDataNav.get(lIdx - 1);
			IBDataNav lIBDataNav1 = lListIBDataNav.get(lIdx);
			/*
			 * Create IBTransaction
			 */
			double lAmount = lIBDataNav1.getpInterests() - lIBDataNav0.getpInterests();
			pIBTransactionManager.createIBTransactionFromNonTrade(lIBDataNav1.getpDate(), 
					IBStatic.getCOMMENT_INTERESTS(), BKAssetManager.getpBKAssetCurrencyReference(), 
					lAmount, Double.NaN, BKI_HEDGING_SUB.Interests);
		}
	}
	
}
