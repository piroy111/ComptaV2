package step1loadtransactions.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import staticdata.StaticDate;
import step1loadtransactions.transactions.BKTransaction;

public class BKInventoryManager {

	
	/**
	 * a BKInventory(Date) contains the aggregate of the BKTransactions prior 
	 * and equal to the date of the BKInventory<br>
	 * Creates a continuous Map(Date, BKInventory)<br>
	 * All the dates are in the map from the smallest of the dates of BKTransactions until Today<br>
	 * Compute the data of the BKInventory: ValueUSD, Map(BKAsset, Quantity)<br>
	 * @param _sListBKTransaction : List of BKTransaction which will make the BKInventory
	 * @param _sIDTRackFrom : optional, allows to track who has requested for the BKInventory
	 */
	public static final TreeMap<Integer, BKInventory> getpAndComputeMapBKInventory(List<BKTransaction> _sListBKTransaction,
			String _sIDTRackFrom, Class<?> _sClassGeneratorTrackFrom) {
		List<BKTransaction> lListBKTransaction = new ArrayList<BKTransaction>(_sListBKTransaction);
		int lDateStop = StaticDate.getDATE_MAX() == -1 ? BasicDateInt.getmToday() : StaticDate.getDATE_MAX();
		/*
		 * Find Date Start and Stop
		 */
		int lDateStart = lDateStop;
		for (BKTransaction lBKTransaction : lListBKTransaction) {
			lDateStart = Math.min(lDateStart, lBKTransaction.getpDate());
		}
		lDateStart = BasicDateInt.getmPlusBusinessDays(lDateStart, -1);
		/*
		 * Build list of BKInventory
		 */
		TreeMap<Integer, BKInventory> lMapDateToBKInventory = new TreeMap<Integer, BKInventory>();
		BKInventory lBKInventoryPrevious = null;
		for (int lDate = lDateStart; lDate <= BasicDateInt.getmToday(); lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
			BKInventory lBKInventory = getpOrCreateBKInventory(lDate, lMapDateToBKInventory, _sIDTRackFrom, _sClassGeneratorTrackFrom.getSimpleName());
			lBKInventory.setpBKInventoryPrevious(lBKInventoryPrevious);
			lBKInventoryPrevious = lBKInventory;
		}
		/*
		 * Put the BKTransaction in the proper BKInventory
		 */
		for (BKTransaction lBKTransaction : lListBKTransaction) {
			int lDate = lBKTransaction.getpDate();
			BKInventory lBKInventory = getpOrCreateBKInventory(lDate, lMapDateToBKInventory, _sIDTRackFrom, _sClassGeneratorTrackFrom.getSimpleName());
			if (lBKTransaction.getpDate() <= lDateStop) {
				lBKInventory.declareNewBKTransaction(lBKTransaction);
			}
		}
		List<BKInventory> lListBKInventory = new ArrayList<BKInventory>(lMapDateToBKInventory.values());
		/*
		 * Compute data of BKInventory
		 */
		Collections.sort(lListBKInventory);
		for (int lIdx = 0; lIdx < lListBKInventory.size(); lIdx++) {
			BKInventory lBKInventory = lListBKInventory.get(lIdx);
			lBKInventory.compute();
		}
		/*
		 * Return Map
		 */
		return lMapDateToBKInventory;
	}

	/**
	 * classic get or create
	 * @param _sDate
	 * @return
	 */
	public static final BKInventory getpOrCreateBKInventory(int _sDate, 
			Map<Integer, BKInventory> _sMapDateToBKInventory, String _sIDTRackFrom, String _sClassGeneratorTrackFrom) {
		BKInventory lBKInventory = _sMapDateToBKInventory.get(_sDate);
		if (lBKInventory == null) {
			lBKInventory = new BKInventory(_sDate, _sIDTRackFrom, _sClassGeneratorTrackFrom);
			_sMapDateToBKInventory.put(_sDate, lBKInventory);
		}
		return lBKInventory;
	}

	/**
	 * 
	 * @param _sBKInventory
	 * @param _sBKTransaction
	 */
	public static final void declareNewBKTransaction(BKInventory _sBKInventory, BKTransaction _sBKTransaction) {
		_sBKInventory.declareNewBKTransaction(_sBKTransaction);
	}
	
	/*
	 * Getters & Setters
	 */

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
