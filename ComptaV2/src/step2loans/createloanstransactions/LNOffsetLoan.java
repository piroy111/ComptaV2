package step2loans.createloanstransactions;

import java.util.HashMap;
import java.util.Map;

import basicmethods.AMNumberTools;
import staticdata.StaticBKIncome;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.bars.BKBar;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;

public class LNOffsetLoan extends LNAbstract {


	/**
	 * Offset of loans happens when we buy bars at refiners.<br>
	 * There are two cases: whether the bars are bought by Bunker, in case Bunker will pass the bars to PRoy in exchange of the loan<br>
	 * the second case if that PRoy buys the bars directly, in which case Bunker will pass cash equivalent to PRoy in order to offset the loans<br>
	 * @param _sLNManager
	 */
	public LNOffsetLoan(LNManager _sLNManager) {
		super(StaticDir.getTREATED_LOANS(), StaticNames.getFILE_LOAN_OFFSET(), _sLNManager);
	}

	/*
	 * Data
	 */
	private BKAccount pBKAccountBunker;
	private BKAccount pBKAccountPRoy;
	private BKHolder pBKHolderBunkerBar;
	private BKHolder pBKHolderPRoyBar;
	private BKAsset pBKAssetUSD;
	private BKHolder pBKHolderPRoyBarLoan;
	private Map<BKAsset, Double> pMapBKBarLoanToQuantity;
	private boolean pIsStarted;

