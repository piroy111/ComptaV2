package step0treatrawdata.bktransactionquick;

import step0treatrawdata.objects.BKAsset;

public class BKHoldingQuick {

	public BKHoldingQuick(BKAsset _sBKAsset) {
		pBKAsset = _sBKAsset;
	}
	
	/*
	 * Data
	 */
	private BKAsset pBKAsset;
	private String pComment;
	private double pQuantity;
	
	/*
	 * Getters & Setters
	 */
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final String getpComment() {
		return pComment;
	}
	public final double getpQuantity() {
		return pQuantity;
	}
	public final void setpBKAsset(BKAsset pBKAsset) {
		this.pBKAsset = pBKAsset;
	}
	public final void setpComment(String pComment) {
		this.pComment = pComment;
	}
	public final void setpQuantity(double pQuantity) {
		this.pQuantity = pQuantity;
	}

	
}
