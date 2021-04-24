public class App {
    public static void main(String[] args) {
//        String fileName = "/Users/bookgvi/Documents/GW_RGS/bso_10_03_2021.xlsx";
//        Parser.parse(fileName);
        String baseFileName = "/Users/bookgvi/Documents/GW_RGS/Agreport_April/PCG-55820_";
        String inExt = "-.xlsx";
//        ParserV2.parse(inFile, outFile);

        for (int fileNumber = 2; fileNumber <= 15; fileNumber++) {
            String inFile = baseFileName + fileNumber + inExt;
            String outFile = baseFileName + fileNumber;
            ScriptBuilder.parse(inFile, outFile);
        }
    }
}

//PCG-55820_2-.xlsx
