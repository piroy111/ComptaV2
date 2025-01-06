package step5fiscalyearend.debug;

import java.util.HashMap;
import java.util.Map;

import basicmethods.BasicFichiers;
import staticdata.StaticDir;

public class FYDebugFileManager {

	public FYDebugFileManager() {
		pMapNameToFYDebugFile = new HashMap<String, FYDebugFile>();
		BasicFichiers.getOrCreateDirectory(StaticDir.getOUTPUT_FY_DEBUG());
	}
	
	/*
	 * Data
	 */
	private Map<String, FYDebugFile> pMapNameToFYDebugFile;
	
	/**
	 * Classic get or create
	 * @param _sName
	 * @return
	 */
	public final FYDebugFile getpOrCreateFYFile(String _sName) {
		FYDebugFile lFYDebugFile = pMapNameToFYDebugFile.get(_sName);
		if (lFYDebugFile == null) {
			lFYDebugFile = new FYDebugFile(_sName);
			pMapNameToFYDebugFile.put(_sName, lFYDebugFile);
		}
		return lFYDebugFile;
	}
	
	/**
	 * Write files
	 */
	public final void writeDebugFiles() {
		for (FYDebugFile lFYDebugFile : pMapNameToFYDebugFile.values()) {
			lFYDebugFile.writeFile();
		}
	}
	
}
