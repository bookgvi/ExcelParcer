package ScriptBuilderV2;

import java.util.ArrayList;
import java.util.List;

public class Executor {

    public static void main(String[] args) {
        final int CHUNK_SIZE = 666;
        final int FIRST_FILE_NUMBER = 2;
        final int LAST_FILE_NUMBER = 15;

        String baseFileName = "/Users/bookgvi/Documents/GW_RGS/Agreport_April/PCG-55820_";
        String inExt = "-.xlsx";
        String outExt = "_042021.gsp";
        ArrayList<Data> arrayListWithData = new ArrayList<>();
        int scriptsCount = 0;

        for (int index = FIRST_FILE_NUMBER; index <= LAST_FILE_NUMBER; index++) {
            String xlsFileName = baseFileName + index + inExt;
            arrayListWithData = new LoadXlsInArrayList().execute(xlsFileName);
            int subIndex = 1;
            for(List<Data> chunkOfData : com.google.common.collect.Iterables.partition(arrayListWithData, CHUNK_SIZE)) {
                String gspFileName = baseFileName + index + "_" + subIndex + outExt;
                new ScriptBuilder(chunkOfData).buildAndSave(gspFileName);
                subIndex++;
                scriptsCount++;
            }
        }
        System.out.printf("Number of scripts %d\n", scriptsCount);
    }
}
