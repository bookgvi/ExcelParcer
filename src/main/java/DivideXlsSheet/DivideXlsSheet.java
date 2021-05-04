package DivideXlsSheet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DivideXlsSheet {
    private String _fileName;
    private XSSFWorkbook _xssfWorkbook;
    private XSSFSheet _xssfSheet;
    private final ArrayList<List<Row>> groupsOfRows = new ArrayList<>();

    private DivideXlsSheet() {
    }

    static Builder createBuilder() {
        return new DivideXlsSheet().new Builder();
    }


    public class Builder {
        private Builder() {
        }

        public Builder loadFile(String fileName) {
            _fileName = fileName;
            try {
                _xssfWorkbook = new XSSFWorkbook(new FileInputStream(_fileName));
                _xssfSheet = _xssfWorkbook.getSheetAt(0);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return this;
        }

        public Builder splitWorkSheet() {
            int defaultRecordsCount = 900;
            return splitWorkSheet(defaultRecordsCount);
        }

        public Builder splitWorkSheet(int recordsPerSheet) {
            ArrayList<Row> rowsArrayList = com.google.common.collect.Lists.newArrayList(_xssfSheet.rowIterator());
            for (List<Row> rowsChunk : com.google.common.collect.Iterables.partition(rowsArrayList, recordsPerSheet)) {
                groupsOfRows.add(rowsChunk);
            }
            return this;
        }

        public Builder saveGroupsOfCellIntoSheets() {
            for (List<Row> rowsChunk : groupsOfRows) {
                XSSFSheet newSheet = _xssfWorkbook.createSheet();
                Iterator<Row> rowIterator = rowsChunk.iterator();
                int rowIndex = 0;
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    XSSFRow newRow = newSheet.createRow(rowIndex);
                    rowIndex++;
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        XSSFCell newCell = newRow.createCell(cell.getColumnIndex());
                        newCell.setCellValue(cell.getStringCellValue());
                    }
                }
            }
            return this;
        }

        public void saveFile() {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(_fileName);
                _xssfWorkbook.write(fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
