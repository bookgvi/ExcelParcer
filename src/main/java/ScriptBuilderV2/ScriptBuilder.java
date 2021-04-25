package ScriptBuilderV2;

import Constants.FilesAndDirectories;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScriptBuilder {
    private final String _arrayWithData;

    public ScriptBuilder(List<Data> arrayWithData) {
        _arrayWithData = new Serializer().serializeToArrayWithData(arrayWithData);
    }

    public void buildAndSave(String fileName) {
        String part1 = loadFromFile(FilesAndDirectories.scriptFilePart1);
        String part2 = loadFromFile(FilesAndDirectories.scriptFilePart2);
        String script = concatScriptParts(part1, _arrayWithData, part2);
        saveToFile(script, fileName);
    }

    private String loadFromFile(String fileName) {
        StringBuilder file = new StringBuilder();
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String line;
            while((line = buffer.readLine()) != null) {
                file.append(line).append("\n");
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return file.toString();
    }

    private void saveToFile(String inputData, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(inputData.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String concatScriptParts(String part1, String part2, String part3) {
        return part1 + part2 + part3;
    }
}
