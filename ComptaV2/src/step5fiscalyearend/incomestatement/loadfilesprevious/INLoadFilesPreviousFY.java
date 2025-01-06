package step5fiscalyearend.incomestatement.loadfilesprevious;

import java.util.HashMap;
import java.util.Map;

import basicmethods.BasicDir;
import basicmethods.BasicFile;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step5fiscalyearend.FYMain;

public class INLoadFilesPreviousFY {

	public INLoadFilesPreviousFY(FYMain _sFYMain) {
		pFYMain = _sFYMain;
		/*
		 * 
		 */
		pMapDateToINFileFY = new HashMap<>();
	}
	
	/*
	 * Data
	 */
	private FYMain pFYMain;
	private Map<Integer, INFileFY> pMapDateToINFileFY;
	
	/**
	 * 
	 */
	public final void run() {
		String lDir = StaticDir.getOUTPUT_FY_COMPUTATION_VALIDATED();
		BasicDir lBasicDir = new BasicDir(lDir, StaticNames.getOUTPUT_FY_INCOME_STATEMENT_DELTA());
		for (BasicFile lBasicFile : lBasicDir.getmMapDateToBasicFile().values()) {
			INFileFY lINFileFY = getpOrCreateINFileFY(lBasicFile.getmDate());
			lINFileFY.fillData(lBasicFile.getmLitUnFichierEnLignes());
		}
	}
	
	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final INFileFY getpOrCreateINFileFY(int _sDate) {
		INFileFY lINFileFY = pMapDateToINFileFY.get(_sDate);
		if (lINFileFY == null) {
			lINFileFY = new INFileFY(_sDate, this);
			pMapDateToINFileFY.put(_sDate, lINFileFY);
		}
		return lINFileFY;
	}

	/*
	 * Getters & Setters
	 */
	public final FYMain getpFYMain() {
		return pFYMain;
	}
	public final Map<Integer, INFileFY> getpMapDateToINFileFY() {
		return pMapDateToINFileFY;
	}
	
	
	
}
