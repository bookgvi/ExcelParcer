import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Parser {
    private static final Map<String, ArrayList<String>> resultMap = new HashMap<>();

    public static void parse(String fileName) {
        InputStream inputStream = null;
        XSSFWorkbook workBook = null;
        try {
            inputStream = new FileInputStream(fileName);
            workBook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        XSSFSheet sheet = workBook.getSheetAt(0);
        Iterator<Row> it = sheet.iterator();
        while (it.hasNext()) {
            Row row = it.next();
            Iterator<Cell> cells = row.iterator();
            String key = null;
            String val = null;
            while (cells.hasNext()) {
                Cell cell = cells.next();
                if (key == null) {
                    key = cell.getStringCellValue();
                } else {
                    val = cell.getStringCellValue();
                }
                if (key != null && !key.equals("")) {
                    createMap(key, val);
                }
            }
        }

        saveToFile(serializeMap());
    }

    private static void createMap(String key, String val) {
        ArrayList<String> res = resultMap.get(key);
        if (res == null) {
            ArrayList<String> newArray = new ArrayList<>();
//            newArray.add(val);
            resultMap.put(key, newArray);
        } else if (val != null) {
            res.add(val);
        }
    }

    private static String serializeMap() {
        Gson gson = new Gson();
        return gson.toJson(Parser.resultMap)
                .replace(":", "->")
                .replace("[", "{")
                .replace("]", "}");
    }

    private static void saveToFile(String str) {
        String fileName = "/Users/bookgvi/Documents/GW_RGS/bso-parse.txt";
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(str.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
