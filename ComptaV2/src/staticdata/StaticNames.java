package staticdata;

public class StaticNames {

	/*
	 * CONF
	 */
	private static String CONF_COST_LOAN = "Cost_of_bar_loan.csv";
	private static String CONF_COST_LOAN_YEALRY = "Cost_of_bar_loan_percentage_yearly.csv";
	private static String CONF_COST_LOAN_CURRENCY = "Cost_of_currency_loan.csv";
	private static String CONF_BKACCOUNT = "Clients_accounts_details.csv";
	private static String CONF_BKBARTYPE = "Bars_type.csv";
	private static String CONF_STORAGE = "Storage_cost.csv";
	private static String CONF_FY_INCOME = "Fiscal_year_end_income_statement_categories.csv";
	private static String CONF_OPERATIONAL_MARGIN = "Operational_margin.csv";
	/*
	 * 
	 */
	private static String UOB_ACCOUNT_STATEMENT = "AccountStatement";
	private static String FOREX = "Forex.csv";
	private static String UOB_ALL_TRANSACTIONS = "UOB_all_transactions.csv";
	private static String RHB_ALL_TRANSACTIONS = "RHB_all_transactions.csv";
	private static String IFB_ALL_TRANSACTIONS = "IFB_all_transactions.csv";
	private static String ARGENTOR_ALL_TRANSACTIONS = "Argentor_all_transactions.csv";
	private static String UOB_DESIGNATIONS = "UOB_designations_into_categories.csv";
	private static String PRICES_HISTO = "Prices_histo_forex_metals_in_USD.csv";
	private static String ACCOUNT_BUNKER = "contact@bunker-group.com";
	private static String ACCOUNT_PROY = "pierre.roy@hotmail.com";
	private static String CURRENCY_REFERENCE = "USD";
	private static String FILE_LOAN = "_Loan.csv";
	private static String FILE_LOAN_OFFSET = "_Loan_offset.csv";
	private static String FILE_STORAGE = "Storage.csv";
	private static String TREATED_INTERACTIVEBROKERS = "_InteractiveBrokers.csv";
	private static String BROKERS_INTERACTIVEBROKERS = "_IB_report.csv";
	private static String BROKERS_OANDA = "_OANDA.csv";
	private static String BROKERS_OANDA_CRYPTO = "_OANDA_Crypto.csv";
	private static String BROKERS_OANDA_POSITIONS = "_OANDA_Positions.csv";
	private static String TREATED_FY_ADJUSTMENTS_PREFIX = "FY_Adjustments_";
	private static String PREFIX_DELIVERIES = "Delivery_";
	private static String TREATED_COSTS_LOANS_YEARLY = "_Cost_loan_yearly.csv";
	private static String TREATED_COSTS_LOANS_CURRENCY = "_Cost_loan_currencies.csv";
	/*
	 * Output
	 */
	private static String OUTPUT_RECONCILIATION_UOB = "_Reconciliation_UOB.csv";
	private static String OUTPUT_BROKERS_ASSETS = "_Brokers_assets.csv";
	private static String OUTPUT_PVL = "_output_pvl.csv";
	private static String OUTPUT_SUB_ACCOUNT = "_output_subaccount.csv";
	private static String OUTPUT_BKTRANSACTIONS = "_bktransactions.csv";
	private static String OUTPUT_FY_INCOME_STATEMENT = "_FiscalYear_Income_Statement_Since_Creation_Of_Bunker.csv";
	private static String OUTPUT_FY_INCOME_STATEMENT_DELTA = "_FiscalYear_Income_Statement.csv";
	private static String OUTPUT_FY_BALANCE_SHEET = "_FiscalYear_Balance_Sheet.csv";
	private static String OUTPUT_FY_DEBUG_CASH_CLIENTS = "_Debug_cash_clients.csv";
	private static String OUTPUT_FY_DEBUG_BS = "_Balance_sheet_all_BSAssets.csv";
	private static String OUTPUT_FY_DEBUG_BS_MIRROR = "_Balance_sheet_mirror_transactions.csv";
	private static String OUTPUT_FY_DEBUG_BS_SELECTED = "_Balance_sheet_BSAsset_selected.csv";
	private static String OUTPUT_FY_DEBUG_IN_COMPARISON = "_IN_Comparison_deltaFY_";
	private static String OUTPUT_CRYPTOS_ASSETS = "_Cryptos_assets.csv";
	private static String OUTPUT_BROKERS_PVL = "_Brokers_PvL.csv";
	private static String OUTPUT_BROKERS_BKINCOMES = "_Brokers_BKIncomes.csv";
	private static String OUTPUT_BROKERS_AMOUNTS = "_Brokers_amounts.csv";
	private static String OUTPUT_CRYPTOS_AMOUNTS = "_Cryptos_amounts.csv";
	private static String OUTPUT_CRYPTOS_VALUATION = "_Cryptos_valuation.csv";
	private static String OUTPUT_INCOMING_FUNDS = "_Incoming_funds_per_month.csv";
	private static String OUTPUT_VALUATION_CRYPTOS = "_Valuation_accounts_cryptos.csv";
	private static String OUTPUT_VALUATION_BROKERS = "_Valuation_accounts_brokers.csv";
	private static String OUTPUT_BROKERS_EXPOSURES = "_Brokers_exposures.csv";
	private static String OUTPUT_LEDGERS_UOB = "_Ledgers_UOB.csv";
	private static String OUTPUT_BKHOLDINGS_REAL_TIME = "_BKHoldingClient_RealTime_";
	private static String OUTPUT_BKTRANSACTIONS_REAL_TIME = "_BKTransactionsClient_RealTime.csv";
	private static String OUTPUT_EXPOSURE_REAL_TIME_STAND_ALONE = "_Exposure_to_metals.csv";
	private static String OUTPUT_EXPOSURE_SUMMARY_REAL_TIME_STAND_ALONE = "_Exposure_to_metals_summary.csv";
	private static String OUTPUT_EXPOSURE_REAL_TIME_STAND_ALONE_BKTRANSACTIONS = "_BKTransactions_kept.csv";
	private static String OUTPUT_FY_ADJUSTMENTS = "FY_Adjustments.csv";
	private static String OUTPUT_BARS_MALCA_AMIT = "_Reconciliation_bars_Malca_versus_Compta.csv";
	private static String OUTPUT_BKLOANS = "_BKLoans.csv";
	private static String OUTPUT_BKLOANS_CURRENCIES = "_BKLoansCurrencies.csv";
	private static String OUTPUT_HOLDING_CLIENTS_END_OF_MONTH = "_Holdings_clients_end_of_month.csv";
	private static String OUTPUT_STORAGE_MONTHLY = "_Storage_monthly.csv";
	/*
	 * 
	 */
	private static String SEPARATOR = ";;";
	
