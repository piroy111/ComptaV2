package step1loadtransactions.brokers.interactivebrokers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import step0treatrawdata.brokers.interactivebrokers.files.IBFile;
import step0treatrawdata.brokers.interactivebrokers.reports.IBReport;
import step0treatrawdata.brokers.interactivebrokers.reports.IBReportManager;

public class BKIBLoadIBFiles {

	protected BKIBLoadIBFiles(BKIBManager _sBKIBManager) {
		pBKIBManager = _sBKIBManager;
	}
	
	/*
	 * Data
	 */
	private BKIBManager pBKIBManager;
	private List<IBReport> pListIBReportContinuous;
	private IBReport pIBReportLast;
	private int pDateStopPreviousReport;
	
	/**
	 * Build a list of IBReport which are continuous<br> 
	 * (i.e. the last date of the previous report is one day before the start date of the next one)
	 */
	public final void loadIBReports() {
		BasicPrintMsg.display(this, null);
		BasicPrintMsg.display(this, "Keep continuous IBFiles");
		/*
		 * Initiate
		 */
		List<IBFile> lListIBFile = pBKIBManager.getpIBManager().getpIBFileManager().getpListIBFile();
		if (lListIBFile == null || lListIBFile.size() == 0) {
			BasicPrintMsg.error("No IBFile");
		}
		IBReportManager lIBReportManager = pBKIBManager.getpIBManager().getpIBReportManager();
		/*
		 * 
		 */
		Collections.sort(lListIBFile);
		pListIBReportContinuous = new ArrayList<>();
		IBFile lIBFilePrevious = lListIBFile.get(0);
		/*
		 * Loop of the IBFile to check the continuity
		 */
		for (int lIdx = 1; lIdx < lListIBFile.size(); lIdx++) {
			/*
			 * Load
			 */
			IBFile lIBFile = lListIBFile.get(lIdx);
			int lDateStartPrevious = lIBFilePrevious.getpDateStart();
			int lDateStopPrevious = lIBFilePrevious.getpDateStop();
			int lDateStart = lIBFile.getpDateStart();
			int lDateStop = lIBFile.getpDateStop();
			/*
			 * Check continuity
			 */
			boolean lIsOverlap = lDateStart == lDateStartPrevious && lDateStopPrevious < lDateStop;
			boolean lIsNewAndContinuous = lDateStopPrevious <= lDateStart 
					&& (BasicDateInt.getmNumberBusinessDays(lDateStopPrevious, lDateStart) - 2 <= 1);
			if (!lIsOverlap && ! lIsNewAndContinuous) {
				BasicPrintMsg.error("Something is wrong with the dates of the 2 files. They don't follow the continuity rule"
						+ "\nlIsOverlap= " + lIsOverlap
						+ "\nlIsNewAndContinuous= " + lIsNewAndContinuous
						+ "\nIBFilePrevious= " + lIBFilePrevious.getpPath().getFileName().toString()
						+ "\nIBFile= " + lIBFile.getpPath().getFileName().toString());
			}
			/*
			 * Add to list if new
			 */
			if (lIsNewAndContinuous) {
				IBReport lIBReport = lIBReportManager.createIBReport(lIBFilePrevious);
				pListIBReportContinuous.add(lIBReport);
			}
			/*
			 * Set the new file as the reference
			 */
			lIBFilePrevious = lIBFile;
		}
		/*
		 * Find the last report
		 */
		IBFile lIBFileLast = lListIBFile.get(lListIBFile.size() - 1);
		pIBReportLast = lIBReportManager.createIBReport(lIBFileLast);
		pListIBReportContinuous.add(pIBReportLast);
		/*
		 * Find the end date of the previous report
		 */
		if (lListIBFile.size() - 2 >= 0) {
			pDateStopPreviousReport = lListIBFile.get(lListIBFile.size() - 2).getpDateStop();
		} else {
			pDateStopPreviousReport = pIBReportLast.getpDateStart();
		}

		/*
		 * Load the files into the report
		 */
		for (IBReport lIBReport : pListIBReportContinuous) {
			lIBReport.loadFile();
			BasicPrintMsg.display(this, "Kept: " + lIBReport.getpIBFile().getpReadFile().getmNomFichier());
		}
	}

	/*
	 * Getters & Setters
	 */
	public final List<IBReport> getpListIBReportContinuous() {
		return pListIBReportContinuous;
	}
	public final IBReport getpIBReportLast() {
		return pIBReportLast;
	}
	public int getpDateStopPreviousReport() {
		return pDateStopPreviousReport;
	}
	
	
	
}
