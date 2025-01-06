package step1loadtransactions.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import staticdata.StaticDebug;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.bars.BKBar;
import step1loadtransactions.bars.BKBarManager;
import step1loadtransactions.transactions.BKTransaction;

public class BKInventory implements Comparable<BKInventory> {

	/**
	 * a BKInventory(Date) contains the aggregate of the BKTransactions prior and equal to the date of the BKInventory<br>
	 * the BKInventoryManager will create all the BKInventory for all the dates 
	 * and compute the data of the BKInventory: ValueUSD, Map(BKAsset, Quantity)<br>
	 * @param _sDate :
	 * @param _sIDTRackFrom : optional, allows to track who has requested for the BKInventory
	 */
	protected BKInventory(int _sDate, String _sIDTrackFrom, String _sClassCreatorTrackFrom) {
		pDate = _sDate;
		pIDTrackFrom = _sIDTrackFrom;
		pClassCreatorTrackFrom = _sClassCreatorTrackFrom;
		/*
		 * Initiate
		 */
		pMapBKAssetToQuantity = new HashMap<BKAsset, Double>();
		pListBKTransactionToday = new ArrayList<BKTransaction>();
		pMapBKAssetToQuantity = new HashMap<BKAsset, Double>();
		pMapBKBarToHolding = new HashMap<BKBar, Integer>();
		pMapBKBarToTransactedToday = new HashMap<>();
		pMapBKBarToBoughtToday = new HashMap<>();
		pMapBKBarToSoldToday = new HashMap<>();
		pMapBKCurrencyToIncomingFunds = new HashMap<BKAsset, Double>();
		pIncomingFundsUSD = 0.;
		pDateYstD = BasicDateInt.getmPlusDay(pDate,  -1);
	}

	/*
	 * Data
	 */
	private int pDate;
	private int pDateYstD;
	private double pValueUSD;
	private double pValueUSDPaper;
	private double pValueUSDWithPriceYstD;
	private double pValueUSDPaperWithPriceYstD;
	private Map<BKAsset, Double> pMapBKAssetToQuantity;
	private Map<BKBar, Integer> pMapBKBarToHolding;
	private Map<BKBar, Integer> pMapBKBarToTransactedToday;
	private Map<BKBar, Integer> pMapBKBarToBoughtToday;
	private Map<BKBar, Integer> pMapBKBarToSoldToday;
	private List<BKTransaction> pListBKTransactionToday;
	private BKInventory pBKInventoryPrevious;
	private String pIDTrackFrom;
	private String pClassCreatorTrackFrom;
	private Map<BKAsset, Double> pMapBKCurrencyToIncomingFunds;
	private double pIncomingFundsUSD;

	/**
	 * Add new BKTransaction to the list
	 * @param _sBKTransaction
	 */
	protected final void declareNewBKTransaction(BKTransaction _sBKTransaction) {
		pListBKTransactionToday.add(_sBKTransaction);
	}

