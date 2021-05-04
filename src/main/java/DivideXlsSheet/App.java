package DivideXlsSheet;

import Constants.FilesAndDirectories;

public class App {
    public static void main(String[] args) {
        String fileName = FilesAndDirectories.baseDir + "PCG-47999/" + "PCG-49777_null.xlsx";
        DivideXlsSheet.createBuilder()
                .loadFile(fileName)
                .splitWorkSheet()
                .saveGroupsOfCellIntoSheets()
                .saveFile();
    }
}
