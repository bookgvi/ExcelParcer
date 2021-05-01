package ParcerWithSplit;

import Constants.FilesAndDirectories;

import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        String fileName = FilesAndDirectories.inputFileDirectory +  "PCG-55820_april_v2.xlsx";
        XLSUtils xlsObj = new XLSUtils(fileName);
        ArrayList<ArrayList<String>> data = xlsObj.parce(3);
        ArrayList<ArrayList<String>> flatData = new SerializeUtils(data).flatArraylist();
        xlsObj.createNewSheet(flatData);
    }
}
