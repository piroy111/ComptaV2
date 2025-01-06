package step3statements.statements.statements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.StaticBKIncome;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step3statements.statements.abstractstatements.STBKAccounts;
import step3statements.statements.main.STStatementAbstract;

public class STWriteFileBKLoans extends STStatementAbstract {

	public STWriteFileBKLoans(STBKAccounts _sSTBKAccounts) {
		super (_sSTBKAccounts);
		pSTBKAccounts = _sSTBKAccounts;
	}

	/*
	 * 
	 */
	private STBKAccounts pSTBKAccounts;
	
	@Override public String getpDirName() {
		return StaticDir.getOUTPUT_BKLOANS();
	}

	@Override public void createLines() {
		/*
		 * We keep the transactions only for the previous month
		 */
		int lDateStop = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(BasicDateInt.getmToday(), -1));
		int lDateStart = BasicDateInt.getmPlusDay(BasicDateInt.getmPlusMonth(lDateStop, -1), 1);
		/*
		 * Build the list of loan
		 */
		List<String> lListBKAssetStr = new ArrayList<>();
		List<BKAsset> lListBKAssetBarLoan = new ArrayList<>();
		addToHeader("Date");
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAsset()) {
			if (lBKAsset.getpIsBarLoan()) {
				lListBKAssetStr.add(lBKAsset.getpName());
				lListBKAssetBarLoan.add(lBKAsset);
				addToHeader(lBKAsset.getpName() + "," + lBKAsset.getpName().replaceAll("LOAN OZ", "LOAN") + " in USD");
			}
		}
		addToHeader("Cost of loan");
		/*
		 * Compute the quantity of loan every day
		 */
		TreeMap<Integer, String> lTreeMapLineToWrite = new TreeMap<>();
		for (BKHolder lBKHolder : pSTStatementGeneratorAbstract.getpListBKHolder()) {
			/*
			 * Get the holding of the bar loans for the account PROY
			 */
			BKAccount lBKAccountCurrent = pSTBKAccounts.getpAndComputeBKAccountCurrent(lBKHolder);
			if (!lBKAccountCurrent.equals(BKAccountManager.getpBKAccountPierreRoy())) {
				continue;
			}
			for (int lDate = lDateStart; lDate <= lDateStop; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
				BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDate);
				/*
				 * Get the holding of the bar
				 */
				String lLine = lDate + "";
				for (BKAsset lBKAssetBarLoan : lListBKAssetBarLoan) {
					double lHoldingOz;
					if (lBKInventory == null) {
						lHoldingOz = 0.;
					} else {
						lHoldingOz = lBKInventory.getpBKAssetQty(lBKAssetBarLoan);
					}
					double lHoldingUSD = lHoldingOz * lBKAssetBarLoan.getpPriceUSD(lDate);
					lLine += "," + lHoldingOz + "," + lHoldingUSD;
				}
				lTreeMapLineToWrite.put(lDate, lLine);
			}
			/*
			 * Compute the cost of the loan from the account PROY
			 */
			Map<Integer, Double> lMapDateToLoanCost = new HashMap<>();
			for (BKTransaction lBKTransaction : lBKHolder.getpListBKTransaction()) {
				/*
				 * Load BKTransaction and exit if the date is outside the range, the account is not PIROY or the BKIncome is not a loan 
				 */
				int lDate = lBKTransaction.getpDate();
				if (lDate < lDateStart || lDate > lDateStop) {
					continue;
				}
				String lBKIncome = lBKTransaction.getpBKIncome();
				if (!lBKIncome.equals(StaticBKIncome.getLOAN_COST())) {
					continue;
				}
				BKAsset lBKAsset = lBKTransaction.getpBKAsset();
				if (!lBKAsset.equals(BKAssetManager.getpBKCurrency("USD"))) {
					BasicPrintMsg.error("Error logic");
				}
				/*
				 * Store line to write in TreeMap
				 */
				Double lQuantity = lMapDateToLoanCost.get(lDate);
				if (lQuantity == null) {
					lQuantity = 0.;
				}
				lQuantity += -lBKTransaction.getpQuantity();
				lMapDateToLoanCost.put(lDate, lQuantity);
			}
			/*
			 * Add the loan cost at the end of each line
			 */
			for (int lDate : lMapDateToLoanCost.keySet()) {
				double lQuantity = lMapDateToLoanCost.get(lDate);
				String lLine = lTreeMapLineToWrite.get(lDate);
				lLine += "," + lQuantity;
				lTreeMapLineToWrite.put(lDate, lLine);
			}
		}
		/*
		 * Build the body of the file
		 */
		for (String lLine : lTreeMapLineToWrite.values()) {
			addToListLine(lLine);
		}
		/*
		 * Write file
		 */
		String lFileNameWithDate = lDateStop + StaticNames.getOUTPUT_BKLOANS();
		setpNameFile(lFileNameWithDate);
		writeFile();
		String lFileNameCurrent = StaticNames.getOUTPUT_BKLOANS().substring(1);
		setpNameFile(lFileNameCurrent);
		writeFile();
	}




}