	/**
	 * 
	 * @param _sBKInventoryPrevious
	 */
	public final void compute() {
		/*
		 * Count the quantity of each BKAsset
		 */
		if (pBKInventoryPrevious != null) {
			pMapBKAssetToQuantity = new HashMap<BKAsset, Double>(pBKInventoryPrevious.getpMapBKAssetToQuantity());
		} else {
			pMapBKAssetToQuantity = new HashMap<BKAsset, Double>();
		}
		for (BKTransaction lBKTransaction : pListBKTransactionToday) {
			BKAsset lBKAsset = lBKTransaction.getpBKAsset();
			Double lQuantity = pMapBKAssetToQuantity.get(lBKAsset);
			if (lQuantity == null) {
				lQuantity = 0.;
			}
			lQuantity += lBKTransaction.getpQuantity();
			pMapBKAssetToQuantity.put(lBKAsset, lQuantity);
		}
		/*
		 * Initiate the holdings of the bars
		 */
		if (pBKInventoryPrevious != null) {
			pMapBKBarToHolding = new HashMap<BKBar, Integer>(pBKInventoryPrevious.pMapBKBarToHolding);
		} else {
			pMapBKBarToHolding = new HashMap<BKBar, Integer>();
		}
		pMapBKBarToTransactedToday.clear();
		pMapBKBarToBoughtToday.clear();
		pMapBKBarToSoldToday.clear();
		/*
		 * Track the holding of the BKBars
		 */
		for (BKTransaction lBKTransaction : pListBKTransactionToday) {
			BKAsset lBKAsset = lBKTransaction.getpBKAsset();
			if (lBKAsset.getpIsBar()) {
				BKBar lBKBar = BKBarManager.getpAndCheckBKBar(lBKTransaction);
				/*
				 * Get or create
				 */
				Integer lHolding = pMapBKBarToHolding.get(lBKBar);
				if (lHolding == null) {
					lHolding = 0;
				}
				Integer lTransactedToday = pMapBKBarToTransactedToday.get(lBKBar);
				Integer lBoughtToday = pMapBKBarToBoughtToday.get(lBKBar);
				Integer lSoldToday = pMapBKBarToSoldToday.get(lBKBar);
				if (lTransactedToday == null) {
					lTransactedToday = 0;
					lBoughtToday = 0;
					lSoldToday = 0;
				}
				/*
				 * Compute the new holdings
				 */
				int lTransaction = (int) Math.round(lBKTransaction.getpQuantity() 
						/ lBKBar.getpWeightOz());
				lHolding += lTransaction;
				lTransactedToday += lTransaction;
				lBoughtToday += Math.max(0, lTransaction);
				lSoldToday += Math.min(0, lTransaction);
				/*
				 * Update Map of BKBar
				 */
				pMapBKBarToHolding.put(lBKBar, lHolding);
				pMapBKBarToTransactedToday.put(lBKBar, lTransactedToday);
				pMapBKBarToBoughtToday.put(lBKBar, lBoughtToday);
				pMapBKBarToSoldToday.put(lBKBar, lSoldToday);
			}
		}
		/*
		 * Debug
		 */
		if (StaticDebug.getIS_ACTIVATE_BKINVENTORY_DEBUG()
				&& !StaticDebug.getBKINVENTORY_ID_FROM().equals("") 
				&& !StaticDebug.getBKINVENTORY_CLASS_FROM().equals("")
				&& !StaticDebug.getDEBUG_BAR().equals("")) {
			if (pIDTrackFrom.equals(StaticDebug.getBKINVENTORY_ID_FROM())
					&& pClassCreatorTrackFrom.equals(StaticDebug.getBKINVENTORY_CLASS_FROM())) {
				BKBar lBKBar = BKBarManager.getpMapRefToBKBar().get(StaticDebug.getDEBUG_BAR());
				String lMessageDebug = ">> "
						+ "Date= " + pDate 
						+ "; pMapBKBarToHolding.get('" + StaticDebug.getDEBUG_BAR() + "')= " + (lBKBar == null ? "'BKBar = null'" : pMapBKBarToHolding.get(lBKBar))
						+ "; pMapBKBarToTransactedToday.get('" + StaticDebug.getDEBUG_BAR() + "')= " + (lBKBar == null ? "'BKBar = null'" : pMapBKBarToTransactedToday.get(lBKBar))
						+ "; pIDTrackFrom= " + pIDTrackFrom
						+ "; pClassCreatorTrackFrom= " + pClassCreatorTrackFrom
						+ "; Object= " + this.hashCode()
						;
				if (lBKBar == null) {
					lMessageDebug += "BKBar= null";
				} else {
					lMessageDebug += pMapBKBarToHolding.get(lBKBar);
				}
				System.err.println(lMessageDebug);
			}
		}
		/*
		 * Compute a cash flow for the paper traded at a BKPrice; Cash flow= Quantity today * (Price today - BKPrice)
		 */
		double lValueUSDPaperToday = 0.;
		double lValueUSDPaperTodayWithPriceYstD = 0.;
		for (BKTransaction lBKTransaction : pListBKTransactionToday) {
			BKAsset lBKAsset = lBKTransaction.getpBKAsset();
			if (lBKAsset.getpIsPaper() 
					&& !Double.isNaN(lBKTransaction.getpBKPrice())) {
				double lPriceUSD = lBKAsset.getpPriceUSD(pDate);
				lValueUSDPaperToday += lBKTransaction.getpQuantity()
						* (lPriceUSD - lBKTransaction.getpBKPrice());
				/*
				 * Yesterday
				 */
				double lPriceUSDYstD = lBKAsset.getpPriceUSD(pDateYstD);				
				lValueUSDPaperTodayWithPriceYstD += lBKTransaction.getpQuantity()
						* (lPriceUSDYstD - lBKTransaction.getpBKPrice());
			}
		}
		/*
		 * Compute Value USD from normal BKAssets
		 */
		pValueUSD = 0.;
		pValueUSDWithPriceYstD = 0.;
		for (BKAsset lBKAsset : pMapBKAssetToQuantity.keySet()) {
			if (!lBKAsset.getpIsPaper()) {
				pValueUSD += getpValueUSD(lBKAsset, false);
				pValueUSDWithPriceYstD += getpValueUSD(lBKAsset, true);
			}
		}
		/*
		 * Compute Value USD from paper
		 */
		if (pBKInventoryPrevious == null) {
			pValueUSDPaper = 0.;
		} else {
			pValueUSDPaper = pBKInventoryPrevious.pValueUSDPaper;
		}
		pValueUSDPaper += lValueUSDPaperToday;
		for (BKAsset lBKAsset : pMapBKAssetToQuantity.keySet()) {
			if (lBKAsset.getpIsPaper()) {
				pValueUSDPaper += getpValueUSD(lBKAsset, false);
			}
		}
		pValueUSD += pValueUSDPaper;
		/*
		 * With prices yesterday
		 */
		if (pBKInventoryPrevious == null) {
			pValueUSDPaperWithPriceYstD = 0.;
		} else {
			pValueUSDPaperWithPriceYstD = pBKInventoryPrevious.pValueUSDPaperWithPriceYstD;
		}
		pValueUSDPaperWithPriceYstD += lValueUSDPaperTodayWithPriceYstD;
		for (BKAsset lBKAsset : pMapBKAssetToQuantity.keySet()) {
			if (lBKAsset.getpIsPaper()) {
				pValueUSDPaperWithPriceYstD += getpValueUSD(lBKAsset, true);
			}
		}
		pValueUSDWithPriceYstD += pValueUSDPaperWithPriceYstD;
		/*
		 * Compute the incoming cash for the clients = incoming cash + FOREX
		 */
		pIncomingFundsUSD = 0.;
		for (BKAsset lBKAsset : BKAssetManager.getpListCurrency()) {
			/*
			 * Quantity Previous
			 */
			Double lQtyPrevious = null;
			if (pBKInventoryPrevious != null) {
				lQtyPrevious = pBKInventoryPrevious.getpMapBKCurrencyToIncomingFunds().get(lBKAsset);
			}
			if (lQtyPrevious == null) {
				lQtyPrevious = 0.;
			}
			/*
			 * Quantity Current
			 */
			double lQtyCurrent = 0.;
			for (BKTransaction lBKTransaction : pListBKTransactionToday) {
				if (lBKTransaction.getpBKAsset().equals(lBKAsset)
						&& (lBKTransaction.getpBKIncome().equals("Operations_Incoming_funds_from_client")
								|| lBKTransaction.getpBKIncome().equals("Forex with clients"))) {
					lQtyCurrent += lBKTransaction.getpQuantity();
				}
			}
			/*
			 * Assign quantity
			 */
			double lQtyIncomingFunds = lQtyPrevious + lQtyCurrent;
			pMapBKCurrencyToIncomingFunds.put(lBKAsset, lQtyIncomingFunds);
			pIncomingFundsUSD += lQtyIncomingFunds * lBKAsset.getpPriceUSD(pDate);
		}
	}

