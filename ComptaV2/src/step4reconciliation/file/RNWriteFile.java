package step4reconciliation.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import staticdata.StaticDir;

public class RNWriteFile {

	public RNWriteFile(String _sName) {
		mName = _sName;
		/*
		 * Initiate
		 */
		mListRNColumnInFileToWrite = new ArrayList<RNColumnInFile>();
		mMapTitleToRNColumnInFile = new HashMap<String, RNColumnInFile>();
	}
	
	/*
	 * Data
	 */
	private String mName;
	private List<RNColumnInFile> mListRNColumnInFileToWrite;
	private Map<String, RNColumnInFile> mMapTitleToRNColumnInFile;
	
	/*
	 * Classic get or create RNColumnInFile
	 * @param _sTitle
	 */
	public final RNColumnInFile getmOrCreateRNColumnInFile(String _sTitle) {
		RNColumnInFile lRNColumnInFile = mMapTitleToRNColumnInFile.get(_sTitle);
		if (lRNColumnInFile == null) {
			lRNColumnInFile = new RNColumnInFile(_sTitle);
			mMapTitleToRNColumnInFile.put(_sTitle, lRNColumnInFile);
			mListRNColumnInFileToWrite.add(lRNColumnInFile);
		}
		return lRNColumnInFile;
	}
	
	
	/**
	 * Write the file
	 */
	public final void writeFile() {
		/*
		 * Harmonize the dates
		 */
		int lDateMin = -1;
		int lDateMax = -1;
		for (RNColumnInFile lRNColumnInFile : mListRNColumnInFileToWrite) {
			for (int lDate : lRNColumnInFile.getmDateToRNValue().keySet()) {
				if (lDateMin == -1 || lDateMin > lDate) {
					lDateMin = lDate;
				}
				if (lDateMax == -1 || lDateMax < lDate) {
					lDateMax = lDate;
				}
			}
		}
		List<Integer> lListDate = BasicDateInt.getmListDays(lDateMin, lDateMax);
		/*
		 * Build the header
		 */
		String lHeader = "Date";
		for (RNColumnInFile lRNColumnInFile : mListRNColumnInFileToWrite) {
			lHeader += "," + lRNColumnInFile.getmTitle();
		}
		/*
		 * Build the list of last values to ensure the continuity when a date is lacking  
		 */
		List<Double> lListLastValue = new ArrayList<>();
		for (int lIdx = 0; lIdx < mListRNColumnInFileToWrite.size(); lIdx++) {
			lListLastValue.add(0.);
		}
		/*
		 * Build the lines of the file
		 */
		List<String> lListLineToWrite = new ArrayList<String>();
		for (int lDate : lListDate) {
			String lLine = "" + lDate;
			for (int lIdx = 0; lIdx < mListRNColumnInFileToWrite.size(); lIdx++) {
				RNColumnInFile lRNColumnInFile = mListRNColumnInFileToWrite.get(lIdx);
				RNValue lRNValue = lRNColumnInFile.getmDateToRNValue().get(lDate);
				if (lRNValue == null) {
					lLine += "," + lListLastValue.get(lIdx);
				} else {
					lLine += "," + lRNValue.getmValue();
					lListLastValue.set(lIdx, lRNValue.getmValue());
				}
			}
			lListLineToWrite.add(lLine);
		}
		/*
		 * Write the file
		 */
		String lPath = StaticDir.getOUTPUT_RECONCILIATION();
		BasicFichiers.getOrCreateDirectory(lPath);
		System.out.println("Write file output reconciliation: " + lPath + mName);
		BasicFichiers.writeFile(lPath, mName, lHeader, lListLineToWrite);
	}













}
