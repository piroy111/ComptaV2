package step1loadtransactions.bars;

import conf.bkbartype.BKBarType;
import step0treatrawdata.objects.BKAsset;

public class BKBar {

	/**
	 * a BKBar is a designated bar which is in the vault 
	 * @param _sRef
	 */
	protected BKBar(String _sRef) {
		pRef = _sRef;
	}
	
	/*
	 * Data
	 */
	private String pRef;
	private BKAsset pBKAsset;
	private double pWeightOz;
	private BKBarType pBKBarType;
	private int pDateAcquisition;
	
	/**
	 * Classic toString
	 */
	public final String toString() {
		return pBKAsset.getpName()
				+ "; Ref= " + pRef
				+ "; Weight in oz= " + pWeightOz;
	}
	
	/**
	 * The minimum date among all the dates of the transactions
	 * @param pDateAcquisition
	 */
	protected final void setpDateAcquisition(int _sDateAcquisition) {
		if (pDateAcquisition == 0) {
			pDateAcquisition = _sDateAcquisition;
		} else {
			pDateAcquisition = Math.min(pDateAcquisition, _sDateAcquisition);
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpRef() {
		return pRef;
	}
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final double getpWeightOz() {
		return pWeightOz;
	}
	protected final void setpBKAsset(BKAsset pBKAsset) {
		this.pBKAsset = pBKAsset;
	}
	protected final void setpWeightOz(double pWeightOz) {
		this.pWeightOz = pWeightOz;
	}
	public final BKBarType getpBKBarType() {
		return pBKBarType;
	}
	protected final void setpBKBarType(BKBarType pBKBarType) {
		this.pBKBarType = pBKBarType;
	}
	public final int getpDateAcquisition() {
		return pDateAcquisition;
	}
	
}
