package step2loans.createloanstransactions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicFichiersNio;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.BasicTime;
import staticdata.StaticDir;

public class LNDeleteFilesCurrentMonth {

	public static void main(String[] _sArgs) {
		LNDeleteFilesCurrentMonth.run();
	}
	
	
	public static final void run() {
		int lDateLimit = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(BasicDateInt.getmToday(), -1));
		String lDir = StaticDir.getTREATED_LOANS();
		List<Path> lListPathFiles = BasicFichiersNio.getListFilesAndSubFiles(Paths.get(lDir));
		for (Path lPath : lListPathFiles) {
			String lNameFile = lPath.getFileName().toString();
			int lDate = BasicString.getInt(lNameFile.substring(0, 8));
			if (lDate > lDateLimit) {
				BasicPrintMsg.display(new LNDeleteFilesCurrentMonth(), 
						"     Delete file '" + lNameFile + "' because it is older than the last month of compta (" + lDateLimit + ")");
				BasicFichiers.deleteFile(lDir + lNameFile);
				BasicTime.sleep(500);
			}
		}
	}
	
}
