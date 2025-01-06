package staticdata;

@Deprecated
public class StaticSubAccount {

	private static String LOAN = "OperationalProfit_Loan";
	private static String LOAN_COST = "OperationalProfit_Loan_cost";
	private static String REFINER_IN = "Refiner_In";
	private static String CURRENCY_REFERENCE = "USD";
	private static String BUNKER = "contact@bunker-group.com";

	/*
	 * Getters & Setters
	 */
	public static final String getLOAN() {
		return LOAN;
	}
	public static final String getREFINER_IN() {
		return REFINER_IN;
	}
	public static final String getLOAN_COST() {
		return LOAN_COST;
	}
	public static final String getCURRENCY_REFERENCE() {
		return CURRENCY_REFERENCE;
	}
	public static final String getBUNKER() {
		return BUNKER;
	}
	
}
