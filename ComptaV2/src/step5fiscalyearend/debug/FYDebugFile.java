package step5fiscalyearend.debug;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiers;
import staticdata.StaticDir;

public class FYDebugFile {

	protected FYDebugFile(String _sName) {
		pName = _sName;
		/*
		 * Initiate
		 */
		pListLineToWrite = new ArrayList<String>();
		pHeader = null;
	}
	
	/*
	 * Data
	 */
	private String pName;
	private List<String> pListLineToWrite;
	private String pHeader;
	
	/**
	 * Write the file in the debug folder
	 */
	protected final void writeFile() {
		BasicFichiers.writeFile(StaticDir.getOUTPUT_FY_DEBUG(), pName, pHeader, pListLineToWrite);
	}

	/*
	 * Getters & Setters
	 */
	public final String getpHeader() {
		return pHeader;
	}
	public final void setpHeader(String pHeader) {
		this.pHeader = pHeader;
	}
	public final void addNewLine(String _sLine) {
		pListLineToWrite.add(_sLine);
	}

	public final List<String> getpListLineToWrite() {
		return pListLineToWrite;
	}
	
}