	/**
	 * Compute the value USD for the BKAsset within this BKInventory
	 * @param _sBKAsset
	 * @return
	 */
	private double getpValueUSD(BKAsset _sBKAsset, boolean _sIsYstd) {
		int lDate = _sIsYstd ? pDateYstD : pDate;
		/*
		 * Case of a normal asset: ValueUSD = Quantity * Price
		 */
		if (!_sBKAsset.getpIsPaper()) {
			Double lQuantity = pMapBKAssetToQuantity.get(_sBKAsset);
			if (lQuantity == null) {
				return 0.;
			} else {
				double lPriceUSD = _sBKAsset.getpPriceUSD(lDate);
				return lQuantity * lPriceUSD;
			}
		}
		/*
		 * Case of a Paper: ValueUSD = QuantityYesterday * (PriceToday - PriceYesterday)
		 */
		else {
			if (pBKInventoryPrevious == null) {
				return 0.;
			} else {
				Double lQuantityPrevious = pBKInventoryPrevious.getpMapBKAssetToQuantity().get(_sBKAsset);
				if (lQuantityPrevious == null) {
					return 0.;
				} else {
					double lPriceUSD = _sBKAsset.getpPriceUSD(lDate);
					double lPriceUSDPrevious = _sBKAsset.getpPriceUSD(_sIsYstd ? pBKInventoryPrevious.getpDateYstD() : pBKInventoryPrevious.getpDate());
					return lQuantityPrevious * (lPriceUSD - lPriceUSDPrevious);
				}
			}
		}
	}

