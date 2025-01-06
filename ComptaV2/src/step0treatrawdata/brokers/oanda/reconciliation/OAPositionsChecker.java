package step0treatrawdata.brokers.oanda.reconciliation;

import java.util.Map;

import basicmethods.BasicPrintMsg;
import staticdata.StaticDir;
import step0treatrawdata.brokers.oanda.report.OAReport;
import step0treatrawdata.brokers.oanda.report.OAReportManager;
import step0treatrawdata.brokers.oanda.report.OATransaction;
import step0treatrawdata.objects.BKAsset;

public class OAPositionsChecker {

	protected OAPositionsChecker(OAReconcilationManager _sOAReconcilationManager) {
		pOAReconcilationManager = _sOAReconcilationManager;
	}

	/*
	 * Data
	 */
	private OAReconcilationManager pOAReconcilationManager;
	private static int DATE_MIGRATION = 20181110;

	/**
	 * 
	 */
	public final void initiate() {

	}

	/**
	 * 
	 */
	public final void run() {
		/*
		 * Communication
		 */
		BasicPrintMsg.display(this, null);
		/*
		 * Initiate
		 */
		Map<String, Double> lMapSymbolToPosition = pOAReconcilationManager.getpOAFileReader().getpMapSymbolToPosition();
		String lMsgError = "";
		/*
		 * Loop on symbols
		 */
		for (String lSymbol : lMapSymbolToPosition.keySet()) {
			/*
			 * Load
			 */
			BKAsset lBKAsset = OAReport.getBKAsset(lSymbol);
			double lPosition = lMapSymbolToPosition.get(lSymbol);
			double lPositionFromTransaction = 0.;
			double lPositionPreMigration = 0.;
			double lPositionPostMigration = 0.;
			/*
			 * Compute position from OATransactions
			 */
			for (OAReportManager lOAReportManager : pOAReconcilationManager.getpOAManager().getpListOAReportManager()) {
				if (lOAReportManager.getpDirImport().equals(StaticDir.getIMPORT_BROKERS_OANDA())) {
					for (OAReport lOAReport : lOAReportManager.getpListOAReport()) {
						for (OATransaction lOATransaction : lOAReport.getpListOATransaction()) {
							if (lOATransaction.getpBKAsset().equals(lBKAsset)) {
								lPositionFromTransaction += lOATransaction.getpQuantityOANDA();
								if (lOATransaction.getpDate() <= DATE_MIGRATION) {
									lPositionPreMigration += lOATransaction.getpQuantityOANDA();
								} else {
									lPositionPostMigration += lOATransaction.getpQuantityOANDA();
								}
							}
						}
					}
				}
			}
			/*
			 * Reconcile
			 */
			if (Math.abs(lPosition - lPositionFromTransaction) > 1) {
				lMsgError += "\nSymbol= " + lSymbol
						+ "; Position OANDA= " + BasicPrintMsg.afficheIntegerWithComma(lPosition)
						+ "; Position transactions= " + BasicPrintMsg.afficheIntegerWithComma(lPositionFromTransaction)
						+ "; Position transactions pre migration= " + BasicPrintMsg.afficheIntegerWithComma(lPositionPreMigration)
						+ "; Postion transactions post migration= " + BasicPrintMsg.afficheIntegerWithComma(lPositionPostMigration);
			} else {
				BasicPrintMsg.display(this, "Position reconciliation Ok for " + lSymbol
						+ "; Position= " + BasicPrintMsg.afficheIntegerWithComma(lPositionFromTransaction));
			}
		}
		/*
		 * Communications
		 */
		if (lMsgError.equals("")) {
			BasicPrintMsg.display(this, "Reconciliation of positions OANDA --> All good !");
		} else {
			BasicPrintMsg.error("There is an error: when we sum the positions of the transactions, we dont have the positions of OANDA"
					+ lMsgError);
		}
	}

	/*
	 * Getters & Setters
	 */
	public final OAReconcilationManager getpOAReconcilationManager() {
		return pOAReconcilationManager;
	}

}
