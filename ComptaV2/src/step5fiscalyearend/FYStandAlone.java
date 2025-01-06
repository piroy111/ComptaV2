package step5fiscalyearend;

import step2loans.createloanstransactions.LNManager;
import uob.UOBMainManager;
import uob.staticdata.UOBStatic.UOB_DISPLAY;

public class FYStandAlone {

	public static void main(String[] _sArgs) {
		LNManager lLNManager = new LNManager();
		lLNManager.run(true);
		new FYMain(lLNManager, new UOBMainManager(UOB_DISPLAY.Off)).run();
	}
	
}
