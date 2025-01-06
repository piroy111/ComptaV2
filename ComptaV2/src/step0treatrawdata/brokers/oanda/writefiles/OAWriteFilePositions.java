package step0treatrawdata.brokers.oanda.writefiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.StaticDir;
import step0treatrawdata.brokers.oanda.OAManager;
import step0treatrawdata.brokers.oanda.report.OAReport;
import step0treatrawdata.brokers.oanda.report.OAReportManager;
import step0treatrawdata.brokers.oanda.report.OATransaction;

public class OAWriteFilePositions {

	public OAWriteFilePositions(OAManager _sOAManager) {
		pOAManager = _sOAManager;
		/*
		 * 
		 */
		pMapAssetToPosition = new HashMap<>();
	}

	/*
	 * Data
	 */
	private OAManager pOAManager;
	private Map<String, Double> pMapAssetToPosition;

	/**
	 * Store positions; Put the positions in a map per asset
	 */
	public final void storePositions() {
		for (OAReportManager lOAReportManager : pOAManager.getpListOAReportManager()) {
			for (OAReport lOAReport : lOAReportManager.getpListOAReport()) {
				for (OATransaction lOATransaction : lOAReport.getpListOATransaction()) {
					/*
					 * Load
					 */
					String lAsset = lOATransaction.getpBKAsset().getpName();
					double lPlusPosition = lOATransaction.getpQuantity();
					/*
					 * Add and Store
					 */
					addPosition(lAsset, lPlusPosition);
				}
			}
		}
	}

	/**
	 * 
	 */
	public final void writeFile() {
		/*
		 * Prepare file
		 */
		List<String> lListAsset = new ArrayList<>(pMapAssetToPosition.keySet());
		Collections.sort(lListAsset);
		List<String> lListLineToWrite = new ArrayList<>();
		for (String lAsset : lListAsset) {
			String lLine = lAsset + "," + pMapAssetToPosition.get(lAsset);
			lListLineToWrite.add(lLine);
		}
		/*
		 * Write the file
		 */
		String lDir = StaticDir.getOUTPUT_OANDA_REAL_TIME_POSITIONS();
		String lNameFile = BasicDateInt.getmToday() + "_OANDA_Real_time_positions.csv";
		String lHeader = "Asset,Position";
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
		BasicPrintMsg.display(this, "File written: " + lNameFile);
	}

	/**
	 * 
	 * @param _sAsset
	 * @return
	 */
	protected final double getpPosition(String _sAsset) {
		Double lPosition = pMapAssetToPosition.get(_sAsset);
		if (lPosition == null) {
			lPosition = 0.;
			pMapAssetToPosition.put(_sAsset, lPosition);
		}
		return lPosition;
	}

	/**
	 * 
	 * @param _sAsset
	 * @param _sPlusPosition
	 */
	protected final void addPosition(String _sAsset, double _sPlusPosition) {
		double lPosition = getpPosition(_sAsset) + _sPlusPosition;
		pMapAssetToPosition.put(_sAsset, lPosition);

	}

}
