package step0treatrawdata.brokers.oanda;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicPrintMsg;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step0treatrawdata.brokers.oanda.reconciliation.OAReconcilationManager;
import step0treatrawdata.brokers.oanda.report.OAReportManager;
import step0treatrawdata.brokers.oanda.writefiles.OAWriteFilePositions;

public class OAManager {

	public static void main(String[] _sArgs) {
		new OAManager().run();
	}
	
	public OAManager() {
		instantiate();
	}
	
	
	/*
	 * Data
	 */
	private List<OAReportManager> pListOAReportManager;
	private OAWriteFilePositions pOAWriteFilePositions;
	private OAReconcilationManager pOAReconcilationManager;
	
	/**
	 * 
	 */
	private void instantiate() {
		pListOAReportManager = new ArrayList<>();
		pListOAReportManager.add(new OAReportManager(StaticDir.getIMPORT_BROKERS_OANDA(),	StaticDir.getTREATED_BROKERS_OANDA(), StaticNames.getBROKERS_OANDA()));
		pListOAReportManager.add(new OAReportManager(StaticDir.getIMPORT_BROKERS_OANDA_CRYPTO(), StaticDir.getTREATED_BROKERS_OANDA_CRYPTO(), StaticNames.getBROKERS_OANDA_CRYPTO()));
		pOAWriteFilePositions = new OAWriteFilePositions(this);
		pOAReconcilationManager = new OAReconcilationManager(this);
	}
	
	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Read new reports of OANDA and create new BKTransactions' file");
		/*
		 * Read files report and create transactions
		 */
		for (OAReportManager lOAReportManager : pListOAReportManager) {
			lOAReportManager.run();
		}
		/*
		 * Write positions
		 */
		pOAWriteFilePositions.storePositions();
		pOAWriteFilePositions.writeFile();
		/*
		 * Reconciliation
		 */
		pOAReconcilationManager.run();
	}

	/*
	 * Getters & Setters
	 */
	public final List<OAReportManager> getpListOAReportManager() {
		return pListOAReportManager;
	}
	
	
}
