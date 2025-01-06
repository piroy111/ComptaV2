package step0treatrawdata.bktransactionquick;

import java.util.List;
import java.util.Map;

import basicmethods.BasicString;
import staticdata.StaticColumns.ColumnName;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;

public class BKTransactionQuick {

	/**
	 * Build a quick BKTransaction from a line of the file. It contains only the amount, BKAsset, 
	 * @param _sLineStr
	 * @param _sMapHeaderToIdxColumn
	 */
	public BKTransactionQuick(List<String> _sLineStr, 
			Map<String, Integer> _sMapHeaderToIdxColumn) {
		/*
		 * Read the line to fill the data
		 */
		pDate = BasicString.getInt(_sLineStr.get(_sMapHeaderToIdxColumn.get(ColumnName.Date.toString())));
		pComment = _sLineStr.get(_sMapHeaderToIdxColumn.get(ColumnName.Comment.toString()));
		pQuantity = BasicString.getDouble(_sLineStr.get(_sMapHeaderToIdxColumn.get(ColumnName.Amount.toString())));
		pBKAsset = BKAssetManager.getpAndCheckBKAsset(_sLineStr.get(_sMapHeaderToIdxColumn.get(ColumnName.BKAsset.toString())));
	}

	/*
	 * Data
	 */
	private int pDate;
	private BKAsset pBKAsset;
	private String pComment;
	private double pQuantity;
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final String getpComment() {
		return pComment;
	}
	public final double getpQuantity() {
		return pQuantity;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
