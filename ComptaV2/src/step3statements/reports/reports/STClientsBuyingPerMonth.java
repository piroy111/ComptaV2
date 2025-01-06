package step3statements.reports.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step3statements.reports.manager.STReportManager;

public class STClientsBuyingPerMonth extends BKHolderGenerator {

	public STClientsBuyingPerMonth(STReportManager _sSTReportManager) {
		super(_sSTReportManager.getpBKTransactionManager());

	}

	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return !_sBKTransaction.getpBKAccount().equals(BKAccountManager.getpBKAccountBunker())
				&& !_sBKTransaction.getpBKAccount().equals(BKAccountManager.getpBKAccountPierreRoy())
				&& _sBKTransaction.getpBKIncome().equals("Operations_Incoming_funds_from_client");
	}

	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAccount().getpEmailAddress();
	}

	
	public final void run() {
		generateBKHolder();
		/*
		 * Initiate
		 */
		int lDateStart = BasicDateInt.getmPlusBusinessDays(BasicDateInt.getmToday(), 1);
		int lDateStop = BasicDateInt.getmToday();
		Map<Integer, List<BKTransaction>> lMapMonthToListBKTransaction = new HashMap<Integer, List<BKTransaction>>();
		List<String> lListOrigin = new ArrayList<String>();
		/*
		 * Loop on the BKHolder
		 */
		for (BKHolder lBKHolder : getpListBKHolder()) {
			BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(BasicDateInt.getmToday());
			lBKInventory.getpValueUSD();
			/*
			 * Date start
			 */
			lDateStart = Math.min(lDateStart, lBKHolder.getpListDate().get(0));
			/*
			 * Fill lMapMonthToListBKTransaction: reorder the BKTransaction by month
			 */
			for (BKTransaction lBKTransaction: lBKHolder.getpListBKTransaction()) {
				int lMonth = BasicDateInt.getmMonthAndYear(lBKTransaction.getpDate());
				/*
				 * Get or Create 
				 */
				List<BKTransaction> lListBKTransaction = lMapMonthToListBKTransaction.get(lMonth);
				if (lListBKTransaction == null) {
					lListBKTransaction = new ArrayList<BKTransaction>();
					lMapMonthToListBKTransaction.put(lMonth, lListBKTransaction);
				}
				lListBKTransaction.add(lBKTransaction);
				/*
				 * Origin
				 */
				String lOrigin = lBKTransaction.getpBKAccount().getpCommercialOrigin();
				if (!lListOrigin.contains(lOrigin)) {
					lListOrigin.add(lOrigin);
				}
			}
		}
		/*
		 * Prepare file
		 */
		List<String> lListLineToWrite = new ArrayList<String>();
		/*
		 * Build the lines of the file
		 */
		double lAmountUSDYtD = 0.;
		for (int lDate = lDateStart; lDate <= lDateStop; lDate = BasicDateInt.getmPlusMonth(lDate, 1)) {
			int lMonth = BasicDateInt.getmMonthAndYear(lDate);
			/*
			 * Initiate
			 */
			List<BKTransaction> lListBKTransaction = lMapMonthToListBKTransaction.get(lMonth);
			double lAmountUSDMonth = 0.;
			Map<String, Double> lMapBKAccountToAmountUSD = new HashMap<String, Double>();
			Map<String, Double> lMapOriginToAmountUSD = new HashMap<String, Double>();
			/*
			 * Load and compute amount USD
			 */
			if (lListBKTransaction != null) {
				for (BKTransaction lBKTransaction : lListBKTransaction) {
					/*
					 * Compute total amount of incoming funds for the month
					 */
					lAmountUSDMonth += lBKTransaction.getpValueUSD();
					lAmountUSDYtD += lBKTransaction.getpValueUSD();
					/*
					 * Decomposition per account
					 */
					String lBKAccountStr = lBKTransaction.getpBKAccount().getpEmailAddress();
					Double lAmountPerBKAccount = lMapBKAccountToAmountUSD.get(lBKAccountStr);
					if (lAmountPerBKAccount == null) {
						lAmountPerBKAccount = 0.;
					}
					lAmountPerBKAccount += lBKTransaction.getpValueUSD();
					lMapBKAccountToAmountUSD.put(lBKAccountStr, lAmountPerBKAccount);
					/*
					 * Decomposition per commercial origin
					 */
					String lOrigin = lBKTransaction.getpBKAccount().getpCommercialOrigin();
					Double lAmountPerOrigin = lMapOriginToAmountUSD.get(lOrigin);
					if (lAmountPerOrigin == null) {
						lAmountPerOrigin = 0.;
					}
					lAmountPerOrigin += lBKTransaction.getpValueUSD();
					lMapOriginToAmountUSD.put(lOrigin, lAmountPerOrigin);
				}
			}
			/*
			 * Write line
			 */
			String lLine = lMonth
					+ "," + lAmountUSDMonth
					+ "," + lAmountUSDYtD;
			for (String lOrigin : lListOrigin) {
				if (lMapOriginToAmountUSD.containsKey(lOrigin)) {
					lLine += "," + lMapOriginToAmountUSD.get(lOrigin);
				} else {
					lLine += ",0";
				}
			}
			lLine += "," + lMapBKAccountToAmountUSD.toString().replaceAll(",", ";");
			lListLineToWrite.add(lLine);
		}
		/*
		 * Write the file
		 */
		String lHeader = "Month,Incoming funds from client,Total incoming fund from clients since creation";
		for (String lOrigin : lListOrigin) {
			lHeader += "," + lOrigin;
		}
		lHeader += ",Split per client of incoming funds";
		String lDir = StaticDir.getOUTPUT_INCOMING_FUNDS();
		BasicFichiers.getOrCreateDirectory(lDir);
		String lNameFile = BasicDateInt.getmToday() + StaticNames.getOUTPUT_INCOMING_FUNDS();
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
	}
	
	
	
	
	
	
	
	
	
	
}
