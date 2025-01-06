package step1loadtransactions.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.inventory.BKInventoryManager;
import step1loadtransactions.transactions.BKTransaction;

public class BKHolder {

	/**
	 * a BKHolder is a list of BKTransaction and a Map of Date to BKInventory<br>
	 * <b>pListBKTransaction</b>: the List of BKTransaction can be anything and is built through the method 'declareNewBKTransaction'<br>
	 * <b>pMapDateToBKInventory</b>: the Map of Date to BKInventory is then deduced from the list of BKTransactions<br>
	 * @param _sKey : allows to create BKHolders within the BKHolderGenerator. The key is a function of each BKTransaction, 
	 * and all BKTransactions are grouped into a BKHolder when they have the same Key. Therefore the Key function is set by the user
	 */
	protected BKHolder(String _sKey, BKHolderGenerator _sBKHolderGenerator) {
		pKey = _sKey;
		pBKHolderGenerator = _sBKHolderGenerator;
		/*
		 * Initiate
		 */
		pListBKTransaction = new ArrayList<BKTransaction>();
	}

	/*
	 * Data
	 */
	private String pKey;
	private BKHolderGenerator pBKHolderGenerator;
	private List<BKTransaction> pListBKTransaction;
	private TreeMap<Integer, BKInventory> pMapDateToBKInventory;
	private List<Integer> pListDate;

	/**
	 * Add a new BKTransaction to the list
	 * @param _sBKTransaction
	 */
	protected final void declareNewBKTransaction(BKTransaction _sBKTransaction) {
		pListBKTransaction.add(_sBKTransaction);
	}

	/**
	 * Build the Map of BKInventory
	 */
	protected final void computeBKInventory() {
		pMapDateToBKInventory = BKInventoryManager.getpAndComputeMapBKInventory(pListBKTransaction, 
				pKey, pBKHolderGenerator.getClass());
		pListDate = new ArrayList<Integer>(pMapDateToBKInventory.keySet());
		Collections.sort(pListDate);
		Collections.sort(pListBKTransaction);
	}

	/**
	 * Add a new BKTransaction to the list and recompute all the coming BKInventory
	 * @param _sBKTransaction
	 */
	protected final void declareNewBKTransactionAndRecomputeBKInventory(List<BKTransaction> _sListBKTransaction) {
		/*
		 * Filter with key of the BKHolder
		 */
		Integer lDateMin = null;
		List<BKTransaction> lListBKTransaction = new ArrayList<BKTransaction>();
		for (BKTransaction lBKTransaction : _sListBKTransaction) {
			if (pKey.equals(pBKHolderGenerator.getpUniqueKey(lBKTransaction))) {
				lListBKTransaction.add(lBKTransaction);
				if (lDateMin == null || lDateMin > lBKTransaction.getpDate()) {
					lDateMin = lBKTransaction.getpDate();
				}
			}
		}
		if (lListBKTransaction.size() == 0) {
			return;
		}
		/*
		 * Add to the list
		 */
		pListBKTransaction.addAll(lListBKTransaction);
		Collections.sort(pListBKTransaction);
		/*
		 * Case the BKTransaction is prior to the smallest date
		 */
		if (pListDate.size() == 0 || lDateMin < pListDate.get(0)) {
			computeBKInventory();
		}
		/*
		 * Otherwise we recompute only the BKInventory after the date of the BKTransaction 
		 */
		else {
			/*
			 * Add to the list of the BKInventory
			 */
			for (BKTransaction lBKTransaction : lListBKTransaction) {
				BKInventory lBKInventory = pMapDateToBKInventory.get(lBKTransaction.getpDate());
				BKInventoryManager.declareNewBKTransaction(lBKInventory, lBKTransaction);
			}
			/*
			 * Re-compute the BKInventory
			 */
			int lDateStop = pListDate.get(pListDate.size() - 1);
			for (int lDate = lDateMin; lDate <= lDateStop; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
				BKInventory lBKInventory = pMapDateToBKInventory.get(lDate);
				lBKInventory.compute();
			}
		}
	}

	/**
	 * Classic toString
	 */
	public final String toString() {
		return pKey;
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpKey() {
		return pKey;
	}
	public final List<BKTransaction> getpListBKTransaction() {
		return pListBKTransaction;
	}
	public final TreeMap<Integer, BKInventory> getpMapDateToBKInventory() {
		return pMapDateToBKInventory;
	}
	public final List<Integer> getpListDate() {
		return pListDate;
	}
















}
