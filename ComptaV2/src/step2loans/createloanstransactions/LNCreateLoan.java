package step2loans.createloanstransactions;

import basicmethods.BasicPrintMsg;
import staticdata.StaticBKIncome;
import staticdata.StaticDebug;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step0treatrawdata.conf.CFLoanCost;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.bars.BKBar;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;

public class LNCreateLoan extends LNAbstract {



	public LNCreateLoan(LNManager _sLNManager) {
		super(StaticDir.getTREATED_LOANS(), StaticNames.getFILE_LOAN(), _sLNManager);
		/*
		 * 
		 */
		pBKAccountBunker = BKAccountManager.getpBKAccountBunker();
		pBKAccountPRoy = BKAccountManager.getpBKAccountPierreRoy();
	}

	/*
	 * Static
	 */
	private static String CLIENTS = "CLIENTS";
	/*
	 * Data
	 */
	private BKAccount pBKAccountBunker;
	private BKAccount pBKAccountPRoy;
	private BKHolder pBKHolderBunker;
	private BKHolder pBKHolderPRoy;
	private BKHolder pBKHolderClients;
	private BKAsset pBKAssetUSD;

	/**
	 * We keep the bars
	 */
	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAsset().getpIsBar();
	}

	/**
	 * IDKey for the Map
	 */
	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		String lEmailAddress = _sBKTransaction.getpBKAccount().getpEmailAddress();
		if (lEmailAddress.equals(pBKAccountBunker.getpEmailAddress()) 
				|| lEmailAddress.equals(pBKAccountPRoy.getpEmailAddress())) {
			return lEmailAddress;
		} else {
			return CLIENTS;
		}
	}

	/**
	 * Read all the BKTransactions related to a BKBar and create a loan associated to it<br>
	 * Create a file with all the loans<br>
	 */
	@Override public final void initiate() {
		/*
		 * Identify BKHolder of Bunker and PRoy
		 */
		pBKHolderBunker = pMapKeyToBKHolder.get(pBKAccountBunker.getpEmailAddress());
		pBKHolderPRoy = pMapKeyToBKHolder.get(pBKAccountPRoy.getpEmailAddress());
		pBKHolderClients = pMapKeyToBKHolder.get(CLIENTS);
		pBKAssetUSD = BKAssetManager.getpAndCheckBKAsset("USD");
	}

	/**
	 * Create all the BKTransactions due to the loan
	 */
	@Override public final void createBKTransaction(int _sDate) {
		if (_sDate <= pLNManager.getpDateLimitToStart()) {
			return;
		}
		/*
		 * Get out if there is no inventory
		 */
		BKInventory lBKInventoryDate = pBKHolderClients.getpMapDateToBKInventory().get(_sDate);
		if (lBKInventoryDate == null) {
			return;
		}
		/*
		 * Create loans for each bar
		 */
		for (BKBar lBKBar : lBKInventoryDate.getpMapBKBarToTransactedToday().keySet()) {
			int lQuantity = lBKInventoryDate.getpMapBKBarToTransactedToday().get(lBKBar);
			/*
			 * Check the bar is held by only one client
			 */
			if (lQuantity < -1 || 1 < lQuantity) {
				String lMsgError = "The bar is held by several clients on the same date"
						+ "\nList of BKTransactions for this date (" + _sDate + ")";
				for (BKTransaction lBKTransaction : lBKInventoryDate.getpListBKTransactionToday()) {
					if (lBKTransaction.getpComment().equals(lBKBar.getpRef()) && lBKTransaction.getpBKAsset().equals(lBKBar.getpBKAsset())) {
						lMsgError += "\n   BKtransaction= " + lBKTransaction.toString();
					}
				}
				lMsgError += "\n\nlBKInventoryDate= " + lBKInventoryDate.toString()
				+ "\nId(pBKHolderClients)= " + pBKHolderClients.hashCode()
				+ "\nNumber of BKBar held by lBKInventoryDate= " + lBKInventoryDate.getpBKBarHolding(lBKBar);
				lMsgError += "\n\nUSE CLASS 'StaticDebug', PUT THE NAME OF THE BAR ('" + lBKBar.getpRef() + "') AND TRACK THE MOVEMENTS OF THE BAR";
				System.err.println(lBKInventoryDate.getpIDTrackFrom());
				BasicPrintMsg.error(lMsgError);
			}
			/*
			 * Load data
			 */
			BKAsset lBKAsset = lBKBar.getpBKAsset();
			String lComment = lBKBar.getpRef();
			double lValueUSD = lBKBar.getpWeightOz() * lBKBar.getpBKAsset().getpPriceUSD(_sDate);
			/*
			 * Load the holding of Bunker
			 */
			int lQuantityTodayBunker = 0;
			BKInventory lBKInventoryBunker = null;
			if (pBKHolderBunker != null) {
				lBKInventoryBunker = pBKHolderBunker.getpMapDateToBKInventory().get(_sDate);
				lQuantityTodayBunker = lBKInventoryBunker.getpBKBarBoughtToday(lBKBar) + lBKInventoryBunker.getpBKBarSoldToday(lBKBar);
			}
			/*
			 * Load the holding of the clients
			 */
			int lQuantityClientBought = lBKInventoryDate.getpBKBarBoughtToday(lBKBar);
			int lQuantityClientSold = lBKInventoryDate.getpBKBarSoldToday(lBKBar);
			/*
			 * Special case where the bar was delivered to the client --> We end up with a sell and Bunker does not get back the bar
			 * In this case, we just continue. We need to check the holding is zero
			 */
			if (lQuantityClientSold == -1 && lQuantityTodayBunker == 0) {
				continue;
			}
			/*
			 * Check lQuantityTodayBunker = -lQuantityClientSold. This comes from the client files where we create a mirror transaction when the client sells but not when he buys
			 */
			if (lQuantityTodayBunker != -lQuantityClientSold) {
				BasicPrintMsg.printError("This should not be possible. It means there is a client file where the client sells the bar and Bunker does not buy it (missing the Bunker transaction)");				
			}
			/*
			 * Each time a client bought a bar, and a client sold the same bar on the same day, we need to create a mirror transaction Bunker for the client who bought 
			 */
			for (int lIdx = 0; lIdx < Math.min(lQuantityClientBought, -lQuantityClientSold); lIdx++) {
				createBKTransaction(_sDate, lBKAsset, lComment, -lBKBar.getpWeightOz(), pBKAccountBunker, StaticBKIncome.getBARS_BUNKER());
				lQuantityTodayBunker--;
			}
			if (lQuantityClientBought + lQuantityClientSold + lQuantityTodayBunker != 0
					&& lQuantityClientBought + lQuantityClientSold + lQuantityTodayBunker != 1) {
				BasicPrintMsg.error("Impossible");
			}
			/*
			 * Ignore if lQuantityTodayBunker + lQuantityClientBought + lQuantityClientSold == 0 --> it means one client bought and another one sold, 
			 * or that one client sold and we have the hedge by Bunker directly from the client file
			 */
			if (lQuantityClientBought + lQuantityClientSold + lQuantityTodayBunker == 0) {
				continue;
			}
			if (lQuantityClientBought + lQuantityClientSold + lQuantityTodayBunker != 1) {
				BasicPrintMsg.error("Not possible");
			}
			/*
			 * The case which is left is : lQuantityClientBought + lQuantityClientSold + lQuantityTodayBunker == 1
			 * This case is when the client bought the bar. We need to know if the bar comes from Bunker or PRoy
			 * First, we check whether the bar was held by Bunker (from this stage the quantity can only be 1)
			 */
			if (lBKInventoryBunker != null && lBKInventoryBunker.getpBKBarHolding(lBKBar) == 1) {
				/*
				 * Bunker gives the bar to the client
				 */
				createBKTransaction(_sDate, lBKAsset, lComment, -lBKBar.getpWeightOz(), pBKAccountBunker, StaticBKIncome.getBARS_BUNKER());							
			}						
			/*
			 * Check whether the bar was held by PRoy
			 */
			else {
				BKInventory lBKInventoryPRoy = null;
				if (pBKHolderPRoy != null) {
					lBKInventoryPRoy = pBKHolderPRoy.getpMapDateToBKInventory().get(_sDate);
				}
				if (lBKInventoryPRoy != null && lBKInventoryPRoy.getpBKBarHolding(lBKBar) == 1) {
					/*
					 * PRoy gives the bar to the client
					 */
					createBKTransaction(_sDate, lBKAsset, lComment, -lBKBar.getpWeightOz(), 
							pBKAccountPRoy, StaticBKIncome.getLOAN(lBKAsset));
					/*
					 * Bunker gives a bar loan to PRoy
					 */
					BKAsset lBKAssetLoan = lBKAsset.getpLoanFromBar();
					createBKTransaction(_sDate, lBKAssetLoan, lComment, -lBKBar.getpWeightOz(), 
							pBKAccountBunker, StaticBKIncome.getLOAN(lBKAssetLoan));
					createBKTransaction(_sDate, lBKAssetLoan, lComment, lBKBar.getpWeightOz(), 
							pBKAccountPRoy, StaticBKIncome.getLOAN(lBKAssetLoan));
					/*
					 * Bunker pays a fee to PRoy for the cost of the loan
					 */
					double lCostLoan = CFLoanCost.getpCostLoan(lBKAssetLoan.getpName(),	_sDate) * lValueUSD;
					createBKTransaction(_sDate, pBKAssetUSD, "Loss due to loan of " + lComment, -lCostLoan, 
							pBKAccountBunker, StaticBKIncome.getLOAN_COST());
					createBKTransaction(_sDate, pBKAssetUSD, "Gain due to loan of " + lComment, lCostLoan, 
							pBKAccountPRoy, StaticBKIncome.getLOAN_COST());
				} else {
					String lMsgError = "The bar should have been held either by PRoy or Bunker when the client bought it"
							+ "\nList of BKTransactions for this date (" + _sDate + ")";
					for (BKTransaction lBKTransaction : lBKInventoryDate.getpListBKTransactionToday()) {
						if (lBKTransaction.getpComment().equals(lBKBar.getpRef()) && lBKTransaction.getpBKAsset().equals(lBKBar.getpBKAsset())) {
							lMsgError += "\n   BKtransaction= " + lBKTransaction.toString();
						}
					}
					if (lBKInventoryPRoy != null) {
						lMsgError += "\n\nlBKInventoryPRoy= " + lBKInventoryPRoy.toString()
						+ "\nId(pBKHolderPRoy)= " + pBKHolderPRoy.hashCode()
						+ "\nNumber of BKBar held by lBKInventoryPRoy= " + lBKInventoryPRoy.getpBKBarHolding(lBKBar);
						lMsgError += "\n\nlBKInventoryBunker= " + lBKInventoryBunker.toString()
						+ "\nId(pBKHolderBunker)= " + pBKHolderBunker.hashCode()
						+ "\nNumber of BKBar held by lBKInventoryBunker= " + lBKInventoryBunker.getpBKBarHolding(lBKBar);
						lMsgError += "\n\nUSE CLASS 'StaticDebug', PUT THE NAME OF THE BAR ('" + lBKBar.getpRef() + "') AND TRACK THE MOVEMENTS OF THE BAR";
						lMsgError += "\nThe file debug with all the transaction regarding the bar of StaticDebug is written in '" + StaticDebug.getDIR_DEBUG() + StaticDebug.getNAME_DEBUG() + "'";
						System.err.println(lBKInventoryPRoy.getpIDTrackFrom());
					}
					BasicPrintMsg.error(lMsgError);
				}
			}
		}
	}

	//	/**
	//	 * Create all the BKTransactions due to the loan
	//	 */
	//	@Override public final void createBKTransaction(int _sDate) {
	//		/*
	//		 * Loop over the bars which the clients hold long
	//		 */
	//		for (BKHolder lBKHolder : pMapKeyToBKHolder.values()) {
	//			/*
	//			 * Test if the BKHolder is a client (not PRoy)
	//			 */
	//			if (!lBKHolder.equals(pBKHolderBunker) && !lBKHolder.equals(pBKHolderPRoy)) {
	//				BKInventory lBKInventoryDate = lBKHolder.getpMapDateToBKInventory().get(_sDate);
	//				if (lBKInventoryDate == null) {
	//					continue;
	//				}
	//				for (BKTransaction lBKTransaction : lBKInventoryDate.getpListBKTransactionToday()) {
	//					/*
	//					 * Ignore if the date of the BKTransaction is under the date of the files already there
	//					 */
	//					int lDate = lBKTransaction.getpDate();
	//					if (lDate <= pLNManager.getpDateLimitToStart()) {
	//						continue;
	//					}
	//					/*
	//					 * Ignore if the quantity < 0 --> it means the client is selling the bar, there is no loan to create in this case
	//					 */
	//					double lQuantity = lBKTransaction.getpQuantity();
	//					if (AMNumberTools.isNegativeOrNull(lQuantity)) {
	//						continue;
	//					}
	//					/*
	//					 * Load data
	//					 */
	//					BKAsset lBKAsset = lBKTransaction.getpBKAsset();
	//					String lComment = lBKTransaction.getpComment();
	//					BKBar lBKBar = BKBarManager.getpAndCheckBKBar(lBKTransaction);
	//					/*
	//					 * Check whether the bar was held by Bunker
	//					 */
	//					BKInventory lBKInventoryBunker = null;
	//					if (pBKHolderBunker != null) {
	//						BKInventory lBKInventory = pBKHolderBunker.getpMapDateToBKInventory()
	//								.get(lBKTransaction.getpDate());
	//						if (lBKInventory != null) {
	//							lBKInventoryBunker = lBKInventory.getpBKInventoryPrevious();
	//						}
	//					}
	//					if (lBKInventoryBunker != null && lBKInventoryBunker.getpBKBarHolding(lBKBar) == 1) {
	//						/*
	//						 * Bunker gives the bar to the client
	//						 */
	//						createBKTransaction(lDate, lBKAsset, lComment, -lQuantity, 
	//								pBKAccountBunker, StaticBKIncome.getBARS_BUNKER());							
	//					}						
	//					/*
	//					 * Check whether the bar was held by PRoy
	//					 */
	//					else {
	//						BKInventory lBKInventoryPRoy = null;
	//						if (pBKHolderPRoy != null) {
	//							lBKInventoryPRoy = pBKHolderPRoy.getpMapDateToBKInventory()
	//									.get(lBKTransaction.getpDate()).getpBKInventoryPrevious();
	//						}
	//						if (lBKInventoryPRoy != null && lBKInventoryPRoy.getpBKBarHolding(lBKBar) == 1) {
	//							/*
	//							 * PRoy gives the bar to the client
	//							 */
	//							createBKTransaction(lDate, lBKAsset, lComment, -lQuantity, 
	//									pBKAccountPRoy, StaticNames.getLOAN(lBKAsset));
	//							/*
	//							 * Bunker gives a bar loan to PRoy
	//							 */
	//							BKAsset lBKAssetLoan = lBKAsset.getpLoanFromBar();
	//							createBKTransaction(lDate, lBKAssetLoan, lComment, -lQuantity, 
	//									pBKAccountBunker, StaticNames.getLOAN(lBKAssetLoan));
	//							createBKTransaction(lDate, lBKAssetLoan, lComment, lQuantity, 
	//									pBKAccountPRoy, StaticNames.getLOAN(lBKAssetLoan));
	//							/*
	//							 * Bunker pays a fee to PRoy for the cost of the loan
	//							 */
	//							double lCostLoan = CFLoanCost.getpCostLoan(lBKAssetLoan.getpName(),	lDate) * lBKTransaction.getpValueUSD();
	//							createBKTransaction(lDate, pBKAssetUSD, "Loss due to loan of " + lComment, -lCostLoan, 
	//									pBKAccountBunker, StaticNames.getLOAN_COST());
	//							createBKTransaction(lDate, pBKAssetUSD, "Gain due to loan of " + lComment, lCostLoan, 
	//									pBKAccountPRoy, StaticNames.getLOAN_COST());
	//						} else {
	//							String lMsgError = "The bar should have been held either by PRoy or Bunker when the client bought it"
	//									+ "\nBKTransaction= " + lBKTransaction.toString();
	//							if (lBKInventoryPRoy != null) {
	//								lMsgError += "\n\nlBKInventoryPRoy= " + lBKInventoryPRoy.toString()
	//								+ "\nId(pBKHolderPRoy)= " + pBKHolderPRoy.hashCode()
	//								+ "\nNumber of BKBar held by lBKInventoryPRoy= " + lBKInventoryPRoy.getpBKBarHolding(lBKBar);
	//								lMsgError += "\n\nlBKInventoryBunker= " + lBKInventoryBunker.toString()
	//								+ "\nId(pBKHolderBunker)= " + pBKHolderBunker.hashCode()
	//								+ "\nNumber of BKBar held by lBKInventoryBunker= " + lBKInventoryBunker.getpBKBarHolding(lBKBar);
	//								lMsgError += "\n\nUSE CLASS 'StaticDebug', PUT THE NAME OF THE BAR ('" + lBKTransaction.getpComment() + "') AND TRACK THE MOVEMENTS OF THE BAR";
	//								System.err.println(lBKInventoryPRoy.getpIDTrackFrom());
	//							}
	//							BasicPrintMsg.error(lMsgError);
	//						}
	//					}
	//				}
	//			}
	//		}
	//	}




}
