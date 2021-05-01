package ParcerWithSplit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class XLSUtils {
    private String _fileName;
    private XSSFWorkbook _xssfWorkbook;
    private XSSFSheet _xssFSheet;

    public XLSUtils(String fileName) {
        _fileName = fileName;
        try {
            InputStream fileInputStream = new FileInputStream(fileName);
            _xssfWorkbook = new XSSFWorkbook(fileInputStream);
            _xssFSheet = _xssfWorkbook.getSheetAt(0);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public ArrayList<ArrayList<String>> parce(int columnsCount) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        Iterator<Row> rowIterator = _xssFSheet.rowIterator();
        while (rowIterator.hasNext()) {
            ArrayList<String> rowList = new ArrayList<>();
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                rowList.add(cell.getStringCellValue().trim());
            }
            result.add(rowList);
        }
        return result;
    }

    public void saveToXls(ArrayList<ArrayList<String>> data) {
        XSSFSheet newSheet = _xssfWorkbook.getSheet("Modified");
        newSheet = newSheet == null ? _xssfWorkbook.createSheet("Modified") : newSheet;
        List<Row> rowList = IntStream.range(0, data.size()).mapToObj(newSheet::createRow).collect(Collectors.toList());
        Iterator<Row> newRowIterator = rowList.iterator();
        int rowIndex = 0;
        while (newRowIterator.hasNext()) {
            int cellIndex = 0;
            Row row = newRowIterator.next();
            List<Cell> cellList = IntStream.range(0, 3).mapToObj(row::createCell).collect(Collectors.toList());
            Iterator<Cell> cellListIterator = cellList.iterator();
            while (cellListIterator.hasNext()) {
                Cell cell = cellListIterator.next();
                cell.setCellValue(data.get(rowIndex).get(cellIndex));
                cellIndex++;
            }
            rowIndex++;
        }
        saveToFile(newSheet);
    }

    protected void saveToFile(XSSFSheet xssfSheet) {
        try {
            OutputStream fileOutputStream = new FileOutputStream(_fileName);
            _xssfWorkbook.write(fileOutputStream);
        } catch (IOException ignored) {

        }
    }
}
