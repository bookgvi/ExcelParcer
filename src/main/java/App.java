import ScriptBuilder.ScriptBuilder;
import ScriptBuilder.ParcerThread;

import java.util.HashMap;
import java.util.concurrent.*;

public class App {
    public static void main(String[] args) {
//        String fileName = "/Users/bookgvi/Documents/GW_RGS/bso_10_03_2021.xlsx";
//        Parser.parse(fileName);
        String baseFileName = "/Users/bookgvi/Documents/GW_RGS/Agreport_April/PCG-55820_";
        String inExt = "-.xlsx";
//        ParserV2.parse(inFile, outFile);

        final int COUNT = 15;
        ExecutorService executorService = Executors.newFixedThreadPool(COUNT);
        CountDownLatch mainCDL = new CountDownLatch(COUNT - 1);
        HashMap<String, ScriptBuilder.Builder> scriptBuilders = new HashMap<>();
        try {
            for (int fileNumber = 2; fileNumber <= COUNT; fileNumber++) {
                String inFile = baseFileName + fileNumber + inExt;
                String outFile = baseFileName + fileNumber;
                executorService
                        .submit(new ParcerThread(mainCDL, inFile))
                        .get()
                        .buildScripts(outFile);
            }
            mainCDL.await();
        } catch (InterruptedException | ExecutionException ignored) {
        } finally {
            executorService.shutdown();
        }
    }
}

//PCG-55820_2-.xlsx
