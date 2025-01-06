package conf.bkstorage;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;

public class BKStorageSample {

	public static void main(String[] _aArgs) {
		print("", "SILVER", BasicDateInt.getmToday());
		print("", "PLATINUM", 20160101);
		print("arnaud.droitcourt@centraliens.net", "SILVER", 20150101);
		print("arnaud.droitcourt@centraliens.net", "SILVER", 20170101);
		print("arnaud.droitcourt@centraliens.net", "SILVER", BasicDateInt.getmToday());
	}
	
	
	/**
	 * Proper display
	 * @param _sAccount
	 * @param _sMetal
	 * @param _sDateTarget
	 */
	private static void print(String _sAccount, String _sMetal, int _sDateTarget) {
		System.out.println(
				"Account= " + _sAccount
				+ "; Metal= " + _sMetal
				+ "; DateTarget= " + _sDateTarget
				+ " --> cost of storage= "
				+ BasicPrintMsg.afficheBps(BKStorageManager.getmStorageCost(_sAccount, _sMetal, _sDateTarget), 0));
	}
	
}
