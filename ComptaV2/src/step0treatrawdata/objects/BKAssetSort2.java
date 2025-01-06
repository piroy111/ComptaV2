package step0treatrawdata.objects;

import java.util.Comparator;

public class BKAssetSort2 implements Comparator<BKAsset> {

	/**
	 * Sort the BKAsset by type and then by alphabetical order
	 */
	@Override public int compare(BKAsset _sBKAsset1, BKAsset _sBKAsset2) {
		int lCompareType1 = getpType(_sBKAsset1);
		int lCompareType2 = getpType(_sBKAsset2);
		if (lCompareType1 != lCompareType2) {
			return Integer.compare(lCompareType1, lCompareType2);
		} else {
			return _sBKAsset1.getpName().compareTo(_sBKAsset2.getpName());
		}
	}

	
	private int getpType(BKAsset _sBKAsset) {
		if (_sBKAsset.getpIsCurrency()) {
			return 0;
		} else if (_sBKAsset.getpIsPaper() && _sBKAsset.getpName().length() == "XSGD".length()) {
			return 1;
		} else if (_sBKAsset.getpIsBar()) {
			return 2;
		} else if (_sBKAsset.getpIsPaper()) {
			return 3;
		} else if (_sBKAsset.getpIsBarLoan()) {
			return 4;
		} else {
			return 5;
		}
	}
	
	
}
