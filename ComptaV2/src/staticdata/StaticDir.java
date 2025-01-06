package staticdata;

public class StaticDir {

	/*
	 * Data
	 */
	private static String DRIVE = StaticRoboCopy.getAndComputeMasterDrive(true);
	private static String CONF_COMPTA = DRIVE + "BUNKER_V2/11_COMPTA/00_Conf_files/";
	private static String PRICES_HISTO = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/05_Prices_histo/";
	/*
	 * Import CSV
	 */
	private static String IMPORT_UOB_STATEMENTS = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/01_UOB_bank_statements/"; // "BUNKER_V2/03_BANK_BROKERHEDGE/01_UOB/05_Bank_statements/01_Raw_imports/";
	private static String IMPORT_CSV = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/";
	private static String IMPORT_CLIENTS_PURCHASE = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/02_Clients_purchases_and_sell/";
	private static String IMPORT_REFINERS_PURCHASE = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/03_Refiner_purchases/";
	private static String IMPORT_BROKERS = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/06_Brokers/";
	private static String IMPORT_FOREX = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/08_Forex/";
	private static String IMPORT_CRYPTOS = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/10_Cryptos/";
	private static String IMPORT_BROKERS_IB = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/06_Brokers/InteractiveBrokers/";
	private static String IMPORT_BROKERS_HF = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/06_Brokers/HotForex/";
	private static String IMPORT_BROKERS_IFC = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/06_Brokers/IFCM/";
	private static String IMPORT_BROKERS_OANDA = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/06_Brokers/OANDA/";
	private static String IMPORT_BROKERS_OANDA_CRYPTO = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/06_Brokers/OANDA_Crypto/";
	private static String IMPORT_DIRECTIONAL = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/11_Directional_Bunker/";
	private static String IMPORT_DELIVERY = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/12_Bars_delivery/";
	private static String IMPORT_BROKERS_OANDA_POSITIONS = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/06_Brokers/OANDA_Positions/";
	private static String IMPORT_FY_ADJUSTMENTS = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/13_FY_Adjustments/";
	private static String IMPORT_BARS_MALCA_AMIT = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/14_Bars_in_MalcaAmit/";
	private static String IMPORT_HOLDING_CLIENTS_END_OF_MONTH = DRIVE + "BUNKER_V2/11_COMPTA/01_Import_csv/15_Holding_clients_end_of_month_in_website/";
	private static String FY_VALIDATED = DRIVE + "BUNKER_V2/11_COMPTA/06_Fiscal-year_end/";
	/*
	 * Import CSV treated
	 */
	private static String UOB_ALL_TRANSACTIONS = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/01_UOB_transactions_treated/";
	private static String RHB_ALL_TRANSACTIONS = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/01_RHB_transactions_treated/";
	private static String IFB_ALL_TRANSACTIONS = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/01_IFB_transactions_treated/";
	private static String ARGENTOR_ALL_TRANSACTIONS = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/06_Brokers/Argentor_transactions_treated/";
	private static String IMPORT_CSV_TREATED = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/";
	private static String TREATED_CLIENTS_PURCHASE = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/02_Clients_purchases_and_sell/";
	private static String TREATED_REFINERS_PURCHASE = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/03_Refiner_purchases/";
	private static String TREATED_LOANS = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/07_Loans/";
	private static String TREATED_FOREX = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/08_Forex/";
	private static String TREATED_STORAGE = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/09_Storage/";
	private static String TREATED_CRYPTOS = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/10_Cryptos/";
	private static String TREATED_BROKERS_IB = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/06_Brokers/InteractiveBrokers/";
	private static String TREATED_BROKERS_HF = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/06_Brokers/HotForex/";
	private static String TREATED_BROKERS_IFC = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/06_Brokers/IFCM/";
	private static String TREATED_BROKERS_OANDA = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/06_Brokers/OANDA/";
	private static String TREATED_BROKERS_OANDA_CRYPTO = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/06_Brokers/OANDA_Crypto/";
	private static String TREATED_DIRECTIONAL = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/11_Directional_Bunker/";
	private static String TREATED_DELIVERY = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/12_Bars_delivery/";
	private static String TREATED_FY_ADJUSTMENTS = DRIVE + "BUNKER_V2/11_COMPTA/02_csv_treated/13_FY_Adjustments/";
	/*
	 * Output
	 */
	private static String OUTPUT = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/";
	private static String OUTPUT_PVL = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/01_PvL/";
	private static String OUTPUT_SUB_ACCOUNT = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/02_SubAccounts/";
	private static String OUTPUT_DEBUG = DRIVE + "BUNKER_V2/11_COMPTA/04_Debug/";
	private static String OUTPUT_BKASSETS = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/03_BKAssets/";
	private static String OUTPUT_BKACCOUNTS = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/06_BKAccounts/";
	private static String OUTPUT_STORAGE = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/07_Storage/";
	private static String OUTPUT_RECONCILIATION = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/04_Reconciliation/";
	private static String OUTPUT_BROKERS_ASSETS = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/05_Brokers_assets/";
	private static String OUTPUT_FISCAL_YEAR_END = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/08_Fiscal_year_end/";
	private static String OUTPUT_FY_COMPUTATION_CURRENT = OUTPUT_FISCAL_YEAR_END + "computation current/";
	private static String OUTPUT_FY_COMPUTATION_VALIDATED = OUTPUT_FISCAL_YEAR_END + "computation validated and passed/";
	private static String OUTPUT_FY_DEBUG = OUTPUT_FISCAL_YEAR_END + "Debug/";
	private static String OUTPUT_FY_DEBUG_BS = OUTPUT_FY_DEBUG + "Balance Sheet/";
	private static String OUTPUT_FY_DEBUG_BS_MIRROR = OUTPUT_FY_DEBUG + "Balance Sheet mirror transactions/";
	private static String OUTPUT_FY_DEBUG_BS_SELECTED = OUTPUT_FY_DEBUG + "Balance Sheet selected BSAssets/";
	private static String OUTPUT_FY_DEBUG_IN_COMPARISON = OUTPUT_FY_DEBUG + "Income comparison FY and FYm1/";
	private static String OUTPUT_CRYPTOS_ASSETS = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/10_Cryptos_assets/";
	private static String OUTPUT_BROKERS_VALUATION = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/05_Brokers_valuation/";
	private static String OUTPUT_CRYPTOS_VALUATION = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/05_Cryptos_valuation/";
	private static String OUTPUT_INCOMING_FUNDS = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/09_Incoming_funds_per_month/";
	private static String OUTPUT_VALUATIONS = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/11_Valuations/";
	private static String OUTPUT_BKTRANSACTIONS = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/12_BKTransactions/";
	private static String OUTPUT_BKBARS = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/02_BKBars/";
	private static String OUTPUT_STORAGE_COST_MALCA_AMIT = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/07_Storage_MalcaAmit/";
	private static String OUTPUT_EXPOSURE_REAL_TIME = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/13_Exposure_real_time/";
	private static String OUTPUT_EXPOSURE_REAL_TIME_STAND_ALONE = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/13_Exposure_real_time_stand_alone/";
	private static String OUTPUT_BKTRANSACTIONS_COMMERCIAL = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/12_BKTransactions_Commercial/";
	private static String OUTPUT_OANDA_REAL_TIME_POSITIONS = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/14_OANDA_real_time_positions/";
	private static String OUTPUT_BKHOLDINGS_REAL_TIME = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/15_BKHoldings_Real_time/";
	private static String OUTPUT_BKTRANSACTIONS_REAL_TIME = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/15_BKTransactions_Real_time/";
	private static String OUTPUT_FY_ADJUSTMENTS = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/08_Fiscal_year_end/fy adjustments/";
	private static String OUTPUT_BARS_MALCA_AMIT = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/16_Bars_in_MalcaAmit_reconciliation/";
	private static String OUTPUT_BKLOANS = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/17_BKLoans/";
	private static String OUTPUT_BKLOANS_CURRENCIES = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/17_BKLoans_currencies/";
	private static String OUTPUT_HOLDING_CLIENTS_END_OF_MONTH = DRIVE + "BUNKER_V2/11_COMPTA/03_File_output/18_Holdings_clients_end_of_months/";
	/*
	 * Getters & Setters
	 */
	public static final String getIMPORT_UOB_STATEMENTS() {
		return IMPORT_UOB_STATEMENTS;
	}
	public static final String getCONF_COMPTA() {
		return CONF_COMPTA;
	}
	public static final String getUOB_ALL_TRANSACTIONS() {
		return UOB_ALL_TRANSACTIONS;
	}
	public static final String getIMPORT_CLIENTS_PURCHASE() {
		return IMPORT_CLIENTS_PURCHASE;
	}
	public static final String getIMPORT_REFINERS_PURCHASE() {
		return IMPORT_REFINERS_PURCHASE;
	}
	public static final String getPRICES_HISTO() {
		return PRICES_HISTO;
	}
	public static final String getIMPORT_BROKERS() {
		return IMPORT_BROKERS;
	}
	public static final String getOUTPUT_PVL() {
		return OUTPUT_PVL;
	}
	public static final String getOUTPUT_SUB_ACCOUNT() {
		return OUTPUT_SUB_ACCOUNT;
	}
	public static final String getOUTPUT_BKTRANSACTIONS() {
		return OUTPUT_BKTRANSACTIONS;
	}
	public static final String getTREATED_CLIENTS_PURCHASE() {
		return TREATED_CLIENTS_PURCHASE;
	}
	public static final String getTREATED_REFINERS_PURCHASE() {
		return TREATED_REFINERS_PURCHASE;
	}
	public static final String getTREATED_LOANS() {
		return TREATED_LOANS;
	}
	public static final String getOUTPUT_DEBUG() {
		return OUTPUT_DEBUG;
	}
	public static final String getOUTPUT_BKASSETS() {
		return OUTPUT_BKASSETS;
	}
	public static final String getOUTPUT_RECONCILIATION() {
		return OUTPUT_RECONCILIATION;
	}
	public static final String getOUTPUT_BROKERS_ASSETS() {
		return OUTPUT_BROKERS_ASSETS;
	}
	public static final String getOUTPUT_BKACCOUNTS() {
		return OUTPUT_BKACCOUNTS;
	}
	public static final String getIMPORT_FOREX() {
		return IMPORT_FOREX;
	}
	public static final String getTREATED_FOREX() {
		return TREATED_FOREX;
	}
	public static final String getTREATED_STORAGE() {
		return TREATED_STORAGE;
	}
	public static final String getOUTPUT_STORAGE() {
		return OUTPUT_STORAGE;
	}
	public static final String getOUTPUT_FISCAL_YEAR_END() {
		return OUTPUT_FISCAL_YEAR_END;
	}
	public static final String getOUTPUT_FY_DEBUG() {
		return OUTPUT_FY_DEBUG;
	}
	public static final String getIMPORT_CRYPTOS() {
		return IMPORT_CRYPTOS;
	}
	public static final String getTREATED_CRYPTOS() {
		return TREATED_CRYPTOS;
	}
	public static final String getOUTPUT_CRYPTOS_ASSETS() {
		return OUTPUT_CRYPTOS_ASSETS;
	}
	public static final String getOUTPUT_BROKERS_VALUATION() {
		return OUTPUT_BROKERS_VALUATION;
	}
	public static final String getOUTPUT_CRYPTOS_VALUATION() {
		return OUTPUT_CRYPTOS_VALUATION;
	}
	public static final String getOUTPUT_INCOMING_FUNDS() {
		return OUTPUT_INCOMING_FUNDS;
	}
	public static final String getOUTPUT_VALUATIONS() {
		return OUTPUT_VALUATIONS;
	}
	public static final String getOUTPUT_BKBARS() {
		return OUTPUT_BKBARS;
	}
	public static final String getIMPORT_BROKERS_IB() {
		return IMPORT_BROKERS_IB;
	}
	public static final String getTREATED_BROKERS_IB() {
		return TREATED_BROKERS_IB;
	}
	public static final String getDRIVE() {
		return DRIVE;
	}
	public static final String getTREATED_BROKERS_HF() {
		return TREATED_BROKERS_HF;
	}
	public static final String getTREATED_BROKERS_IFC() {
		return TREATED_BROKERS_IFC;
	}
	public static final String getTREATED_BROKERS_OANDA() {
		return TREATED_BROKERS_OANDA;
	}
	public static final String getIMPORT_BROKERS_HF() {
		return IMPORT_BROKERS_HF;
	}
	public static final String getIMPORT_BROKERS_IFC() {
		return IMPORT_BROKERS_IFC;
	}
	public static final String getIMPORT_BROKERS_OANDA() {
		return IMPORT_BROKERS_OANDA;
	}
	public static final String getIMPORT_BROKERS_OANDA_CRYPTO() {
		return IMPORT_BROKERS_OANDA_CRYPTO;
	}
	public static final String getTREATED_BROKERS_OANDA_CRYPTO() {
		return TREATED_BROKERS_OANDA_CRYPTO;
	}
	public static final String getTREATED_DIRECTIONAL() {
		return TREATED_DIRECTIONAL;
	}
	public static final String getIMPORT_DIRECTIONAL() {
		return IMPORT_DIRECTIONAL;
	}
	public static final String getIMPORT_CSV() {
		return IMPORT_CSV;
	}
	public static final String getIMPORT_CSV_TREATED() {
		return IMPORT_CSV_TREATED;
	}
	public static final String getOUTPUT() {
		return OUTPUT;
	}
	public static final String getIMPORT_DELIVERY() {
		return IMPORT_DELIVERY;
	}
	public static final String getTREATED_DELIVERY() {
		return TREATED_DELIVERY;
	}
	public static final String getOUTPUT_STORAGE_COST_MALCA_AMIT() {
		return OUTPUT_STORAGE_COST_MALCA_AMIT;
	}
	public static final String getOUTPUT_EXPOSURE_REAL_TIME() {
		return OUTPUT_EXPOSURE_REAL_TIME;
	}
	public static final String getOUTPUT_BKTRANSACTIONS_COMMERCIAL() {
		return OUTPUT_BKTRANSACTIONS_COMMERCIAL;
	}
	public static final String getOUTPUT_OANDA_REAL_TIME_POSITIONS() {
		return OUTPUT_OANDA_REAL_TIME_POSITIONS;
	}
	public static final String getIMPORT_BROKERS_OANDA_POSITIONS() {
		return IMPORT_BROKERS_OANDA_POSITIONS;
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
	public static final String getOUTPUT_FY_COMPUTATION_CURRENT() {
		return OUTPUT_FY_COMPUTATION_CURRENT;
	}
	public static final String getOUTPUT_FY_COMPUTATION_VALIDATED() {
		return OUTPUT_FY_COMPUTATION_VALIDATED;
	}
	public static String getRHB_ALL_TRANSACTIONS() {
		return RHB_ALL_TRANSACTIONS;
	}
	public static String getIFB_ALL_TRANSACTIONS() {
		return IFB_ALL_TRANSACTIONS;
	}
	public static final String getOUTPUT_BKHOLDINGS_REAL_TIME() {
		return OUTPUT_BKHOLDINGS_REAL_TIME;
	}
	public static final String getOUTPUT_EXPOSURE_REAL_TIME_STAND_ALONE() {
		return OUTPUT_EXPOSURE_REAL_TIME_STAND_ALONE;
	}
	public static final String getIMPORT_FY_ADJUSTMENTS() {
		return IMPORT_FY_ADJUSTMENTS;
	}
	public static final String getTREATED_FY_ADJUSTMENTS() {
		return TREATED_FY_ADJUSTMENTS;
	}
	public static final String getOUTPUT_FY_ADJUSTMENTS() {
		return OUTPUT_FY_ADJUSTMENTS;
	}
	public static final String getIMPORT_BARS_MALCA_AMIT() {
		return IMPORT_BARS_MALCA_AMIT;
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
	public static final String getOUTPUT_BKLOANS_CURRENCIES() {
		return OUTPUT_BKLOANS_CURRENCIES;
	}
	public static final String getOUTPUT_HOLDING_CLIENTS_END_OF_MONTH() {
		return OUTPUT_HOLDING_CLIENTS_END_OF_MONTH;
	}
	public static final String getIMPORT_HOLDING_CLIENTS_END_OF_MONTH() {
		return IMPORT_HOLDING_CLIENTS_END_OF_MONTH;
	}
	public static final String getFY_VALIDATED() {
		return FY_VALIDATED;
	}
	public static final String getARGENTOR_ALL_TRANSACTIONS() {
		return ARGENTOR_ALL_TRANSACTIONS;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}


