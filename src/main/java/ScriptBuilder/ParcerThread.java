package ScriptBuilder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class ParcerThread implements Callable<ScriptBuilder.Builder> {
    private final String _inFile;
    private final CountDownLatch _mainCDL;
    private final ScriptBuilder scriptBuilder = new ScriptBuilder();

    public ParcerThread(CountDownLatch mainCDL, String inFile) {
        _mainCDL = mainCDL;
        _inFile = inFile;
    }
    @Override
    public ScriptBuilder.Builder call() throws Exception {
            InputStream inputStream = null;
            XSSFWorkbook workBook = null;
            ArrayList<String> values = new ArrayList<>();
            try {
                inputStream = new FileInputStream(_inFile);
                workBook = new XSSFWorkbook(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            XSSFSheet sheet = workBook.getSheetAt(0);
            Iterator<Row> it = sheet.iterator();
            int rowCounts = 0;
            while (it.hasNext()) {
                rowCounts++;
                Row row = it.next();
                Iterator<Cell> cells = row.iterator();
                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    values.add(cell.getStringCellValue());
                }
                scriptBuilder.createMap(values);
            }
            System.out.printf("%d, %s \n", rowCounts, Thread.currentThread().getName());
            _mainCDL.countDown();
            return scriptBuilder.newBuilder();
    }

}
