package step0treatrawdata.brokers.interactivebrokers.reports;

import java.util.ArrayList;
import java.util.List;

import step0treatrawdata.brokers.interactivebrokers.IBManager;
import step0treatrawdata.brokers.interactivebrokers.files.IBFile;

public class IBReportManager {

	public IBReportManager(IBManager _sIBManager) {
		pIBManager = _sIBManager;
	}
	
	/*
	 * Data
	 */
	private IBManager pIBManager;
	private List<IBReport> pListIBReportNew;
	private IBReport pIBReportPrevious;
	
	/**
	 * 
	 */
	public final void run() {
		createListIBReportNew();
		loadFileNewIBReports();
	}
	
	/**
	 * 
	 */
	private void createListIBReportNew() {
		/*
		 * Initiate
		 */
		pListIBReportNew = new ArrayList<IBReport>();
		List<IBFile> lListIBFileNew = pIBManager.getpIBFileManager().getpListIBFileNew();
		IBFile lIBFileCurrent = null;
		IBFile lIBFilePrevious = pIBManager.getpIBFileManager().getpIBFileLastReport();
		pIBReportPrevious = createIBReport(lIBFilePrevious);
		/*
		 * Instantiate the IBReports and read file
		 */
		for (int lIdx = 0; lIdx < lListIBFileNew.size(); lIdx++) {
			lIBFileCurrent = lListIBFileNew.get(lIdx);
			/*
			 * Store the reports
			 */
			IBReport lIBReport = createIBReport(lIBFileCurrent);
			pListIBReportNew.add(lIBReport);
		}
	}

	/**
	 * 
	 */
	private void loadFileNewIBReports() {
		for (IBReport lIBReport : pListIBReportNew) {
			lIBReport.loadFile();
		}
		if (pIBReportPrevious != null) {
			pIBReportPrevious.loadFile();
		}
	}
	
	/**
	 * 
	 * @param _sIBFileReport
	 * @return
	 */
	public final IBReport createIBReport(IBFile _sIBFileReport) {
		return new IBReport(this, _sIBFileReport);
	}
	
	/*
	 * Getters & Setters
	 */
	public final IBManager getpIBManager() {
		return pIBManager;
	}
	public final List<IBReport> getpListIBReportNew() {
		return pListIBReportNew;
	}
	public final IBReport getpIBReportPrevious() {
		return pIBReportPrevious;
	}
	
	
}
