package ParcerWithSplit;

import Constants.FilesAndDirectories;

import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        String fileName = FilesAndDirectories.inputFileDirectory +  "PCG-55820_april_v2.xlsx";
        XLSUtils xlsObj = new XLSUtils(fileName);
        ArrayList<ArrayList<String>> data = xlsObj.parce(3);
//        ArrayList<ArrayList<String>> flatData = new SerializeUtils(data).splitToArraylistV1();
        ArrayList<ArrayList<String>> flatData2 = new SerializeUtils(data).splitToArraylistV2();
//        xlsObj.createNewSheet(flatData);
        xlsObj.createNewSheet(flatData2);
    }
}