	/**
	 * Keep only the bars bought to the refiner
	 */
	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return getpUniqueKey(_sBKTransaction) != null;
	}

	/**
	 * Only one BKHolder
	 */
	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		/*
		 * Case Bunker or PRoy buys  a physical bar
		 */
		if (_sBKTransaction.getpBKAsset().getpIsBar()
				&& (_sBKTransaction.getpBKAccount().equals(BKAccountManager.getpBKAccountBunker())
						|| _sBKTransaction.getpBKAccount().equals(BKAccountManager.getpBKAccountPierreRoy()))) {
			return _sBKTransaction.getpBKAccount().getpEmailAddress();
		}
		/*
		 * Case there is a loan at PRoy
		 */
		if (_sBKTransaction.getpBKAsset().getpIsBarLoan()
				&& _sBKTransaction.getpBKAccount().equals(BKAccountManager.getpBKAccountPierreRoy())) {
			return "Loan PRoy";
		}
		/*
		 * Other case
		 */
		return null;
	}

	/**
	 * Build the objects 
	 */
	@Override public final void initiate() {
		pIsStarted = false;
		/*
		 * Instantiate objects
		 */
		pBKAccountBunker = BKAccountManager.getpBKAccountBunker();
		pBKAccountPRoy = BKAccountManager.getpBKAccountPierreRoy();
		pBKHolderBunkerBar = pMapKeyToBKHolder.get(pBKAccountBunker.getpEmailAddress());
		pBKHolderPRoyBar = pMapKeyToBKHolder.get(pBKAccountPRoy.getpEmailAddress());
		pBKHolderPRoyBarLoan = pMapKeyToBKHolder.get("Loan PRoy");
		pBKAssetUSD = BKAssetManager.getpAndCheckBKAsset("USD");
		pMapBKBarLoanToQuantity = new HashMap<BKAsset, Double>();
	}

	/**
	 * Read all the BKTransactions related to a BKBar and create a loan associated to it<br>
	 * Create a file with all the loans<br>
	 */
	@Override public final void createBKTransaction(int _sDate) {
		/*
		 * Compute at date start
		 */
		if (!pIsStarted) {
			computeQtyToOffset(_sDate);
			pIsStarted = true;
		}
		/*
		 * Update quantity to offset
		 */
		else {
			updateQtyToOffset(_sDate);
		}
		/*
		 * Offset loans
		 */
		offsetLoan(_sDate);
	}

	/**
	 * Start with the quantity loan to offset on the first day
	 */
	private void computeQtyToOffset(int _sDateStart) {
		BKInventory lBKInventoryLoan = pBKHolderPRoyBarLoan.getpMapDateToBKInventory().get(_sDateStart);
		for (BKAsset lBKAssetLoan : lBKInventoryLoan.getpMapBKAssetToQuantity().keySet()) {
			Double lQtyLoan = lBKInventoryLoan.getpMapBKAssetToQuantity().get(lBKAssetLoan);
			pMapBKBarLoanToQuantity.put(lBKAssetLoan, lQtyLoan);
		}
	}

	/**
	 * Update the quantity loan to offset with the quantity of each day
	 */
	private void updateQtyToOffset(int _sDate) {
		BKInventory lBKInventoryLoan = pBKHolderPRoyBarLoan.getpMapDateToBKInventory().get(_sDate);
		if (lBKInventoryLoan != null) {
			for (BKTransaction lBKTransaction : lBKInventoryLoan.getpListBKTransactionToday()) {
				BKAsset lBKAsset = lBKTransaction.getpBKAsset();
				if (lBKAsset.getpIsBarLoan()) {
					/*
					 * Update the map
					 */
					Double lQtyLoan = lBKTransaction.getpQuantity();
					Double lQtyLoanPrevious = pMapBKBarLoanToQuantity.get(lBKAsset);
					if (lQtyLoanPrevious == null) {
						lQtyLoanPrevious = 0.;
					}
					/*
					 * Update the QtyLoan as the quantity cumulated
					 */
					lQtyLoan += lQtyLoanPrevious;
					pMapBKBarLoanToQuantity.put(lBKAsset, lQtyLoan);
				}
			}
		}
	}

	/**
	 * 
	 * @param _sDate
	 */
	private void offsetLoan(int _sDate) {
		/*
		 * Initiate
		 */
		BKInventory lBKInventoryBunker = pBKHolderBunkerBar.getpMapDateToBKInventory().get(_sDate);
		BKInventory lBKInventoryPRoy = pBKHolderPRoyBar.getpMapDateToBKInventory().get(_sDate);
		BKInventory lBKInventoryPRoyLoans = pBKHolderPRoyBarLoan.getpMapDateToBKInventory().get(_sDate);

		////////////////////////////////////////////////////////////////////
		System.out.println(_sDate + ": Loan gold= " + lBKInventoryPRoyLoans.getpMapBKAssetToQuantity().get(BKAssetManager.getpAndCheckBKAsset("GOLD LOAN OZ")));
		//////////////////////////////////////////////////////////////////

		//		/*
		//		 * 
		//		 */
		//		for (BKAsset lBKAssetLoan : pMapBKBarLoanToQuantity.keySet()) {
		//			/*
		//			 * Load
		//			 */
		//			double lQtyLoan = pMapBKBarLoanToQuantity.get(lBKAssetLoan);

		/*
		 * 
		 */
		for (BKAsset lBKAssetLoan : lBKInventoryPRoyLoans.getpMapBKAssetToQuantity().keySet()) {
			/*
			 * Load
			 */
			double lQtyLoan = lBKInventoryPRoyLoans.getpMapBKAssetToQuantity().get(lBKAssetLoan);
			BKAsset lBKAssetBar = lBKAssetLoan.getpBarFromLoan();
			/*
			 * Case there is a bar bought by Bunker on this date --> we transfer the bar to PRoy
			 */
			//			if (AMNumberTools.isPositiveStrict(lQtyLoan) && lBKInventoryBunker != null) {
			//				for (BKTransaction lBKTransaction : lBKInventoryBunker.getpListBKTransactionToday()) {
			//					Double lQtyBarBunker = lBKTransaction.getpQuantity();
			//					if (lBKTransaction.getpBKAsset().equals(lBKAssetBar)
			//							&& AMNumberTools.isPositiveStrict(lQtyBarBunker)
			//							&& AMNumberTools.isSmallerOrEqual(lQtyBarBunker, lQtyLoan)) {
			//						String lComment = lBKTransaction.getpComment();
			//						/*
			//						 * Transfer the real bar to PRoy
			//						 */
			//						createBKTransaction(_sDate, lBKAssetBar, lComment, -lQtyBarBunker, pBKAccountBunker, StaticBKIncome.getBARS_BUNKER());
			//						createBKTransaction(_sDate, lBKAssetBar, lComment, lQtyBarBunker, pBKAccountPRoy, null);
			//						/*
			//						 * Offset the loan
			//						 */
			//						createBKTransaction(_sDate, lBKAssetLoan, lComment, lQtyBarBunker, pBKAccountBunker, null);
			//						createBKTransaction(_sDate, lBKAssetLoan, lComment, -lQtyBarBunker, pBKAccountPRoy, null);
			//						/*
			//						 * Communication
			//						 */
			//						System.out.println(_sDate + ": Transfer a bar from Bunker (transaction) to Proy"
			//								+ "; Bar number= " + lBKTransaction.getpComment()
			//								+ "; Bar= " + lBKAssetBar.getpName()
			//								+ "; Qty= " + lQtyBarBunker
			//								+ "; QtyLoan remaining= " + (lQtyLoan - lQtyBarBunker));
			//						/*
			//						 * Update quantity loan to offset 
			//						 */
			//						lQtyLoan += -lQtyBarBunker;
			//						if (AMNumberTools.isNegativeOrNull(lQtyLoan)) {
			//							break;
			//						}
			//					}
			//				}
			//			}
			/*
			 * Case Bunker is holding bars --> we transfer the bar to PRoy
			 */
			if (AMNumberTools.isPositiveStrict(lQtyLoan) 
					&& lBKInventoryBunker != null) {
				for (BKBar lBKBarBunker : lBKInventoryBunker.getpMapBKBarToHolding().keySet()) {
					if (lBKAssetLoan.getpMetalName().equals(lBKBarBunker.getpBKBarType().getmMetal())
							&& lBKInventoryBunker.getpMapBKBarToHolding().get(lBKBarBunker) == 1) {
						double lQtyBarBunker = lBKBarBunker.getpWeightOz();
						if (AMNumberTools.isSmallerOrEqual(lQtyBarBunker, lQtyLoan)) {
							String lComment = lBKBarBunker.getpRef();
							/*
							 * Transfer the real bar to PRoy
							 */
							createBKTransaction(_sDate, lBKAssetBar, lComment, -lQtyBarBunker, pBKAccountBunker, StaticBKIncome.getBARS_BUNKER());
							createBKTransaction(_sDate, lBKAssetBar, lComment, lQtyBarBunker, pBKAccountPRoy, null);
							/*
							 * Offset the loan
							 */
							createBKTransaction(_sDate, lBKAssetLoan, lComment, lQtyBarBunker, pBKAccountBunker, null);
							createBKTransaction(_sDate, lBKAssetLoan, lComment, -lQtyBarBunker, pBKAccountPRoy, null);
							/*
							 * Communication
							 */
							System.out.println(_sDate + ": Transfer a bar from Bunker (holdings) to Proy"
									+ "; Bar number= " + lComment
									+ "; Bar= " + lBKAssetBar.getpName()
									+ "; Qty= " + lQtyBarBunker
									+ "; QtyLoan remaining= " + (lQtyLoan - lQtyBarBunker));
							/*
							 * Update quantity loan to offset 
							 */
							lQtyLoan += -lQtyBarBunker;
							if (AMNumberTools.isNegativeOrNull(lQtyLoan)) {
								break;
							}
						}
					}
				}
			}
			/*
			 * Case PRoy is buying a bar --> Bunker repays the cash of the transaction
			 */
			if (lBKInventoryPRoy != null && AMNumberTools.isPositiveStrict(lQtyLoan)) {
				for (BKTransaction lBKTransaction : lBKInventoryPRoy.getpListBKTransactionToday()) {
					Double lQtyBarProy = lBKTransaction.getpQuantity();
					if (lBKTransaction.getpBKAsset().equals(lBKAssetBar)
							&& AMNumberTools.isPositiveStrict(lQtyBarProy)) {
						/*
						 * Compute amount USD to offset
						 */
						double lQtyToOffset = Math.min(lQtyBarProy, lQtyLoan);
						double lAmountUSDToOffset = lQtyToOffset * lBKTransaction.getpBKPrice();
						if (!AMNumberTools.isNaNOrZero(lAmountUSDToOffset)) {
							String lComment = "Offset loan at the time that " + lBKAssetBar.getpName()
							+ " are being bought by " + pBKAccountPRoy;
							/*
							 * Transfer the cash to PRoy
							 */
							createBKTransaction(_sDate, pBKAssetUSD, lComment, -lAmountUSDToOffset, pBKAccountBunker, null);
							createBKTransaction(_sDate, pBKAssetUSD, lComment, lAmountUSDToOffset, pBKAccountPRoy, null);
							/*
							 * Offset the loan
							 */
							createBKTransaction(_sDate, lBKAssetLoan, lComment, lQtyToOffset, pBKAccountBunker, null);
							createBKTransaction(_sDate, lBKAssetLoan, lComment, -lQtyToOffset, pBKAccountPRoy, null);
							/*
							 * Communication
							 */
							System.out.println("Transfer cash from Bunker to Proy to offset loan"
									+ "; Bar number= " + lBKTransaction.getpComment()
									+ "; BKAssetBar= " + lBKAssetBar.getpName()
									+ "; Qty= " + lQtyToOffset
									+ "; QtyLoan remaining= " + (lQtyLoan - lQtyToOffset));
							/*
							 * Update quantity loan to offset 
							 */
							lQtyLoan += -lQtyToOffset;
							if (AMNumberTools.isNegativeOrNull(lQtyLoan)) {
								break;
							}
						}
					}
				}
			}
		}
	}










}
