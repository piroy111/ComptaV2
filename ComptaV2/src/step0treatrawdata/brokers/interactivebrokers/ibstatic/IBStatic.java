package step0treatrawdata.brokers.interactivebrokers.ibstatic;

public class IBStatic {

	/*
	 * Reports
	 */
	private static String REPORT_NAV = "Net Asset Value (NAV) in Base; trade date basis";
	private static String REPORT_MTM = "Mark-to-Market Performance Summary in Base";
	private static String REPORT_CASH = "Cash Report; trade date basis";
	private static String REPORT_TRADES = "Trades; trade date basis";
	/*
	 * 
	 */
	private static String BASE = "BASE_SUMMARY";
	private static String BKINCOME = "InteractiveBrokers";
	private static String CASH = "CASH";
	/*
	 * Comments for BKTransactions
	 */
	private static String COMMENT_INTERESTS = "Interests";
	private static String COMMENT_DEPOSITS = "Deposits";
	private static String COMMENT_COMMISSIONS = "Commissions";
	private static String COMMENT_FOREX = "Forex real";
	private static String COMMENT_FOREX_SWAPS = "Forex swaps";
	private static String COMMENT_METAL_SWAPS = "Metal swaps";
	private static String COMMENT_OIL = "Oil";
	/*
	 * Error for reconciliation
	 */
	private static double ERROR_ACCEPTED = 0.1;
	/*
	 * Miscellaneous
	 */
	private static String OIL = "COIL";
	private static int OIL_MULTIPLIER = 1000;
	/*
	 * Debug
	 */
	private static String NAME_FILE_DEBUG_BKTRANSACTION = "Debug_InteractiveBrokers_List_all_BKTransactions.csv";
	private static String NAME_FILE_DEBUG_IB_REPORTS = "Debug_InteractiveBrokers_Data_from_IBReports.csv";
	
	
	/*
	 * Getters & Setters
	 */
	public static final String getREPORT_NAV() {
		return REPORT_NAV;
	}
	public static final String getREPORT_MTM() {
		return REPORT_MTM;
	}
	public static final String getREPORT_CASH() {
		return REPORT_CASH;
	}
	public static final String getREPORT_TRADES() {
		return REPORT_TRADES;
	}
	public static final String getBASE() {
		return BASE;
	}
	public static final String getBKINCOME() {
		return BKINCOME;
	}
	public static final String getCOMMENT_INTERESTS() {
		return COMMENT_INTERESTS;
	}
	public static final String getCOMMENT_DEPOSITS() {
		return COMMENT_DEPOSITS;
	}
	public static final String getCOMMENT_COMMISSIONS() {
		return COMMENT_COMMISSIONS;
	}
	public static final String getCOMMENT_FOREX() {
		return COMMENT_FOREX;
	}
	public static final String getCOMMENT_FOREX_SWAPS() {
		return COMMENT_FOREX_SWAPS;
	}
	public static final String getCOMMENT_METAL_SWAPS() {
		return COMMENT_METAL_SWAPS;
	}
	public static final String getCASH() {
		return CASH;
	}
	public static final double getERROR_ACCEPTED() {
		return ERROR_ACCEPTED;
	}
	public static final String getOIL() {
		return OIL;
	}
	public static final String getCOMMENT_OIL() {
		return COMMENT_OIL;
	}
	public static final int getOIL_MULTIPLIER() {
		return OIL_MULTIPLIER;
	}
	public static String getNAME_FILE_DEBUG_IB_REPORTS() {
		return NAME_FILE_DEBUG_IB_REPORTS;
	}
	public static String getNAME_FILE_DEBUG_BKTRANSACTION() {
		return NAME_FILE_DEBUG_BKTRANSACTION;
	}

}
