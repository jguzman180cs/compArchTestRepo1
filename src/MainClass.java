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

        System.out.println("hello wrold");
    }
}
