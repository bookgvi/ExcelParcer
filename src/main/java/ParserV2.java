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

/**
 * 1) Парсинг xls в HashMap<String, ArrayList<String>>
 * первая колонка во входящем xls-файле становится ключем хэшмап,
 * остальные добавляются в массив, который в свою очередь есть значеие этого хэшмапа
 * 2) Сериализация в GOSU hashmap к виду
 *              {"S25067700023153" -> {"12375","10001"}}
 * 3) сохранение полученого файл parse.gs
 * */
public class ParserV2 {
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
            ArrayList<String> values = new ArrayList<String>();
            while (cells.hasNext()) {
                Cell cell = cells.next();
                int cellNumber = cell.getColumnIndex();
                if (key == null && cellNumber == 0) {
                    key = cell.getStringCellValue();
                } else if (cellNumber > 0) {
                    values.add(cell.getStringCellValue());
                }
            }
            createMap(key, values);
        }
        saveToFile(serializeMap());
    }

    private static void createMap(String key, ArrayList<String> values) {
        ArrayList<String> res = resultMap.get(key);
        if (res == null) {
            ArrayList<String> newArray = new ArrayList<String>(values);
            resultMap.put(key, newArray);
        }
    }

    private static String serializeMap() {
        Gson gson = new Gson();
        return gson.toJson(ParserV2.resultMap)
                .replace(":", " -> ")
                .replace("[", "{")
                .replace("],", "},\n")
                .replace("]", "}\n");
    }

    private static void saveToFile(String str) {
        String fileName = "/Users/bookgvi/Documents/GW_RGS/parse.gs";
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(str.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
