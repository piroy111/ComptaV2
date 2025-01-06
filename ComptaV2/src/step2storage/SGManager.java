package step2storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicDir;
import basicmethods.BasicFichiers;
import basicmethods.BasicFile;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import conf.bkstorage.BKStorageManager;
import staticdata.StaticDate;
import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step1loadtransactions.transactions.BKTransactionManager;

public class SGManager extends BKHolderGenerator {

	public SGManager(BKTransactionManager _sBKTransactionManager) {
		super(_sBKTransactionManager);
	}

	/*
	 * Static
	 */
	private static final String COMMENT = "Storage";
	private static final String COMMENT_BKINCOME = "Operations_Storage_incoming_from_clients";
	private static final String FILE_STORAGE = "Storage";

	public final void run() {
		generateBKHolder();
		/*
		 * Date max to count the storage cost --> we count until the end of the month
		 */
		int lDateStopStorage = BasicDateInt.getmToday();
		if (lDateStopStorage < BasicDateInt.getmEndOfMonth(lDateStopStorage)) {
			lDateStopStorage = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(lDateStopStorage, -1));
		}
		/*
		 * Initiate for global file
		 */
//		String lFileSummary = lDateStopStorage + "_" + FILE_STORAGE + ".csv";
		Map<BKAccount, Double> pMapBKAccountToStorageCost = new HashMap<BKAccount, Double>();
		List<String> lListLineToWriteInGlobalFile = new ArrayList<>();
		/*
		 * Loop on the BKHolder
		 */
		for (BKHolder lBKHolder : getpListBKHolder()) {
			/*
			 * Set the Currency
			 */
			String[] lArrayStr = lBKHolder.getpKey().split("; ", -1);
			if (lArrayStr.length ==2) {
				String lAccount = lArrayStr[0];
				if (lAccount.equals(BKAccountManager.getpBKAccountBunker().getpEmailAddress())
						|| lAccount.equals(BKAccountManager.getpBKAccountPierreRoy().getpEmailAddress())) {
					continue;
				}
				String lCurrencyRef = lArrayStr[1];
				BKAsset lBKAssetCurrencyRef = BKAssetManager.getpBKCurrency(lCurrencyRef);
				BKAccount lBKAccount = BKAccountManager.getpBKAccount(lAccount);
				String lSuffix = "_" + FILE_STORAGE + "_" + lBKAccount.getpEmailAddress() + ".csv";
				String lFileName = lDateStopStorage + lSuffix; 
				if (lBKAssetCurrencyRef != null) {
					/*
					 * Load the BKTransactions from the CSV file
					 */
					String lDir  = StaticDir.getTREATED_STORAGE();
					BasicDir lBasicDir = new BasicDir(lDir, lSuffix);
					BasicFile lBasicFile = lBasicDir.getmBasicFile(lDateStopStorage);
					int lDateStorageMonth = -1; 
					List<String> lListLineToWrite = new ArrayList<String>();
					boolean lIsOneLineToWrite = false;
					if (lBasicFile != null && lBasicFile.getmLitUnFichierEnLignes() != null) {
						LitUnFichierEnLignes lReadFile = lBasicFile.getmLitUnFichierEnLignes();
						for (List<String> lListLine : lReadFile.getmContenuFichierListe()) {
							int lIdx = -1;
							int lDate = BasicString.getInt(lListLine.get(++lIdx));
							double lCostStorage = BasicString.getDouble(lListLine.get(++lIdx));
							/*
							 * Create the BKTransactions
							 */
							createBKTransaction(lDate, lBKAssetCurrencyRef, lCostStorage, lBKAccount, lFileName);
							lDateStorageMonth = Math.max(lDate, lDateStorageMonth);
						}
						lListLineToWrite.addAll(lReadFile.getmContenuFichierLignes());
					}
					/*
					 * Create new BKTransactions
					 */
					if (lDateStorageMonth == -1) {
						lDateStorageMonth = BasicDateInt.getmPlusDay(
								pBKTransactionManager.getpListBKTransaction().get(0).getpDate(), -1);
					}
					double lCostStorageMonth= 0.;
					lDateStorageMonth = Math.max(lDateStorageMonth, StaticDate.getDATE_START_STORAGE_CHARGE());
					/*
					 * Initiate by moving on to the next month from the last recorded month in the CSV file
					 */
					int lDateStorageDay = lDateStorageMonth;
					lDateStorageMonth = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusDay(lDateStorageMonth, 1));
					/*
					 * If the current month has already been computed in the CSV file, then we don't compute any storage
					 */
					if (lDateStorageDay < lDateStopStorage) {
						/*
						 * Create the BKTransaction
						 */
						while (true) {
							lDateStorageDay = BasicDateInt.getmPlusDay(lDateStorageDay, 1);
							if (lDateStorageDay > lDateStopStorage) {
								break;
							}
							/*
							 * Aggregate the cost of storage during the month
							 */
							BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDateStorageDay);
							if (lBKInventory != null) {
								for (BKAsset lBKAsset : lBKInventory.getpMapBKAssetToQuantity().keySet()) {
									if (lBKAsset.getpIsBar()) {
										double lStorageCostPercent = BKStorageManager.getmStorageCost(lBKAccount.getpEmailAddress(), 
												lBKAsset.getpMetalName(), lDateStorageDay);
										Double lQuantityOz = lBKInventory.getpMapBKAssetToQuantity().get(lBKAsset);
										if (lQuantityOz != null && !AMNumberTools.isNaNOrNullOrZero(lQuantityOz)) {
											if (AMNumberTools.isNegativeStrict(lQuantityOz)) {
												BasicPrintMsg.error("The quantity should not be negative."
														+ "\nLast time it happened was because of an error of date in a file of delivery. The delivery was prior to the purchase"
														+ "\nAccount= " + lBKHolder.getpKey()
														+ "\nlDateStorageDay= " + lDateStorageDay
														+ "\nlQuantityOz= " + lQuantityOz);
											}
											double lCostStorageDay = -lQuantityOz
													* lBKAsset.getpPriceUSD(lDateStorageDay)
													/ lBKAssetCurrencyRef.getpPriceUSD(lDateStorageDay)
													* lStorageCostPercent
													/ 360;
											if (!AMNumberTools.isNaNOrZero(lCostStorageDay)) {
												lCostStorageMonth += lCostStorageDay;
											}
										}
									}
								}
							}
							/*
							 * Store the cost of storage end of month (i.e. when lDate = lDateStorage)
							 */
							if (lDateStorageDay >= lDateStorageMonth) {
								if (!AMNumberTools.isNaNOrZero(lCostStorageMonth)) {
									/*
									 * Create the BKTransactions
									 */
									createBKTransaction(lDateStorageMonth, lBKAssetCurrencyRef, lCostStorageMonth, lBKAccount, lFileName);
									/*
									 * Write a line for the file
									 */
									String lLine = lDateStorageMonth + "," + lCostStorageMonth;
									lListLineToWrite.add(lLine);
									lIsOneLineToWrite = true;
									/*
									 * Write line for the global file
									 */
									String lLineGlobal = lAccount + "," + lLine;
									lListLineToWriteInGlobalFile.add(lLineGlobal);
									/*
									 * Add to summary
									 */
									Double lCostStorageSummary = pMapBKAccountToStorageCost.get(lBKAccount);
									if (lCostStorageSummary == null) {
										lCostStorageSummary = 0.;
									}
									lCostStorageSummary += lCostStorageMonth;
									pMapBKAccountToStorageCost.put(lBKAccount, lCostStorageSummary);
								}
								/*
								 * Initialize
								 */
								lCostStorageMonth = 0.;
								lDateStorageMonth = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusDay(lDateStorageMonth, 1));
							}
						}
					}
					/*
					 * Write file
					 */
					if (lIsOneLineToWrite) {
						BasicFichiers.writeFile(lDir, lFileName, null, lListLineToWrite);
						System.out.println("File written : " + lDir + lFileName);
					}
				}
			}
		}
