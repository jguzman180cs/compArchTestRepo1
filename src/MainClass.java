import java.io.*;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainClass {
    private static String traceFileName;
    private static int cacheSizeInKB;
    private static int blockSize;
    private static int associativity;
    private static String replacementPolicy;
    public static int count;

    public static void main(String[] args){
        for(int i = 0;i < args.length; i++){
            if (args[i].equals("-f")){
                traceFileName = args[i+1];
            } else if(args[i].equals("-s")){
                cacheSizeInKB = Integer.parseInt(args[i+1]);
            } else if(args[i].equals("-b")){
                blockSize = Integer.parseInt(args[i+1]);
            } else if(args[i].equals("-a")){
                associativity = Integer.parseInt(args[i+1]);
            } else if(args[i].equals("-r")){
                replacementPolicy = args[i+1];
            }
        }

        Cache.initializeCache(cacheSizeInKB, blockSize, associativity, ReplacementPolicy.valueOf(replacementPolicy.toUpperCase()));
//        outputCacheNumbers();
        //when creating our output files we use the print method below
        // otherwise we use outputCacheNumbers() when executing a jar or just running the project in IntelliJ
//        printCacheOutputNumbersToFile();
        Pattern regexPattern = Pattern.compile("EIP \\(([0-9]{2})\\): ([a-f0-9]{8})");
        //match the count and the address in 2 separate groups if the line is in this format:
        //EIP (##): xxxxxxxx
        Matcher patternMatcher;

        try{
            count = 0;
            File inputFile = new File(traceFileName);
            String line1, line2, line3;
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            //Read three lines at a time, "EIP...", "dstM..." and a new line
            while((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null && (line3 = reader.readLine()) != null){// && count < 20){
                patternMatcher = regexPattern.matcher(line1);
                patternMatcher.find();
                String instructionLength = patternMatcher.group(1);
                String instructionAddress = patternMatcher.group(2);
                Cache.accessAddress(Integer.parseInt(instructionAddress, 16), Integer.parseInt(instructionLength));
//                System.out.printf("count = %s, address = 0x%s%n", patternMatcher.group(1), patternMatcher.group(2).toUpperCase());
                //use the print statement above for debugging
                count++;
            }
        } catch (IOException e){
            System.out.printf("%nSomething unexpected happened when reading the trace file. Try again.%n");
        }
        outputCacheSimulatorNumbers(); //use when debugging
//        printMilestoneTwoNumbersToFile();
    }

    public static void outputCacheNumbers(){
        System.out.println("Cache Simulator - CS 3853 - Team 14");
        System.out.println();
        System.out.printf("Trace File: %s%n", traceFileName);
        System.out.println();
        System.out.println("***** Cache Input Parameters *****");
        System.out.println();
        System.out.printf("Cache Size: \t\t\t%s KB%n", Cache.getCacheSizeKB());
        System.out.printf("Block Size: \t\t\t%s bytes%n", Cache.getBlockSize());
        System.out.printf("Associativity: \t\t\t%s%n", Cache.getAssociativity());
        System.out.printf("Replacement Policy: \t%s%n", Cache.getReplacementPolicy().getStringName());
        System.out.println();

        System.out.println("***** Cache Calculated Values *****");
        System.out.println();
        System.out.printf("Total # Blocks: \t\t\t\t%s%n", Cache.getTotalBlocks());
        System.out.printf("Tag Size: \t\t\t\t\t\t%s bits%n", Cache.getTagBitSize());
        System.out.printf("Index Size: \t\t\t\t\t%s bits%n", Cache.getIndexBitSize());
        System.out.printf("Total # Rows: \t\t\t\t\t%s%n", Cache.getNumOfRows());
        System.out.printf("OverheadSize:  \t\t\t\t\t%s bytes%n", Cache.getOverheadMemorySizeBytes());
        System.out.printf("Implementation Memory Size: \t%s KB (%s bytes)%n", Cache.getImplementationMemorySizeBytes() / 1024, Cache.getImplementationMemorySizeBytes());
        System.out.printf("Cost: \t\t\t\t\t\t\t$%s%n", Cache.getCost());
    }

    public static void printCacheOutputNumbersToFile() {
        try{
            FileWriter writer = new FileWriter("O3.txt");
            String newLine = System.getProperty("line.separator");

            writer.write("Cache Simulator - CS 3853 - Team 14");
            writer.write(newLine);
            writer.write(newLine);
            writer.write(String.format("Trace File: %s%n", traceFileName));
            writer.write(newLine);
            writer.write("***** Cache Input Parameters *****");
            writer.write(newLine);
            writer.write(newLine);
            writer.write(String.format("Cache Size: \t\t\t%s KB%n", Cache.getCacheSizeKB()));
            writer.write(String.format("Block Size: \t\t\t%s bytes%n", Cache.getBlockSize()));
            writer.write(String.format("Associativity: \t\t\t%s%n", Cache.getAssociativity()));
            writer.write(String.format("Replacement Policy: \t%s%n", Cache.getReplacementPolicy().getStringName()));
            writer.write(newLine);
            writer.write(newLine);

            writer.write("***** Cache Calculated Values *****");
            writer.write(newLine);
            writer.write(newLine);
            writer.write(String.format("Total # Blocks: \t\t\t\t%s%n", Cache.getTotalBlocks()));
            writer.write(String.format("Tag Size: \t\t\t\t\t\t%s bits%n", Cache.getTagBitSize()));
            writer.write(String.format("Index Size: \t\t\t\t\t%s bits%n", Cache.getIndexBitSize()));
            writer.write(String.format("Total # Rows: \t\t\t\t\t%s%n", Cache.getNumOfRows()));
            writer.write(String.format("OverheadSize:  \t\t\t\t\t%s bytes%n", Cache.getOverheadMemorySizeBytes()));
            writer.write(String.format("Implementation Memory Size: \t%s KB (%s bytes)%n", Cache.getImplementationMemorySizeBytes() / 1024, Cache.getImplementationMemorySizeBytes()));
            writer.write(String.format("Cost: \t\t\t\t\t\t\t$%s%n", Cache.getCost()));
            writer.close();
        } catch (IOException e){
            System.out.printf("%nSomethind unexpected happened when writing to file%n");
        }
    }

    public static void outputCacheSimulatorNumbers(){
        DecimalFormat formatOne = new DecimalFormat("#.00");
        DecimalFormat formatTwo= new DecimalFormat("#.##");
        Cache.calculateUnusedCacheBlocks();
        System.out.println("***** Cache Simulation Results *****");
        System.out.println();
        System.out.printf("Total Cache Accesses: \t%s%n", Cache.getTotalAccess());
        System.out.printf("Cache Hits: \t\t\t%s %n", Cache.getHits());
        System.out.printf("Cache Misses: \t\t\t%s%n", Cache.getCompulsoryMisses()+Cache.getConflictMisses());
        System.out.printf("--- Compulsory Misses: \t\t%s%n", Cache.getCompulsoryMisses());
        System.out.printf("--- Conflict Misses: \t\t%s%n", Cache.getConflictMisses());
        System.out.println();

        System.out.println("***** ***** CACHE MISS RATE: ***** *****");
        System.out.println();
        System.out.printf("Hit Rate: \t\t\t\t\t%s%%%n", formatOne.format(Cache.getHitRate()));
        System.out.printf("CPI: \t\t\t\t\t\t%s Cycles/Instruction%n", Cache.getCPI(count));
        System.out.printf("Unused Cache Space: \t\t%.4f KB / %s KB = %s%% Waste: $%s %n", Cache.getUnusedCacheInKB(), Cache.getCacheSizeKB(),
                formatTwo.format(Cache.getCachePercentageNotUsed()), Cache.getWaste());
        System.out.printf("Unused Cache Blocks: \t\t%s / %s%n", Cache.getUnusedBlocks(), Cache.getTotalBlocks());
    }

    public static void printMilestoneTwoNumbersToFile() {
        DecimalFormat formatOne = new DecimalFormat("#.00");
        DecimalFormat formatTwo= new DecimalFormat("#.##");
        Cache.calculateUnusedCacheBlocks();
        try{
            FileWriter writer = new FileWriter("O5.txt");
            String newLine = System.getProperty("line.separator");

            writer.write("Cache Simulator - CS 3853 - Team 14");
            writer.write(newLine);
            writer.write(newLine);
            writer.write(String.format("Trace File: %s%n", traceFileName));
            writer.write(newLine);
            writer.write("***** Cache Input Parameters *****");
            writer.write(newLine);
            writer.write(newLine);
            writer.write(String.format("Cache Size: \t\t\t%s KB%n", Cache.getCacheSizeKB()));
            writer.write(String.format("Block Size: \t\t\t%s bytes%n", Cache.getBlockSize()));
            writer.write(String.format("Associativity: \t\t\t%s%n", Cache.getAssociativity()));
            writer.write(String.format("Replacement Policy: \t%s%n", Cache.getReplacementPolicy().getStringName()));
            writer.write(newLine);
            writer.write(newLine);

            writer.write("***** Cache Calculated Values *****");
            writer.write(newLine);
            writer.write(newLine);
            writer.write(String.format("Total # Blocks: \t\t\t\t%s%n", Cache.getTotalBlocks()));
            writer.write(String.format("Tag Size: \t\t\t\t\t\t%s bits%n", Cache.getTagBitSize()));
            writer.write(String.format("Index Size: \t\t\t\t\t%s bits%n", Cache.getIndexBitSize()));
            writer.write(String.format("Total # Rows: \t\t\t\t\t%s%n", Cache.getNumOfRows()));
            writer.write(String.format("OverheadSize:  \t\t\t\t\t%s bytes%n", Cache.getOverheadMemorySizeBytes()));
            writer.write(String.format("Implementation Memory Size: \t%s KB (%s bytes)%n", Cache.getImplementationMemorySizeBytes() / 1024, Cache.getImplementationMemorySizeBytes()));
            writer.write(String.format("Cost: \t\t\t\t\t\t\t$%s%n", Cache.getCost()));
            writer.write(newLine);
            writer.write(newLine);
            writer.write("***** Cache Simulation Results *****");
            writer.write(newLine);
            writer.write(String.format("Total Cache Accesses: \t%s%n", Cache.getTotalAccess()));
            writer.write(String.format("Cache Hits: \t\t\t%s %n", Cache.getHits()));
            writer.write(String.format("Cache Misses: \t\t\t%s%n", Cache.getCompulsoryMisses()+Cache.getConflictMisses()));
            writer.write(String.format("--- Compulsory Misses: \t\t%s%n", Cache.getCompulsoryMisses()));
            writer.write(String.format("--- Conflict Misses: \t\t%s%n", Cache.getConflictMisses()));
            writer.write(newLine);

            writer.write("***** ***** CACHE MISS RATE: ***** *****");
            writer.write(newLine);
            writer.write(String.format("Hit Rate: \t\t\t\t\t%s%%%n", formatOne.format(Cache.getHitRate())));
            writer.write(String.format("CPI: \t\t\t\t\t\t%s Cycles/Instruction%n", Cache.getCPI(count)));
            writer.write(String.format("Unused Cache Space: \t\t%.4f KB / %s KB = %s%% Waste: $%s %n", Cache.getUnusedCacheInKB(), Cache.getCacheSizeKB(),
                    formatTwo.format(Cache.getCachePercentageNotUsed()), Cache.getWaste()));
            writer.write(String.format("Unused Cache Blocks: \t\t%s / %s%n", Cache.getUnusedBlocks(), Cache.getTotalBlocks()));
            writer.close();
        } catch (IOException e){
            System.out.printf("%nSomething unexpected happened when writing to file%n");
        }
    }
}
