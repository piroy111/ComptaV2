package step5fiscalyearend.incomestatement;

class FYData implements Comparable<FYData> {

	protected FYData(String _sName, double _sValueUSD, String _sCategory) {
		pName = _sName;
		pValueUSD = _sValueUSD;
		pCategory = _sCategory;
	}
	
	/*
	 * Data
	 */
	private String pName;
	private double pValueUSD;
	private String pCategory;
	
	@Override public int compareTo(FYData _sFYData) {
		if (_sFYData.getpCategory().equals(pCategory)) {
			return pName.compareTo(_sFYData.getpName());
		} else {
			return pCategory.compareTo(_sFYData.getpCategory());
		}
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
	public final String getpCategory() {
		return pCategory;
	}
	
	
}
