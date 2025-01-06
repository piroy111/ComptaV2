package conf.bkbartype;

public class BKBarTypeSample {

	public static void main(String[] _sArgs) {
		for (BKBarType lBKBarType : BKBarTypeManager.getLIST_BKBARTYPE()) {
			System.out.println(lBKBarType);
		}
	}
	
}
