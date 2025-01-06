package conf.bkbartype;

import basicmethods.BasicPrintMsg;

public class BKBarType implements Comparable<BKBarType> {

	protected BKBarType(String _sName, double _sWeightOz, double _sWeightGram, String _sNaturalUnit, String _sMetal) {
		mName = _sName;
		mWeightOz = _sWeightOz;
		mWeightGram = _sWeightGram;
		mNaturalUnit = _sNaturalUnit;
		mMetal = _sMetal;
	}
	
	/*
	 * Data
	 */
	private String mName;
	private double mWeightOz;
	private double mWeightGram;
	private String mNaturalUnit;
	private String mMetal;
	
	/**
	 * ToString classic
	 */
	public final String toString() {
		if (mNaturalUnit.equals("Oz")) {
			return mName + "; w= " + BasicPrintMsg.afficheIntegerWithComma(mWeightOz) + " Oz";
		} else {
			return mName + "; w= " + BasicPrintMsg.afficheIntegerWithComma(mWeightGram) + " g";
		}
	}
	
	/**
	 * Sort metal first, then weight
	 */
	@Override public int compareTo(BKBarType _sBKBarType) {
		int lCompareMetal = mMetal.compareTo(_sBKBarType.getmMetal());
		if (lCompareMetal != 0) {
			return lCompareMetal;
		} else {
			return Double.compare(mWeightOz, _sBKBarType.getmWeightOz());
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getmName() {
		return mName;
	}
	public final double getmWeightOz() {
		return mWeightOz;
	}
	public final double getmWeightGram() {
		return mWeightGram;
	}
	public final String getmNaturalUnit() {
		return mNaturalUnit;
	}
	public final String getmMetal() {
		return mMetal;
	}

	
	
}
