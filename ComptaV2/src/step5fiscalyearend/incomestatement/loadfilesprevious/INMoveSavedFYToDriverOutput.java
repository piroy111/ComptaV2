package step5fiscalyearend.incomestatement.loadfilesprevious;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import basicmethods.BasicFichiersNio;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicTime;
import staticdata.StaticDir;
import staticdata.StaticNames;

public class INMoveSavedFYToDriverOutput {

	public static void main(String[] _sArgs) {
		run();
	}
	
	public final static void run() {
		String lDir = StaticDir.getFY_VALIDATED();
		List<Path> lListSubDir = BasicFichiersNioRaw.getListPath(Paths.get(lDir));
		for (Path lSubPath : lListSubDir) {
			String lSubSubDir = lSubPath.toString() + "/" + "05 - FY - computation validated and passed/";
			Path lSubSubPath = Paths.get(lSubSubDir);
			if (BasicFichiersNioRaw.getIsAlreadyExist(lSubSubPath)) {
				List<Path> lListPathToMove = BasicFichiersNioRaw.getListPath(lSubSubPath);
				for (Path lPathToMove : lListPathToMove) {
					String lNameFile = lPathToMove.getFileName().toString();
					if (lNameFile.endsWith(StaticNames.getOUTPUT_FY_INCOME_STATEMENT_DELTA())
							|| lNameFile.endsWith(StaticNames.getOUTPUT_FY_BALANCE_SHEET())) {
						BasicFichiersNio.copyFiles(lPathToMove, StaticDir.getOUTPUT_FY_COMPUTATION_VALIDATED(), true, true);
					}
				}
			}
		}
		BasicTime.sleep(50);
	}
	
	
}
