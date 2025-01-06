package step8all;

import basicmethods.BasicDateInt;
import staticdata.StaticDate;
import step3statements.statements.main.STMain;
import step4reconciliation.main.RNMain;
import step4reconciliation.sources.RNBrokers;

public class LaunchCompta {

	public static void main(String[] args) {
		/*
		 * Set date to end of the previous month 
		 */
		StaticDate.setDATE_MAX(BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(BasicDateInt.getmToday(), -1)));
		/////////////////////////////////////////////////////	20200831
		StaticDate.setDATE_MAX(20200831);		
		/////////////////////////////////////////////////////
		/*
		 * Build the statements
		 */
		new STMain().run();
		new RNMain().run();
		new RNBrokers().run();
	}

}
