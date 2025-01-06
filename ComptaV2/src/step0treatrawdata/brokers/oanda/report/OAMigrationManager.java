package step0treatrawdata.brokers.oanda.report;

import java.util.ArrayList;
import java.util.List;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;

public class OAMigrationManager {

	protected OAMigrationManager(OAReportManager _sOAReportManager) {
		pOAReportManager = _sOAReportManager;
		/*
		 * 
		 */
		pListOATransactionMigration = new ArrayList<>();
	}
	
	/*
	 * Data
	 */
	private OAReportManager pOAReportManager;
	private List<OATransaction> pListOATransactionMigration;
	private static boolean DEACTIVATE = true;
	
	/**
	 * 
	 * @param _sOATransaction
	 */
	public final void declareNewOATransactionMigration(OATransaction _sOATransaction) {
		pListOATransactionMigration.add(_sOATransaction);
	}

	/**
	 * Check the migration deals match previous deals. If this is not the case, it means that we traded between 2 reports (one with no migration, and one with migration)
	 */
	protected final void check() {
		if (DEACTIVATE) {
			return;
		}
		List<OATransaction> lListOATransactionMatched = new ArrayList<>();
		for (OATransaction lOATransactionMigration : pListOATransactionMigration) {
			/*
			 * Set the date of the report that should contain the similar transaction
			 */
			int lDateReportMax = lOATransactionMigration.getpDate();
			boolean lIsFound = false;
			/*
			 * 
			 */
			for (OAReport lOAReport : pOAReportManager.getpListOAReport()) {
				if (lOAReport.getpDate() < lDateReportMax) {
					for (OATransaction lOATransaction : lOAReport.getpListOATransaction()) {
						if (AMNumberTools.isEqual(lOATransaction.getpQuantity(), lOATransactionMigration.getpQuantity())
								&& AMNumberTools.isEqual(lOATransaction.getpPrice(), lOATransactionMigration.getpPrice())
								&& lOATransaction.getpDate() < lOATransactionMigration.getpDate()) {
							lListOATransactionMatched.add(lOATransactionMigration);
							lIsFound = true;
							break;
						}
					}
					if (lIsFound) {
						break;
					}
				}
			}
		}
		/*
		 * Check whether there are transactions missing
		 */
		List<OATransaction> lListOATransactionMissing = new ArrayList<>(pListOATransactionMigration);
		lListOATransactionMissing.removeAll(lListOATransactionMatched);
		if (lListOATransactionMissing.size() > 0) {
			String lMsg = "There are some transaction of the migration which I cannot find in the previous reports";
			for (OATransaction lOATransactionMigration : lListOATransactionMissing) {
				lMsg += "\nTransaction= " + lOATransactionMigration;
			}
			BasicPrintMsg.error(lMsg);
		}
		BasicPrintMsg.display(this, "Migration deals all reconciled -> Ok");
	}
	
}