//		/*
//		 * Write file summary
//		 */
//		if (pMapBKAccountToStorageCost.size() > 0) {
//			String lDir = StaticDir.getOUTPUT_STORAGE();
//			List<String> lListLineToWrite = new ArrayList<String>();
//			for (BKAccount lBKAccount : pMapBKAccountToStorageCost.keySet()) {
//				String lLine = lBKAccount.getpEmailAddress()
//						+ "," + pMapBKAccountToStorageCost.get(lBKAccount);
//				lListLineToWrite.add(lLine);
//			}
//			String lHeader = "BKAccount,Date to book the cost of storage,Cost of storage in currency of the BKAccount";
//			BasicFichiers.writeFile(lDir, lFileSummary, lHeader, lListLineToWriteInGlobalFile);
//			System.out.println("File written : " + lDir + lFileSummary);
//		}
	}

	/**
	 * Create BKTransaction to debit the account of the client from the cost of storage, 
	 * and another BKTransaction to credit the account of Bunker
	 * @param _sDateStorage
	 * @param _sBKCurrency
	 * @param _sCostStorage
	 * @param _sBKAccount
	 * @param _sFileName
	 */
	private void createBKTransaction(int _sDateStorage, BKAsset _sBKCurrency, 
			double _sCostStorage, BKAccount _sBKAccount, String _sFileName) {
		int lDateCharged = BasicDateInt.getmPlusBusinessDays(BasicDateInt.getmPlusBusinessDays(_sDateStorage, 1), -1);
		if (lDateCharged > BasicDateInt.getmToday()) {
			return;
		}
		pBKTransactionManager.createBKTransaction(lDateCharged, COMMENT, 
				_sBKCurrency, _sCostStorage, _sBKAccount, COMMENT_BKINCOME, 
				Double.NaN, _sFileName);
		pBKTransactionManager.createBKTransaction(lDateCharged, COMMENT, 
				_sBKCurrency, -_sCostStorage, BKAccountManager.getpBKAccountBunker(), COMMENT_BKINCOME, 
				Double.NaN, _sFileName);
	}


	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return true;
	}


	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAccount().getpEmailAddress()
				+ "; " + _sBKTransaction.getpBKAccount().getpBKAssetCurrency().getpName();
	}


}
