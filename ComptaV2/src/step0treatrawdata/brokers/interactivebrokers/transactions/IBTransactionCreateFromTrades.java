package step0treatrawdata.brokers.interactivebrokers.transactions;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicPrintMsg;
import staticdata.StaticAsset;
import staticdata.StaticBKIncome.BKI_HEDGING_SUB;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataManager;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataTrade;
import step0treatrawdata.brokers.interactivebrokers.ibstatic.IBStatic;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;

public class IBTransactionCreateFromTrades {

	protected IBTransactionCreateFromTrades(IBTransactionManager _sIBTransactionManager) {
		pIBTransactionManager = _sIBTransactionManager;
	}
	
	/*
	 * Data
	 */
	private IBTransactionManager pIBTransactionManager;
	
	/**
	 * 
	 * @param _sIBDataManager
	 */
	protected final void createFromIBTrade(IBDataManager _sIBDataManager) {
		List<IBDataTrade> lListIBDataTrade = new ArrayList<IBDataTrade>(_sIBDataManager.getpMapIDToIBDataTrade().values());
		for (int lIdx = 0; lIdx < lListIBDataTrade.size(); lIdx++) {
			IBDataTrade lIBDataTrade = lListIBDataTrade.get(lIdx);
			/*
			 * Create a IBTransaction for the commissions
			 */
			BKAsset lBKAssetCommissions = BKAssetManager.getpAndCheckBKAsset(lIBDataTrade.getpCommissionsCurrency());
			pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_COMMISSIONS(), 
					lBKAssetCommissions, lIBDataTrade.getpCommissions(), Double.NaN, BKI_HEDGING_SUB.Commissions);
			/*
			 * Case of a CASH
			 */
			if (lIBDataTrade.getpAssetClass().equals("CASH")) {
				String[] lArray = lIBDataTrade.getpSymbol().split("\\.");
				BKAsset 	lBKAssetLeft = BKAssetManager.getpAndCheckBKAsset(lArray[0]);
				BKAsset 	lBKAssetRight = BKAssetManager.getpAndCheckBKAsset(lArray[1]);
				double lQuantityLeft = lIBDataTrade.getpQuantityExec();
				double lQuantityRight = -lIBDataTrade.getpQuantityExec() * lIBDataTrade.getpPriceExec();
				/*
				 * Create
				 */
				pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_FOREX(), lBKAssetLeft, lQuantityLeft, Double.NaN, BKI_HEDGING_SUB.Trades);
				pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_FOREX(), lBKAssetRight, lQuantityRight, Double.NaN, BKI_HEDGING_SUB.Trades);
			}
			/*
			 * Case of a SWAP
			 */
			else {
				BKAsset lBKAsset = null;
				double lAmount = lIBDataTrade.getpQuantityExec();
				double lPrice = lIBDataTrade.getpPriceExec();
				String lSymbol = lIBDataTrade.getpSymbol();
				/*
				 * Case of a currency swap
				 */
				if (lIBDataTrade.getpAssetClass().equals("FXCFD")) {
					if (lSymbol.startsWith("USD")) {
						lAmount = -lAmount * lPrice;
						lPrice = 1 / lPrice;
						lBKAsset = BKAssetManager.getpAndCheckBKAsset("X" + lSymbol.substring(4, 7));
					} else if (lSymbol.endsWith("USD")) {
						lBKAsset = BKAssetManager.getpAndCheckBKAsset("X" + lSymbol.substring(0, 3));
					}
					/*
					 * Create
					 */
					pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_FOREX_SWAPS(), lBKAsset, lAmount, lPrice, BKI_HEDGING_SUB.Trades);
				} 
				/*
				 * Case of a paper
				 */
				else if (lIBDataTrade.getpAssetClass().equals("CMDTY")) {
					lBKAsset = BKAssetManager.getpAndCheckBKAsset(lSymbol.substring(0, 3));
					/*
					 * Create
					 */
					pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_METAL_SWAPS(), lBKAsset, lAmount, lPrice, BKI_HEDGING_SUB.Trades);
				}
				/*
				 * Case of a future OIL
				 */
				else if (lIBDataTrade.getpAssetClass().equals("FUT") && lSymbol.startsWith(IBStatic.getOIL())) {
					lBKAsset = BKAssetManager.getpAndCheckBKAsset(StaticAsset.getOIL());
					/*
					 * Create
					 */
					pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_OIL(), lBKAsset, lAmount, lPrice, BKI_HEDGING_SUB.Trades);
				}
				/*
				 * Check
				 */
				else {
					BasicPrintMsg.error("Unknown symbol; Symbol= " + lSymbol);
				}
			}
		}
	}

	
}
