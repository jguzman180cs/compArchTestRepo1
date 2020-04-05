import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

        Cache.initializeCache(cacheSizeInKB, blockSize, associativity, ReplacementPolicy.valueOf(replacementPolicy));

        try{
            File inputFile = new File(traceFileName);
            String line1, line2, line3;
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            //Read three lines at a time, "EIP...", "dstM..." and a new line
            while((line1 = reader.readLine()) != null &&
                    (line2 = reader.readLine()) != null &&
                    (line3 = reader.readLine()) != null){
                System.out.println(line1);
                System.out.println(line2);
                System.out.println(line3);
                System.out.println("END OF LINE 1, 2, 3");
            }
        } catch (IOException e){
            System.out.printf("%nSomething unexpected happened when reading the trace file. Try again.%n");
        }
    }
}
