package ScriptBuilder;

import Constants.FilesAndDirectories;
import com.google.gson.Gson;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScriptBuilder {
    private final ArrayList<Data> resultMap = new ArrayList<>();

    public ScriptBuilder() {}

    public Builder newBuilder() {
        return new Builder();
    }

    public void createMap(ArrayList<String> values) {
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
                .replace("}", ")\n")
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

    public class Builder {
        private int MAX_CHUNK_SIZE = 666;

        private Builder() {
        }

        Builder setChunkSize(int chunkSize) {
            MAX_CHUNK_SIZE = chunkSize;
            return this;
        }

        public void buildScripts(String outFile) {
            final int THREADS_COUNT = 2;
            String scriptPart1;
            String scriptPart2;
            ExecutorService executorService = Executors.newFixedThreadPool(THREADS_COUNT);
            CountDownLatch mainCDL = new CountDownLatch(2);
            try {
                scriptPart1 = executorService.submit(new FileLoaderThread(mainCDL, FilesAndDirectories.scriptFilePart1)).get();
                scriptPart2 = executorService.submit(new FileLoaderThread(mainCDL, FilesAndDirectories.scriptFilePart2)).get();
                mainCDL.await();
            } catch (InterruptedException | ExecutionException ignored) {
                scriptPart1 = getFromFile(FilesAndDirectories.scriptFilePart1);
                scriptPart2 = getFromFile(FilesAndDirectories.scriptFilePart2);
            } finally {
                executorService.shutdown();
            }

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
