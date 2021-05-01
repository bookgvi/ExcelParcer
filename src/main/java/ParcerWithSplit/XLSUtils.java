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
    private int _columnsCount = 3;

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
        _columnsCount = columnsCount;
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        Iterator<Row> rowIterator = _xssFSheet.rowIterator();
        while (rowIterator.hasNext()) {
            ArrayList<String> rowList = new ArrayList<>();
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();
            int cellNumber = 1;
            while (cellIterator.hasNext() && cellNumber < columnsCount) {
                Cell cell = cellIterator.next();
                cellNumber = cell.getColumnIndex() + 1;
                rowList.add(cell.getStringCellValue().trim());
            }
            result.add(rowList);
        }
        return result;
    }

    public void createNewSheet(ArrayList<ArrayList<String>> data) {
        XSSFSheet newSheet = _xssfWorkbook.createSheet();
        List<Row> rowList = IntStream.range(0, data.size()).mapToObj(newSheet::createRow).collect(Collectors.toList());
        Iterator<Row> newRowIterator = rowList.iterator();
        int rowIndex = 0;
        while (newRowIterator.hasNext()) {
            int cellIndex = 0;
            Row row = newRowIterator.next();
            rowIndex = row.getRowNum();
            List<Cell> cellList = IntStream.range(0, _columnsCount).mapToObj(row::createCell).collect(Collectors.toList());
            Iterator<Cell> cellListIterator = cellList.iterator();
            while (cellListIterator.hasNext()) {
                Cell cell = cellListIterator.next();
                cellIndex = cell.getColumnIndex();
                cell.setCellValue(data.get(rowIndex).get(cellIndex));
            }
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
