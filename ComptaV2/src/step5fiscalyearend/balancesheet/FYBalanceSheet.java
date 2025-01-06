package step5fiscalyearend.balancesheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import staticdata.StaticNames;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step5fiscalyearend.FYMain;

public class FYBalanceSheet extends BKHolderGenerator {

	public FYBalanceSheet(FYMain _sFYMain) {
		super(_sFYMain.getpLNManager().getpBKTransactionManager());
		pFYMain = _sFYMain;
	}

	/**
	 * Keep only the BKTransactions of Bunker
	 */
	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return true;
	}

	/**
	 * One column per BKIncome
	 */
	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAccount().getpEmailAddress() 
				+ ";;;" + _sBKTransaction.getpFileNameOrigin()
				+ ";;;" + _sBKTransaction.getpBKIncome();
	}

	/*
	 * Data
	 */
	private FYMain pFYMain;
	private int pDateFY;
	private int pDateNameFile;
	private FYTreatUOBCash pFYTreatUOBCash;
	private FYTreatBunker pFYTreatBunker;
	private FYTreatIncomes pFYTreatIncomes;
	private Map<String, Double> pMapCurrencyToCashClients;
	private Map<String, FYAsset> pMapNameToFYAsset;
	private List<FYAsset> pListFYAsset;

	/**
	 * Compute, extract the data and write the file for the balance sheet for the fiscal year
	 * @param _sDateFY
	 */
	public final void writeFile(int _sDateFY) {
		/*
		 * Set the Dates
		 */
		pDateNameFile = _sDateFY;
		pDateFY = _sDateFY;
		if (pDateFY > BasicDateInt.getmToday()) {
			pDateFY = BasicDateInt.getmToday();
		}
		/*
		 * Extract Data
		 */
		extractData();
		/*
		 * Write File
		 */
		writeFile();
	}

	/**
	 * Extract the data for Bunker and the clients
	 */
	private void extractData() {
		/*
		 * Initiate
		 */
		pFYTreatUOBCash = new FYTreatUOBCash(this);
		pFYTreatBunker = new FYTreatBunker(this);
		pFYTreatIncomes = new FYTreatIncomes(this);
		Map<String, FYTreatClient> lMapBKAccountToFYTreatClient = new HashMap<String, FYTreatClient>();
		pListFYAsset = new ArrayList<>();
		pMapNameToFYAsset = new HashMap<>();
		/*
		 * Extract the cash on the UOB accounts
		 */
		pFYTreatUOBCash.run();
		/*
		 * Extract the data for each BKAccount
		 */
		for (BKHolder lBKHolder : getpListBKHolder()) {
			BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(pDateFY);
			if (lBKInventory != null) {
				String[] lKeys = lBKHolder.getpKey().split(";;;");
				int lIdx = -1;
				String lBKAccountStr = lKeys[++lIdx];
				String lFileNameOrigin = lKeys[++lIdx];
				String lBKIncomeStr = lKeys[++lIdx];
				/*
				 * Case of Bunker --> we look for the Capital+NetIncome+HedgingAccount+Loan
				 */
				if (lBKAccountStr.equals(BKAccountManager.getpBKAccountBunker().getpEmailAddress())) {
					pFYTreatBunker.run(lBKInventory, lFileNameOrigin, lBKIncomeStr);
				} 
				/*
				 * Case of a client account --> we look for the cash
				 */
				else {
					FYTreatClient lFYTreatClient = lMapBKAccountToFYTreatClient.get(lBKAccountStr);
					if (lFYTreatClient == null) {
						lFYTreatClient = new FYTreatClient(this, lBKAccountStr);
						lMapBKAccountToFYTreatClient.put(lBKAccountStr, lFYTreatClient);
					}
					lFYTreatClient.run(lBKAccountStr, lBKInventory);
				}
			}
		}
		/*
		 * Aggregate the cash of the clients per currency. Store in pMapCurrencyToCashClients 
		 */
		pMapCurrencyToCashClients = new HashMap<String, Double>();
		for (FYTreatClient lFYTreatClient : lMapBKAccountToFYTreatClient.values()) {
			for (BKAsset lBKCurrency : lFYTreatClient.getpMapBKCurrencyToAmount().keySet()) {
				String lCurrency = lBKCurrency.getpName();
				Double lAmount = pMapCurrencyToCashClients.get(lCurrency);
				if (lAmount == null) {
					lAmount = 0.;
				}
				lAmount += (lFYTreatClient.getpMapBKCurrencyToAmount().get(lBKCurrency)
						/ BKAssetManager.getpForexReference(pDateFY, lCurrency));
				pMapCurrencyToCashClients.put(lCurrency, lAmount);
			}
		}
		/*
		 * Extract the Income of the current fiscal year and the previous fiscal years from the FYIncomeStatement
		 */
		pFYTreatIncomes.run();
	}

	/**
	 * Write the file of the balance sheet
	 */
	private void writeFile() {
		List<String> lListLineToWrite = new ArrayList<String>();
		/*
		 * Title
		 */
		String lDateFYStr = BasicDateInt.getmDay(pDateNameFile) 
				+ "/" + BasicDateInt.getmMonth(pDateNameFile)
				+ "/" + BasicDateInt.getmYear(pDateNameFile);
		lListLineToWrite.add("BALANCE SHEET FOR FISCAL YEAR ENDING " + lDateFYStr);
		lListLineToWrite.add("");
		lListLineToWrite.add("ASSETS");
		double lTotalAssets = 0.;
		/*
		 * Cash at UOB
		 */
		List<String> lListCurrency = new ArrayList<String>(pFYTreatUOBCash.getpMapCurrencyToCashAtUOB().keySet());
		Collections.sort(lListCurrency);
		for (String lCurrency : lListCurrency) {
			double lCash = pFYTreatUOBCash.getpMapCurrencyToCashAtUOB().get(lCurrency);
			String lLine = "," + "UOB account " + lCurrency + "," + lCash;
			lListLineToWrite.add(lLine);
			lTotalAssets += lCash;
		}
		/*
		 * Hedging, CRYPTOS, Bars detained by Bunker
		 */
		Collections.sort(pListFYAsset);
		for (FYAsset lFYAsset : pListFYAsset) {
			if (!Double.isNaN(lFYAsset.getpValueUSD())) {
				lListLineToWrite.add("," + lFYAsset.getpName() + "," + lFYAsset.getpValueUSD());
				lTotalAssets += lFYAsset.getpValueUSD();
			}
		}
		/*
		 * Total Assets
		 */
		lListLineToWrite.add("Total ASSETS,,," + lTotalAssets);
		/*
		 * Liabilities
		 */
		lListLineToWrite.add("LIABILITIES");
		double lTotalLiabilities = 0.;
		/*
		 * Cash from clients
		 */
		lListCurrency = new ArrayList<String>(pMapCurrencyToCashClients.keySet());
		Collections.sort(lListCurrency);
		for (String lCurrency : lListCurrency) {
			double lCash = pMapCurrencyToCashClients.get(lCurrency);
			String lLine = "," + "Clients holdings " + lCurrency + "," + lCash;
			lListLineToWrite.add(lLine);
			lTotalLiabilities += lCash;
		}
		/*
		 * Loans
		 */
		List<String> lListLoans = new ArrayList<String>(pFYTreatBunker.getpMapLoanToValueUSD().keySet());
		Collections.sort(lListLoans);
		for (String lLoan : lListLoans) {
			Double lAmount = pFYTreatBunker.getpMapLoanToValueUSD().get(lLoan);
			String lLine = "," + lLoan + "," + lAmount;
			lListLineToWrite.add(lLine);
			lTotalLiabilities += lAmount;
		}
		/*
		 * Write lTotalLiabilities
		 */
		lListLineToWrite.add("Total LIABILITIES,,," + lTotalLiabilities);
		/*
		 * Equity
		 */
		lListLineToWrite.add("SHAREHOLDERS' EQUITY");
		double lTotalEquity = 0.;
		lListLineToWrite.add("," + "Capital USD" + "," + pFYTreatBunker.getpCapitalUSD());
		lTotalEquity += pFYTreatBunker.getpCapitalUSD();
		for (int lDateFY : pFYTreatIncomes.getpListDateFY()) {
			String lDateStr = BasicDateInt.getmDay(lDateFY) 
					+ "/" + BasicDateInt.getmMonth(lDateFY)
					+ "/" + BasicDateInt.getmYear(lDateFY);
			lListLineToWrite.add("," + "Retained Earnings FY ending " + lDateStr
					+ "," + pFYTreatIncomes.getpMapDateFYToFYIncome().get(lDateFY));
			lTotalEquity += pFYTreatIncomes.getpMapDateFYToFYIncome().get(lDateFY);
		}
		lListLineToWrite.add("Total EQUITY,,," + lTotalEquity);
		/*
		 * Grand Total
		 */
		double lGrandTotal = Math.round((lTotalAssets - lTotalLiabilities - lTotalEquity)*1000.) / 1000.;
		lListLineToWrite.add("Grand total (ASSETS-LIABILITIES-EQUITY) must = 0,,," 
				+ lGrandTotal);
		/*
		 * Write File
		 */
		String lNameFile = pDateNameFile + StaticNames.getOUTPUT_FY_BALANCE_SHEET();
		FYMain.writeFile_unless_already_exists(this, lNameFile, null, lListLineToWrite);
	}

	/**
	 * Classic get or create
	 * @param _sName
	 * @return
	 */
	protected final FYAsset getpOrCreateFYAsset(String _sName) {
		FYAsset lFYAsset = pMapNameToFYAsset.get(_sName);
		if (lFYAsset == null) {
			lFYAsset = new FYAsset(_sName);
			pMapNameToFYAsset.put(_sName, lFYAsset);
			pListFYAsset.add(lFYAsset);
		}
		return lFYAsset;
	}

	/*
	 * Getters & Setters
	 */
	public final FYMain getpFYMain() {
		return pFYMain;
	}
	public final int getpDateFY() {
		return pDateFY;
	}
	public final int getpDateNameFile() {
		return pDateNameFile;
	}
	public final FYTreatBunker getpFYTreatBunker() {
		return pFYTreatBunker;
	}


}
