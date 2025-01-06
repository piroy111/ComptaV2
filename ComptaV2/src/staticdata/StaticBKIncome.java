package staticdata;

import step0treatrawdata.objects.BKAsset;

public class StaticBKIncome {

	/*
	 * Miscellaneous
	 */
	private static String CAPITAL = "Capital";
	private static String CLIENT_BARS = "Bars";
	/*
	 * Hedge
	 */
	public static enum BKI_HEDGING_BROKERS {InteractiveBrokers, IFCM, OANDA, HotForex, Argentor};
	public static enum BKI_HEDGING_SUB {Cash_wire_in, Cash_wire_out, Trades, Interests, Commissions, Unexpected_loss, Directional, Refiner};
	public static enum BKI_CRYPTO_BROKERS {OANDA, Bitstamp, Kraken, Bitfinex};
	/*
	 * Revenue
	 */
	private static String HEDGING = "Hedging";
	private static String CRYPTOS = "Cryptos";
	private static String CLIENT_CASH_IN = "Operations_Incoming_funds_from_client";
	private static String CLIENT_CASH_SPENDING = "Operations_Cash_spending_from_clients";
	private static String BARS_BUNKER = "Operations_Bars_held_by_Bunker";
	private static String BARS_BUNKER_BOUGHT_FROM_REFINER = "Operations_Bars_bought_by_Bunker_from_refiners";
	private static String OP_CASH_GIVEN_BACK_FOR_LOAN = "Operations_Cash_given_back_to_offset_loan";
	private static String COST_SPENDING_COMMERCIAL = "Cost_Spending_Commercial";
	private static String COST_PHONE = "Cost_Phone";
	private static String CONDOR_OUT = "Operations_Condor_Out";
	private static String CONDOR_IN = "Operations_Condor_In";
	private static String WIRE_OUT_REFINERS = "Operations_Wire_out_to_refiners";
	private static String INTERNAL_TRANSFER = "Cost_Internal_transfer_bank_accounts";
	private static String LOAN_COST = "Loan_cost";
	private static String LOAN_COST_CURRENCY = "Loan_cost_currency";
	private static String LOAN = "Loan";

	/**
	 * 
	 * @param _sBKAsset
	 * @return
	 */
	public static final String getLOAN(BKAsset _sBKAsset) {
		if (_sBKAsset.getpIsBar()) {
			return getBARS_BUNKER();
		} else if (_sBKAsset.getpIsBarLoan()) {
			String lMetal = _sBKAsset.getpMetalName();
			return LOAN + " " + lMetal;
		} else {
			return getOP_CASH_GIVEN_BACK_FOR_LOAN();
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public static final String getHEDGING() {
		return HEDGING;
	}
	public static final String getCLIENT_CASH_IN() {
		return CLIENT_CASH_IN;
	}
	public static final String getCLIENT_CASH_SPENDING() {
		return CLIENT_CASH_SPENDING;
	}
	public static final String getCAPITAL() {
		return CAPITAL;
	}
	public static final String getCLIENT_BARS() {
		return CLIENT_BARS;
	}
	public static final String getCRYPTOS() {
		return CRYPTOS;
	}
	public static final String getOP_CASH_GIVEN_BACK_FOR_LOAN() {
		return OP_CASH_GIVEN_BACK_FOR_LOAN;
	}
	public static final String getBARS_BUNKER() {
		return BARS_BUNKER;
	}
	public static final String getBARS_BUNKER_BOUGHT_FROM_REFINER() {
		return BARS_BUNKER_BOUGHT_FROM_REFINER;
	}
	public static final String getCOST_SPENDING_COMMERCIAL() {
		return COST_SPENDING_COMMERCIAL;
	}
	public static final String getCOST_PHONE() {
		return COST_PHONE;
	}
	public static final String getCONDOR_OUT() {
		return CONDOR_OUT;
	}
	public static final String getCONDOR_IN() {
		return CONDOR_IN;
	}
	public static String getWIRE_OUT_REFINERS() {
		return WIRE_OUT_REFINERS;
	}
	public static String getINTERNAL_TRANSFER() {
		return INTERNAL_TRANSFER;
	}
	public static final String getLOAN_COST() {
		return LOAN_COST;
	}
	public static final String getLOAN() {
		return LOAN;
	}
	public static final String getLOAN_COST_CURRENCY() {
		return LOAN_COST_CURRENCY;
	}
	
	
}
