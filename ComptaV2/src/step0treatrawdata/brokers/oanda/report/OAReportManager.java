package step0treatrawdata.brokers.oanda.report;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDir;
import basicmethods.BasicFile;
import step0treatrawdata.brokers.oanda.writefiles.OAWriteFileManager;

public class OAReportManager {

	public OAReportManager(String _sDirImport, String _sDirTreated, String _sSuffixNameFile) {
		pDirImport = _sDirImport;
		pDirTreated = _sDirTreated;
		pSuffixNameFile = _sSuffixNameFile;
	}

	/*
	 * Data
	 */
	private String pDirImport;
	private String pDirTreated;
	private String pSuffixNameFile;
	private List<OAReport> pListOAReport;
	private List<String> pListIDAlreadyDoneOldVersion;
	private List<String> pListIDAlreadyDoneNewVersion;
	private OAWriteFileManager pOAWriteFileManager;
	private OAMigrationManager pOAMigrationManager;

	/**
	 * loadImportFilesAndCreatOAReports
	 */
	public final void run() {
		/*
		 * Initiate
		 */
		pListOAReport = new ArrayList<OAReport>();
		pListIDAlreadyDoneOldVersion = new ArrayList<String>();
		pListIDAlreadyDoneNewVersion = new ArrayList<String>();
		pOAMigrationManager = new OAMigrationManager(this);
		/*
		 * Load reports
		 */
		BasicDir lBasicDir = new BasicDir(pDirImport, pSuffixNameFile);
		for (BasicFile lBasicFile : lBasicDir.getmMapDateToBasicFile().values()) {
			OAReport lOAReport = new OAReport(lBasicFile.getmLitUnFichierEnLignes(), this);
			pListOAReport.add(lOAReport);
		}
		/*
		 * Check migration
		 */
		pOAMigrationManager.check();
		/*
		 * Write file
		 */
		pOAWriteFileManager = new OAWriteFileManager();
		pOAWriteFileManager.run(pListOAReport, pDirTreated, pSuffixNameFile.substring(1));
	}

	/**
	 * we use this in order to not to duplicate transactions in two reports
	 * @param _sID
	 */
	protected final boolean checkAndStoreNewID(String _sID, boolean _sIsNewVersion) {
		List<String> lListDone = _sIsNewVersion ? pListIDAlreadyDoneNewVersion : pListIDAlreadyDoneOldVersion;
		if (lListDone.contains(_sID)) {
			return false;
		} else {
			lListDone.add(_sID);
			return true;
		}
	}

	/*
	 * Getters & Setters
	 */
	public final List<OAReport> getpListOAReport() {
		return pListOAReport;
	}
	public final OAMigrationManager getpOAMigrationManager() {
		return pOAMigrationManager;
	}
	public final String getpDirImport() {
		return pDirImport;
	}






















}
