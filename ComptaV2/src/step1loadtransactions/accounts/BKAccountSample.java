package step1loadtransactions.accounts;

public class BKAccountSample {

	public static void main(String[] _sArgs) {
		/*
		 * Load file
		 */
		BKAccountManager.getpMapNameToBKAccount();
		/*
		 * System.out
		 */
		for (BKAccount lBKAccount : BKAccountManager.getpMapNameToBKAccount().values()) {
			System.out.println(lBKAccount.toString());
		}
	}
	
}
