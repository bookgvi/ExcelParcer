package ScriptBuilderV2;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class LoadXlsInArrayList {

    public ArrayList<Data> execute(String xlsFileName) {
        XSSFWorkbook xlsWorkBook = null;
        try {
            InputStream fileStream = new FileInputStream(xlsFileName);
            xlsWorkBook = new XSSFWorkbook(fileStream);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
        if (xlsWorkBook != null) {
            return putInQueue(xlsWorkBook);
        }
        return null;
    }

    private ArrayList<Data> putInQueue(XSSFWorkbook xlsWorkBook) {
        ArrayList<Data> arrayListWithData = new ArrayList<>();
        XSSFSheet xlsWorkSheet = xlsWorkBook.getSheetAt(0);
        int rowCounter = 0;
        Iterator<Row> rowIterator = xlsWorkSheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            rowCounter = row.getRowNum();

            Data data = new Data();
            Iterator<Cell> cellIterator = row.iterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getColumnIndex()) {
                    case 0:
                        data._first = cell.getStringCellValue().trim();
                        break;
                    case 1:
                        data._second = cell.getStringCellValue().trim();
                        break;
                    case 2:
                        data._third = cell.getStringCellValue().trim();
                        break;
                }
            }
            arrayListWithData.add(data);
        }
        System.out.printf("Rows %d\n", rowCounter);
        return arrayListWithData;
    }

}