	/**
	 * Quantity of BKAsset for this day
	 * @param _sBKAsset
	 * @return
	 */
	public final double getpQuantity(BKAsset _sBKAsset) {
		if (pMapBKAssetToQuantity.containsKey(_sBKAsset)) {
			return pMapBKAssetToQuantity.get(_sBKAsset);
		} else {
			return 0.;
		}
	}

	/**
	 * Ascending order on the Date
	 */
	@Override public int compareTo(BKInventory _sBKInventory) {
		return Integer.compare(pDate, _sBKInventory.getpDate());
	}

	/**
	 * Classic toString
	 */
	public String toString() {
		return "pIDTrackFrom= " + pIDTrackFrom
				+ "; pDate= " + pDate;
	}

	/**
	 * Gives the number of holding of the BKBar at that time
	 * @param _sBKBar
	 * @return
	 */
	public final int getpBKBarHolding(BKBar _sBKBar) {
		int lHolding = 0;
		if (pMapBKBarToHolding.containsKey(_sBKBar)) {
			lHolding = pMapBKBarToHolding.get(_sBKBar);
		}
		return lHolding;
	}
	
	/**
	 * 
	 * @param _sBKBar
	 * @return
	 */
	public final int getpBKBarBoughtToday(BKBar _sBKBar) {
		int lQuantity = 0;
		if (pMapBKBarToBoughtToday.containsKey(_sBKBar)) {
			lQuantity = pMapBKBarToBoughtToday.get(_sBKBar);
		}
		return lQuantity;
	}
	
	/**
	 * 
	 * @param _sBKBar
	 * @return
	 */
	public final int getpBKBarSoldToday(BKBar _sBKBar) {
		int lQuantity = 0;
		if (pMapBKBarToSoldToday.containsKey(_sBKBar)) {
			lQuantity = pMapBKBarToSoldToday.get(_sBKBar);
		}
		return lQuantity;
	}

	/**
	 * @return 0 if the BKAsset has no holding
	 * @param _sBKAsset
	 * @return
	 */
	public final double getpBKAssetQty(BKAsset _sBKAsset) {
		if (!pMapBKAssetToQuantity.containsKey(_sBKAsset)) {
			return 0.;
		} else {
			return pMapBKAssetToQuantity.get(_sBKAsset);
		}
	}

	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final double getpValueUSD() {
		return pValueUSD;
	}
	public final Map<BKAsset, Double> getpMapBKAssetToQuantity() {
		return pMapBKAssetToQuantity;
	}
	public final List<BKTransaction> getpListBKTransactionToday() {
		return pListBKTransactionToday;
	}
	public final BKInventory getpBKInventoryPrevious() {
		return pBKInventoryPrevious;
	}
	protected final void setpBKInventoryPrevious(BKInventory pBKInventoryPrevious) {
		this.pBKInventoryPrevious = pBKInventoryPrevious;
	}
	public final String getpIDTrackFrom() {
		return pIDTrackFrom;
	}
	public final Map<BKBar, Integer> getpMapBKBarToHolding() {
		return pMapBKBarToHolding;
	}
	public final Map<BKAsset, Double> getpMapBKCurrencyToIncomingFunds() {
		return pMapBKCurrencyToIncomingFunds;
	}
	public final double getpIncomingFundsUSD() {
		return pIncomingFundsUSD;
	}
	public final int getpDateYstD() {
		return pDateYstD;
	}
	public final double getpValueUSDWithPriceYstD() {
		return pValueUSDWithPriceYstD;
	}
	public final Map<BKBar, Integer> getpMapBKBarToTransactedToday() {
		return pMapBKBarToTransactedToday;
	}
	public final Map<BKBar, Integer> getpMapBKBarToBoughtToday() {
		return pMapBKBarToBoughtToday;
	}
	public final Map<BKBar, Integer> getpMapBKBarToSoldToday() {
		return pMapBKBarToSoldToday;
	}


}

