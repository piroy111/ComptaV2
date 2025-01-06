package staticdata;

import ledgerendofmonth.staticdata.LDGStatic;

public class StaticDate {

	private static int DATE_FY_BEGIN = 20160331;
	private static int DATE_START_STORAGE_CHARGE = 20170731; // this is because we started counting the storage from this date only
	private static int DATE_MAX = -1;
	private static int DATE_FILE_UOB_SORT_DESCENDING = 20180401;
	
	/*
	 * Getters & setters
	 */
	public static final int getDATE_FY_BEGIN() {
		return DATE_FY_BEGIN;
	}
	public static final int getDATE_START_STORAGE_CHARGE() {
		return DATE_START_STORAGE_CHARGE;
	}
	public static final void setDATE_MAX(int dATE_MAX) {
		DATE_MAX = dATE_MAX;
		LDGStatic.setDATE_LIMIT(DATE_MAX);
	}
	public static final int getDATE_MAX() {
		return DATE_MAX;
	}
	public static final int getDATE_FILE_UOB_SORT_DESCENDING() {
		return DATE_FILE_UOB_SORT_DESCENDING;
	}
	
}
