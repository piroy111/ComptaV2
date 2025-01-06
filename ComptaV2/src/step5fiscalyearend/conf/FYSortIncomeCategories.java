package step5fiscalyearend.conf;

import java.util.Comparator;

public class FYSortIncomeCategories implements Comparator<String> {


	@Override
	public int compare(String _sCategory1, String _sCategory2) {
		int lRank1 = rank(_sCategory1);
		int lRank2 = rank(_sCategory2);
		return Integer.compare(lRank1, lRank2);
	}
	
	/**
	 * Rank
	 * @param _sCategory
	 * @return
	 */
	private int rank(String _sCategory) {
		if (_sCategory.equals("Revenue")) {
			return 0;
		} else if (_sCategory.equals("Cost of goods sold")) {
			return 1;
		} else if (_sCategory.equals("Expenses")) {
			return 2;
		} else {
			return 3;
		}
	}
	
}
