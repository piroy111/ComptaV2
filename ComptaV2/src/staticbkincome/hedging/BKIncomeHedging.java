package staticbkincome.hedging;

import staticdata.StaticBKIncome.BKI_HEDGING_BROKERS;
import staticdata.StaticBKIncome.BKI_HEDGING_SUB;

public class BKIncomeHedging {

	protected BKIncomeHedging(String _sSubHedging, String _sBroker) {
		pSubHedging = _sSubHedging;
		pBroker = _sBroker;
		/*
		 * 
		 */
		pBKIBroker = BKIncomeHedgingManager.getAndcheckBroker(pBroker);
		pBKISubHedging = BKIncomeHedgingManager.getAndcheckSubHedging(pSubHedging);
		pKeyStr = BKIncomeHedgingManager.getBKIncomeStr(_sBroker, _sSubHedging);
	}
	
	/*
	 * Data
	 */
	private String pSubHedging;
	private String pBroker;
	private String pKeyStr;
	private BKI_HEDGING_BROKERS pBKIBroker;
	private BKI_HEDGING_SUB pBKISubHedging;
	
	/*
	 * Getters & Setters
	 */
	public final String getpSubHedging() {
		return pSubHedging;
	}
	public final String getpBroker() {
		return pBroker;
	}
	public final String getpKeyStr() {
		return pKeyStr;
	}
	public final BKI_HEDGING_BROKERS getpBKIBroker() {
		return pBKIBroker;
	}
	public final BKI_HEDGING_SUB getpBKISubHedging() {
		return pBKISubHedging;
	}
	
	
	
}
