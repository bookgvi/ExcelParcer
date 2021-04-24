public class App {
    public static void main(String[] args) {
//        String fileName = "/Users/bookgvi/Documents/GW_RGS/bso_10_03_2021.xlsx";
//        Parser.parse(fileName);
        String baseFileName = "/Users/bookgvi/Documents/GW_RGS/Agreport_April/PCG-55820_15";
        String inFile = baseFileName + "-.xlsx";
        String outFile = baseFileName + "-.txt";

        ParserV2.parse(inFile, outFile);
    }
}

//PCG-55820_2-.xlsx
