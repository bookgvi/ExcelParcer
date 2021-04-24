import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
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
    private final ArrayList<Data> resultMap = new ArrayList<>();
    private final String directory = "/Users/bookgvi/Documents/GW_RGS/AgreportScript/";
    private final String scriptFilePart1 = directory + "PCG-55820_link_agreport_pps_pcs_part1.gsp";
    private final String scriptFilePart2 = directory + "PCG-55820_link_agreport_pps_pcs_part2.gsp";


    private ScriptBuilder() {
    }

    public static Builder newBuilder() {
        return new ScriptBuilder().new Builder();
    }

    private void createMap(ArrayList<String> values) {
        Data data = new Data(values.get(0).trim(), values.get(1).trim(), values.get(2).trim());
        resultMap.add(data);
    }

    private String serializeMap(List data) {
        Gson gson = new Gson();
        return gson.toJson(data)
                .replace("[", "")
                .replace("{\"_first\":", "new Data(")
                .replace(",\"_second\":", ", ")
                .replace("\"_third\":", " ")
                .replace("},", "),\n")
                .replace("}", ")")
                .replace("]", "");
    }

    static class Data {
        public String _first;
        public String _second;
        public String _third;

        private Data(String first, String second, String third) {
            _first = first;
            _second = second;
            _third = third;
        }
    }

    class Builder {
        private int MAX_CHUNK_SIZE = 666;

        private Builder() {
        }

        public Builder parse(String inFile) {
            InputStream inputStream = null;
            XSSFWorkbook workBook = null;
            ArrayList<String> values = new ArrayList<>();
            try {
                inputStream = new FileInputStream(inFile);
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
                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    values.add(cell.getStringCellValue());
                }
                ScriptBuilder.this.createMap(values);
            }
            System.out.println(count);
            return this;
        }

        Builder setChunkSize(int chunkSize) {
            MAX_CHUNK_SIZE = chunkSize;
            return this;
        }

        void buildScripts(String outFile) {
            String scriptPart1 = getFromFile(scriptFilePart1);
            String scriptPart2 = getFromFile(scriptFilePart2);
            final int CHUNK_SIZE = Math.min(MAX_CHUNK_SIZE, resultMap.size());
            int fileSubIndex = 1;
            String delimeter = "_";
            String _outExt = "_042021.gsp";

            for (List<Data> dataChunk : com.google.common.collect.Iterables.partition(resultMap, CHUNK_SIZE)) {
                String script = concatScriptParts(scriptPart1, serializeMap(dataChunk), scriptPart2);
                this.saveToFile(script, outFile + delimeter + fileSubIndex + _outExt);
                fileSubIndex++;
            }
        }

        private String concatScriptParts(String part1, String part2, String part3) {
            return part1 + part2 + part3;
        }

        private void saveToFile(String inputData, String fileName) {
            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                fos.write(inputData.getBytes());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private String getFromFile(String fileName) {
            StringBuilder result = new StringBuilder();
            try {
                BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
                String fileLine;
                while ((fileLine = buffer.readLine()) != null) {
                    result.append(fileLine).append("\n");
                }
            } catch (IOException fnf) {
                fnf.printStackTrace();
            }
            return result.toString();
        }
    }
}
