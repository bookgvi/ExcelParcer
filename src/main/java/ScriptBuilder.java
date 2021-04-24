import com.google.gson.Gson;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.Buffer;
import java.util.*;

/**
 * 1) Парсинг xls в HashMap<String, ArrayList<String>>
 * первая колонка во входящем xls-файле становится ключем хэшмап,
 * остальные добавляются в массив, который в свою очередь есть значеие этого хэшмапа
 * 2) Сериализация в GOSU hashmap к виду
 * {"S25067700023153" -> {"12375","10001"}}
 * 3) сохранение полученого файл parse.gs
 */
public class ScriptBuilder {
    private static final ArrayList<Data> resultMap = new ArrayList<>();
    private static final String directory = "/Users/bookgvi/Documents/GW_RGS/AgreportScript/";
    private static final String scriptFilePart1 = directory + "PCG-55820_link_agreport_pps_pcs_part1.gsp";
    private static final String scriptFilePart2 = directory + "PCG-55820_link_agreport_pps_pcs_part2.gsp";
    private static String _outFile;
    private static String _inFile;

    public static void parse(String inFile, String outFile) {
        _outFile = outFile;
        _inFile = inFile;
        InputStream inputStream = null;
        XSSFWorkbook workBook = null;
        try {
            inputStream = new FileInputStream(_inFile);
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
        String scriptPart1 = getFromFile(scriptFilePart1);
        String scriptPart2 = getFromFile(scriptFilePart2);
        final int MAX_CHUNK_SIZE = 666;
        final int CHUNK_SIZE = Math.min(MAX_CHUNK_SIZE, resultMap.size());
        int fileSubIndex = 1;
        String delimeter = "_";
        String _outExt = "_042021.gsp";

        for(List<Data> dataChunk : com.google.common.collect.Iterables.partition(resultMap, CHUNK_SIZE)) {
            String script = concatScriptParts(scriptPart1, serializeMap(dataChunk), scriptPart2);
            saveToFile(script, _outFile + delimeter + fileSubIndex + _outExt);
            fileSubIndex++;
        }
        System.out.println(count);
    }

    private static void createMap(String key, ArrayList<String> values) {
        Data data = new Data(key.trim(), values.get(0).trim(), values.get(1).trim());
        resultMap.add(data);
    }

    private static String serializeMap(List data) {
        Gson gson = new Gson();
        return gson.toJson(data)
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

    private static void saveToFile(String inputData, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(inputData.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String getFromFile(String fileName) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String fileLine;
            while((fileLine = buffer.readLine()) != null) {
                result.append(fileLine).append("\n");
            }
        } catch (IOException fnf) {
            fnf.printStackTrace();
        }
        return result.toString();
    }

    private static String concatScriptParts(String part1, String part2, String part3) {
        return part1 + part2 + part3;
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