	/*
	 * Getters & Setters
	 */
	public static final String getUOB_ACCOUNT_STATEMENT() {
		return UOB_ACCOUNT_STATEMENT;
	}
	public static final String getFOREX() {
		return FOREX;
	}
	public static final String getUOB_ALL_TRANSACTIONS() {
		return UOB_ALL_TRANSACTIONS;
	}
	public static final String getUOB_DESIGNATIONS() {
		return UOB_DESIGNATIONS;
	}
	public static final String getPRICES_HISTO() {
		return PRICES_HISTO;
	}
	public static final String getOUTPUT_PVL() {
		return OUTPUT_PVL;
	}
	public static final String getCONF_COST_LOAN() {
		return CONF_COST_LOAN;
	}
	public static final String getOUTPUT_SUB_ACCOUNT() {
		return OUTPUT_SUB_ACCOUNT;
	}
	public static final String getOUTPUT_BKTRANSACTIONS() {
		return OUTPUT_BKTRANSACTIONS;
	}
	public static final String getACCOUNT_BUNKER() {
		return ACCOUNT_BUNKER;
	}
	public static final String getACCOUNT_PROY() {
		return ACCOUNT_PROY;
	}
	public static final String getCURRENCY_REFERENCE() {
		return CURRENCY_REFERENCE;
	}
	public static final String getFILE_LOAN() {
		return FILE_LOAN;
	}
	public static final String getFILE_LOAN_OFFSET() {
		return FILE_LOAN_OFFSET;
	}
	public static final String getOUTPUT_RECONCILIATION_UOB() {
		return OUTPUT_RECONCILIATION_UOB;
	}
	public static final String getOUTPUT_BROKERS_ASSETS() {
		return OUTPUT_BROKERS_ASSETS;
	}
	public static final String getCONF_BKACCOUNT() {
		return CONF_BKACCOUNT;
	}
	public static final String getCONF_BKBARTYPE() {
		return CONF_BKBARTYPE;
	}
	public static final String getCONF_STORAGE() {
		return CONF_STORAGE;
	}
	public static final String getFILE_STORAGE() {
		return FILE_STORAGE;
	}
	public static final String getOUTPUT_FY_INCOME_STATEMENT() {
		return OUTPUT_FY_INCOME_STATEMENT;
	}
	public static final String getOUTPUT_FY_BALANCE_SHEET() {
		return OUTPUT_FY_BALANCE_SHEET;
	}
	public static final String getCONF_FY_INCOME() {
		return CONF_FY_INCOME;
	}
	public static final String getOUTPUT_FY_DEBUG_CASH_CLIENTS() {
		return OUTPUT_FY_DEBUG_CASH_CLIENTS;
	}
	public static final String getOUTPUT_FY_INCOME_STATEMENT_DELTA() {
		return OUTPUT_FY_INCOME_STATEMENT_DELTA;
	}
	public static final String getOUTPUT_CRYPTOS_ASSETS() {
		return OUTPUT_CRYPTOS_ASSETS;
	}
	public static final String getOUTPUT_BROKERS_PVL() {
		return OUTPUT_BROKERS_PVL;
	}
	public static final String getOUTPUT_CRYPTOS_VALUATION() {
		return OUTPUT_CRYPTOS_VALUATION;
	}
	public static final String getOUTPUT_INCOMING_FUNDS() {
		return OUTPUT_INCOMING_FUNDS;
	}
	public static final String getCONF_OPERATIONAL_MARGIN() {
		return CONF_OPERATIONAL_MARGIN;
	}
	public static final String getOUTPUT_VALUATION_CRYPTOS() {
		return OUTPUT_VALUATION_CRYPTOS;
	}
	public static final String getOUTPUT_VALUATION_BROKERS() {
		return OUTPUT_VALUATION_BROKERS;
	}
	public static final String getTREATED_INTERACTIVEBROKERS() {
		return TREATED_INTERACTIVEBROKERS;
	}
	public static final String getBROKERS_INTERACTVIVEBROKERS() {
		return BROKERS_INTERACTIVEBROKERS;
	}
	public static final String getSEPARATOR() {
		return SEPARATOR;
	}
	public static final String getBROKERS_OANDA() {
		return BROKERS_OANDA;
	}
	public static final String getBROKERS_OANDA_CRYPTO() {
		return BROKERS_OANDA_CRYPTO;
	}
	public static final String getOUTPUT_BROKERS_BKINCOMES() {
		return OUTPUT_BROKERS_BKINCOMES;
	}
	public static final String getOUTPUT_BROKERS_AMOUNTS() {
		return OUTPUT_BROKERS_AMOUNTS;
	}
	public static final String getOUTPUT_CRYPTOS_AMOUNTS() {
		return OUTPUT_CRYPTOS_AMOUNTS;
	}
	public static final String getOUTPUT_BROKERS_EXPOSURES() {
		return OUTPUT_BROKERS_EXPOSURES;
	}
	public static final String getOUTPUT_LEDGERS_UOB() {
		return OUTPUT_LEDGERS_UOB;
	}
	public static final String getBROKERS_OANDA_POSITIONS() {
		return BROKERS_OANDA_POSITIONS;
	}
	public static final String getOUTPUT_FY_DEBUG_BS() {
		return OUTPUT_FY_DEBUG_BS;
	}
	public static final String getOUTPUT_FY_DEBUG_BS_MIRROR() {
		return OUTPUT_FY_DEBUG_BS_MIRROR;
	}
	public static final String getOUTPUT_FY_DEBUG_BS_SELECTED() {
		return OUTPUT_FY_DEBUG_BS_SELECTED;
	}
	public static final String getOUTPUT_FY_DEBUG_IN_COMPARISON() {
		return OUTPUT_FY_DEBUG_IN_COMPARISON;
	}
	public static String getRHB_ALL_TRANSACTIONS() {
		return RHB_ALL_TRANSACTIONS;
	}
	public static String getIFB_ALL_TRANSACTIONS() {
		return IFB_ALL_TRANSACTIONS;
	}
	public static String getTREATED_FY_ADJUSTMENTS_PREFIX() {
		return TREATED_FY_ADJUSTMENTS_PREFIX;
	}
	public static final String getOUTPUT_BKHOLDINGS_REAL_TIME() {
		return OUTPUT_BKHOLDINGS_REAL_TIME;
	}
	public static final String getOUTPUT_EXPOSURE_REAL_TIME_STAND_ALONE() {
		return OUTPUT_EXPOSURE_REAL_TIME_STAND_ALONE;
	}
	public static final String getOUTPUT_EXPOSURE_SUMMARY_REAL_TIME_STAND_ALONE() {
		return OUTPUT_EXPOSURE_SUMMARY_REAL_TIME_STAND_ALONE;
	}
	public static final String getOUTPUT_EXPOSURE_REAL_TIME_STAND_ALONE_BKTRANSACTIONS() {
		return OUTPUT_EXPOSURE_REAL_TIME_STAND_ALONE_BKTRANSACTIONS;
	}
	public static final String getOUTPUT_FY_ADJUSTMENTS() {
		return OUTPUT_FY_ADJUSTMENTS;
	}
	public static final String getPREFIX_DELIVERIES() {
		return PREFIX_DELIVERIES;
	}
	public static final String getOUTPUT_BARS_MALCA_AMIT() {
		return OUTPUT_BARS_MALCA_AMIT;
	}
	public static final String getOUTPUT_BKTRANSACTIONS_REAL_TIME() {
		return OUTPUT_BKTRANSACTIONS_REAL_TIME;
	}
	public static final String getOUTPUT_BKLOANS() {
		return OUTPUT_BKLOANS;
	}
	public static final String getCONF_COST_LOAN_YEALRY() {
		return CONF_COST_LOAN_YEALRY;
	}
	public static final String getTREATED_COSTS_LOANS_YEARLY() {
		return TREATED_COSTS_LOANS_YEARLY;
	}
	public static final String getOUTPUT_BKLOANS_CURRENCIES() {
		return OUTPUT_BKLOANS_CURRENCIES;
	}
	public static final String getCONF_COST_LOAN_CURRENCY() {
		return CONF_COST_LOAN_CURRENCY;
	}
	public static final String getTREATED_COSTS_LOANS_CURRENCY() {
		return TREATED_COSTS_LOANS_CURRENCY;
	}
	public static final String getOUTPUT_HOLDING_CLIENTS_END_OF_MONTH() {
		return OUTPUT_HOLDING_CLIENTS_END_OF_MONTH;
	}
	public static final String getOUTPUT_STORAGE_MONTHLY() {
		return OUTPUT_STORAGE_MONTHLY;
	}
	public static final String getARGENTOR_ALL_TRANSACTIONS() {
		return ARGENTOR_ALL_TRANSACTIONS;
	}

















}
