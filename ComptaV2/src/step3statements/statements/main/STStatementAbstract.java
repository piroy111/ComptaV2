package step3statements.statements.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;

public abstract class STStatementAbstract {

	public STStatementAbstract(STStatementGeneratorAbstract _sSTStatementGeneratorAbstract) {
		pSTStatementGeneratorAbstract = _sSTStatementGeneratorAbstract;
	}
	
	/*
	 * Abstract
	 */
	public abstract String getpDirName();
	public abstract void createLines();
	/*
	 * Data
	 */
	protected STStatementGeneratorAbstract pSTStatementGeneratorAbstract;
	private String pNameFile;
	private String pHeader;
	private List<String> pListLineToWrite;
	private boolean pIsFileWrittenAlready;

	/**
	 * 
	 */
	public final void initiate() {
		BasicFichiers.getOrCreateDirectory(getpDirName());
		pNameFile = BasicDateInt.getmToday() + "_" 
				+ this.getClass().getSimpleName().substring("STWriteFile".length())	+ ".csv";
		pListLineToWrite = new ArrayList<>();
		pHeader = "";
		pIsFileWrittenAlready = false;
	}
	
	/**
	 * 
	 * @param _sHeader
	 */
	public final void addToHeader(String _sHeader) {
		if (!pHeader.equals(""))  {
			pHeader += ",";
		}
		pHeader += _sHeader;
	}
	
	/**
	 * 
	 * @param _sNewLine
	 */
	public final void addToListLine(String _sNewLine) {
		pListLineToWrite.add(_sNewLine);
	}

	/**
	 * 
	 */
	public final void writeFile() {
		BasicFichiers.writeFile(getpDirName(), pNameFile, pHeader, pListLineToWrite);
		BasicPrintMsg.display(this, "File written= '" + getpDirName() + pNameFile + "'");
		pIsFileWrittenAlready = true;
	}

	/**
	 * 
	 */
	public final void sortListOfLineToWrite() {
		Collections.sort(pListLineToWrite);
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final STStatementGeneratorAbstract getpSTStatementGeneratorAbstract() {
		return pSTStatementGeneratorAbstract;
	}
	public final String getpNameFile() {
		return pNameFile;
	}
	public final String getpHeader() {
		return pHeader;
	}
	public final List<String> getpListLineToWrite() {
		return pListLineToWrite;
	}
	public final void setpNameFile(String pNameFile) {
		this.pNameFile = pNameFile;
	}
	public final boolean getpIsFileWrittenAlready() {
		return pIsFileWrittenAlready;
	}
	
}
