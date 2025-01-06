	package staticbkincome.hedging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import staticdata.StaticBKIncome;
import staticdata.StaticBKIncome.BKI_HEDGING_BROKERS;
import staticdata.StaticBKIncome.BKI_HEDGING_SUB;

public class BKIncomeHedgingManager {

	/*
	 * Data
	 */
	private static Map<String, Map<String, BKIncomeHedging>> pMapBrokersToMapSubHedgingToBKIncome;
	private static List<String> pListBrokers;
	private static List<String> pListSubHedgings;

	/**
	 * Load all possible values of BKIncome
	 */
	private static void loadMap() {
		if (pMapBrokersToMapSubHedgingToBKIncome == null) {
			/*
			 * Build lists
			 */
			pListBrokers = new ArrayList<String>();
			for (BKI_HEDGING_BROKERS lBroker : BKI_HEDGING_BROKERS.values()) {
				pListBrokers.add(lBroker.toString().replaceAll("_", " " ));
			}
			pListSubHedgings = new ArrayList<String>();
			for (BKI_HEDGING_SUB lSub : BKI_HEDGING_SUB.values()) {
				pListSubHedgings.add(lSub.toString().replaceAll("_", " " ));
			}
			/*
			 * Build Map
			 */
			pMapBrokersToMapSubHedgingToBKIncome = new HashMap<>();
			for (String lBroker : pListBrokers) {
				Map<String, BKIncomeHedging> lMapSubIncomeToBKIncomeHedging = new HashMap<String, BKIncomeHedging>();
				pMapBrokersToMapSubHedgingToBKIncome.put(lBroker, lMapSubIncomeToBKIncomeHedging);
				for (String lComment : pListSubHedgings) {
					BKIncomeHedging lBKIncomeHedging = new BKIncomeHedging(lComment, lBroker);
					lMapSubIncomeToBKIncomeHedging.put(lComment, lBKIncomeHedging);
				}
			}
		}
	}

	/**
	 * @return if the sub-BKIncome is valid
	 * @param _sSubHedging
	 */
	public static BKI_HEDGING_SUB getAndcheckSubHedging(String _sSubHedging) {
		loadMap();
		if (!pListSubHedgings.contains(_sSubHedging)) {
			BasicPrintMsg.error("The sub-BKIncome does not exist; _sSubHedging= " + _sSubHedging
					+ "\nList of sub-BKIncome valid= " + pListSubHedgings);
		}
		return BKI_HEDGING_SUB.valueOf(_sSubHedging.replaceAll(" ", "_"));
	}

	/**
	 * @return if the Broker is valid
	 * @param _sBroker
	 */
	public static BKI_HEDGING_BROKERS getAndcheckBroker(String _sBroker) {
		loadMap();
		if (!pListBrokers.contains(_sBroker)) {
			BasicPrintMsg.error("The broker does not exist; _sBroker= " + _sBroker
					+ "\nList of brokers valid= " + pListBrokers);
		}
		return BKI_HEDGING_BROKERS.valueOf(_sBroker);
	}

	/**
	 * @return build the String for the BKIncomeHedging
	 * @param _sBroker
	 * @param _sComment
	 */
	public static String getBKIncomeStr(String _sBroker, String _sSubHedging) {
		loadMap();
		getAndcheckBroker(_sBroker);
		getAndcheckSubHedging(_sSubHedging);
		return StaticBKIncome.getHEDGING() + " " + _sBroker + " " + _sSubHedging;
	}
	public static String getBKIncomeStr(String _sBroker, BKI_HEDGING_SUB _sSubHedging) {
		return getBKIncomeStr(_sBroker, _sSubHedging.toString().replaceAll("_", " "));
	}
	public static String getBKIncomeStr(BKI_HEDGING_BROKERS _sBroker, BKI_HEDGING_SUB _sSubHedging) {
		return getBKIncomeStr(_sBroker.toString().replaceAll("_", " "), _sSubHedging.toString().replaceAll("_", " "));
	}
	public static String getBKIncomeStr(BKI_HEDGING_BROKERS _sBroker, String _sSubHedging) {
		return getBKIncomeStr(_sBroker.toString().replaceAll("_", " "), _sSubHedging);
	}


	/**
	 * @return Invert function to give the details of the BKIncomeHedging
	 * @param _sBKIncomeHedgingStr
	 */
	public static BKIncomeHedging getBKIncomeHedging(String _sBKIncomeHedgingStr) {
		loadMap();
		/*
		 * Load
		 */
		String[] lArray = _sBKIncomeHedgingStr.split(" ", 3);		
		int lIdx = -1;
		String lHedging = lArray[++lIdx];
		String lBroker = lArray[++lIdx];
		String lComment = lArray[++lIdx];
		/*
		 * Check
		 */
		if (!lHedging.equals(StaticBKIncome.getHEDGING())) {
			BasicPrintMsg.error("This BKIncome is not a hedging; _sBKIncomeHedgingStr=  " + _sBKIncomeHedgingStr);
		}
		getAndcheckBroker(lBroker);
		getAndcheckSubHedging(lComment);
		return pMapBrokersToMapSubHedgingToBKIncome.get(lBroker).get(lComment);
	}

}
