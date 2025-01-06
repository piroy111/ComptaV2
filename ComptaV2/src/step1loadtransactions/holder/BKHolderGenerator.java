package step1loadtransactions.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import staticdata.StaticDate;
import step1loadtransactions.transactions.BKTransaction;
import step1loadtransactions.transactions.BKTransactionManager;

public abstract class BKHolderGenerator {

	/**
	 * Create a Map of BKHolders which contain the BKTransactions
	 * @param _sBKTransactionManager
	 */
	public BKHolderGenerator(BKTransactionManager _sBKTransactionManager) {
		pBKTransactionManager = _sBKTransactionManager;
	}
	
	/*
	 * Abstract
	 */
	public abstract boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction);
	/**
	 * @return allows to create BKHolders within the BKHolderGenerator. The key is a function of each BKTransaction, 
	 * and all BKTransactions are grouped into a BKHolder when they have the same Key. Therefore the Key function is set by the user
	 * @param _sBKTransaction
	 */
	public abstract String getpUniqueKey(BKTransaction _sBKTransaction);
	/*
	 * Data
	 */
	protected BKTransactionManager pBKTransactionManager;
	protected Map<String, BKHolder> pMapKeyToBKHolder;
	protected List<BKHolder> pListBKHolder;
	protected List<Integer> pListDate;
	
	/**
	 * create the Map of BKHolder
	 */
	public final void generateBKHolder() {
		/*
		 * Initiate
		 */
		pMapKeyToBKHolder = new HashMap<String, BKHolder>();
		pListBKHolder = new ArrayList<BKHolder>();
		pListDate = new ArrayList<Integer>();
		/*
		 * Assign the BKTransaction to the BKHolders
		 */
		for (BKTransaction lBKTransaction : pBKTransactionManager.getpListBKTransaction()) {
			if (getpIsKeepBKTransaction(lBKTransaction)) {
				String lKey = getpUniqueKey(lBKTransaction);
				BKHolder lBKHolder = getpOrCreateBKHolder(lKey);
				lBKHolder.declareNewBKTransaction(lBKTransaction);
			}
		}
		/*
		 * Compute BKHolders
		 */
		for (BKHolder lBKHolder : pMapKeyToBKHolder.values()) {
			lBKHolder.computeBKInventory();
		}
		/*
		 * Add list of dates
		 */
		for (BKHolder lBKHolder : pMapKeyToBKHolder.values()) {
			for (int lDate : lBKHolder.getpListDate()) {
				if (!pListDate.contains(lDate)) {
					pListDate.add(lDate);
				}
			}
		}
		Collections.sort(pListDate);
	}
	
	/**
	 * Classic get or create
	 * @param _sKey
	 * @return
	 */
	public final BKHolder getpOrCreateBKHolder(String _sKey) {
		BKHolder lBKHolder = pMapKeyToBKHolder.get(_sKey);
		if (lBKHolder == null) {
			lBKHolder = new BKHolder(_sKey, this);
			pMapKeyToBKHolder.put(_sKey, lBKHolder);
			pListBKHolder.add(lBKHolder);
		}
		return lBKHolder;
	}

	/**
	 * Add a new BKTransaction to the list and to all coming BKInventories<br>
	 * recompute all coming BKInventories<br>
	 * @param _sBKTransaction
	 */
	public final void declareNewBKTransactionAndRecomputeBKInventory(List<BKTransaction> _sListBKTransaction) {
		/*
		 * Build List BKTransaction to keep
		 */
		List<BKTransaction> lListBKTransaction = new ArrayList<BKTransaction>();
		for (BKTransaction lBKTransaction : _sListBKTransaction) {
			if (!getpIsKeepBKTransaction(lBKTransaction)) {
				continue;
			}
			if (StaticDate.getDATE_MAX() == -1 || lBKTransaction.getpDate() <= StaticDate.getDATE_MAX()) {
				lListBKTransaction.add(lBKTransaction);
			}
		}
		/*
		 * Add to the list of BKtransactions of each BKHolder and recompute all the BKInventories
		 */
		for (BKHolder lBKHolder : pListBKHolder) {
			lBKHolder.declareNewBKTransactionAndRecomputeBKInventory(_sListBKTransaction);
		}
	}

	
	/*
	 * Getters & Setters
	 */
	public final Map<String, BKHolder> getpMapKeyToBKHolder() {
		return pMapKeyToBKHolder;
	}
	public final List<BKHolder> getpListBKHolder() {
		return pListBKHolder;
	}
	public final List<Integer> getpListDate() {
		return pListDate;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
