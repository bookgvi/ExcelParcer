package ScriptBuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class FileLoaderThread implements Callable<String> {
    private final String _fileName;
    private final CountDownLatch _cdl;

    FileLoaderThread(CountDownLatch mainCdl, String fileName) {
        _cdl = mainCdl;
        _fileName = fileName;
    }

    @Override
    public String call() throws Exception {
        StringBuilder result = new StringBuilder();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(_fileName), StandardCharsets.UTF_8));
        String fileLine;
        while((fileLine = buffer.readLine()) != null) {
            result.append(fileLine).append("\n");
        }
        _cdl.countDown();
        return result.toString();
    }
}
