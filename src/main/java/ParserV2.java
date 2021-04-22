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
 * {"S25067700023153" -> {"12375","10001"}}
 * 3) сохранение полученого файл parse.gs
 */
public class ParserV2 {
    private static final ArrayList<Data> resultMap = new ArrayList<>();

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
        int count = 0;
        while (it.hasNext()) {
            count++;
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
        System.out.println(count);
//        System.out.println(resultMap);
        System.out.println(serializeMap());
    }

    private static void createMap(String key, ArrayList<String> values) {
        Data data = new Data(key.trim(), values.get(0).trim(), values.get(1).trim());
        resultMap.add(data);
    }

    private static String serializeMap() {
        Gson gson = new Gson();
        return gson.toJson(ParserV2.resultMap)
                .replace("[", "")
                .replace("{\"prc\":", "new Data(")
                .replace(",\"pcs\":", ", ")
                .replace("\"ep\":", " ")
                .replace("},", "),\n")
                .replace("}", ")")
//                .replace("]", "\n}");
//                .replace("[", "new Data(")
//                .replace("],", "),\n")
                .replace("]", "");
    }

    private static void saveToFile(String str) {
        String fileName = "/Users/bookgvi/Documents/GW_RGS/parse.txt";
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(str.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static class Data {
        public String prc;
        public String pcs;
        public String ep;

        Data(String prc, String pcs, String ep) {
            this.prc = prc;
            this.pcs = pcs;
            this.ep = ep;
        }
    }
}
