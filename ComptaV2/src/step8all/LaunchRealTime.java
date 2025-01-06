package step8all;

import step6RealTimeExposure.EXManager;

public class LaunchRealTime {

	/**
	 * Before running you should put the following reports in the appropriate folder:
	 * 		- OANDA
	 * 		- Interactive Brokers
	 */
	public static void main(String[] args) {
		new EXManager().run();
	}
	
}
