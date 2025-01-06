package step5fiscalyearend.balancesheet;

public class FYAsset implements Comparable<FYAsset> {

	protected FYAsset(String _sName) {
		pName = _sName;
	}
	
	/*
	 * Data
	 */
	private String pName;
	private double pValueUSD;
	
	/**
	 * 
	 * @param _sValueUSD
	 */
	protected final void addValue(double _sValueUSD) {
		if (Double.isNaN(pValueUSD)) {
			pValueUSD = 0.;
		}
		pValueUSD += _sValueUSD;
	}

	@Override public int compareTo(FYAsset _sFYAsset) {
		return pName.compareTo(_sFYAsset.pName);
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpName() {
		return pName;
	}
	public final double getpValueUSD() {
		return pValueUSD;
	}

	
	
}
