import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class FileLoaderThread implements Callable<String> {
    private final String _fileName;

    FileLoaderThread(String fileName) {
        _fileName = fileName;
    }

    @Override
    public String call() throws Exception {
        StringBuilder result = new StringBuilder();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(_fileName)));
        String fileLine;
        while((fileLine = buffer.readLine()) != null) {
            result.append(fileLine).append("\n");
        }
        return result.toString();
    }
}