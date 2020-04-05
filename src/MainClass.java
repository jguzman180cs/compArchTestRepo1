import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainClass {
    private static String traceFileName;
    private static int cacheSizeInKB;
    private static int blockSize;
    private static int associativity;
    private static String replacementPolicy;

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
        outputCacheNumbers();
        Pattern regexPattern = Pattern.compile("EIP \\(([0-9]{2})\\): ([a-f0-9]{8})");
        //match the count and the address in 2 separate groups if the line is in this format:
        //EIP (##): xxxxxxxx
        Matcher patternMatcher;

        try{
            int count = 0;
            File inputFile = new File(traceFileName);
            String line1, line2, line3;
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            //Read three lines at a time, "EIP...", "dstM..." and a new line
            while((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null && (line3 = reader.readLine()) != null && count < 20){
                patternMatcher = regexPattern.matcher(line1);
                patternMatcher.find();
                System.out.printf("count = %s, address = 0x%s%n", patternMatcher.group(1), patternMatcher.group(2).toUpperCase());
                count++;
            }
        } catch (IOException e){
            System.out.printf("%nSomething unexpected happened when reading the trace file. Try again.%n");
        }
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
        System.out.println("***** Cache Calculated Values *****");
        System.out.println();
        System.out.printf("Total # Blocks: \t\t\t\t%s%n", Cache.getNumOfBlocks());
        System.out.printf("Tag Size: \t\t\t\t\t\t%s bits%n", Cache.getTagBitSize());
        System.out.printf("Index Size: \t\t\t\t\t%s bits%n", Cache.getIndexBitSize());
        System.out.printf("Total # Rows: \t\t\t\t\t%s%n", Cache.getNumOfRows());
        System.out.printf("OverheadSize:  \t\t\t\t\t%s bytes%n", Cache.getOverheadMemorySizeBytes());
        System.out.printf("Implementation Memory Size: \t%s KB (%s bytes)%n", Cache.getImplementationMemorySizeBytes() / 1024, Cache.getImplementationMemorySizeBytes());
        System.out.printf("Cost: \t\t\t\t\t\t\t$%s%n", Cache.getCost());
    }
}
