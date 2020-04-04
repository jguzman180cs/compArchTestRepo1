import com.sun.xml.internal.ws.encoding.fastinfoset.FastInfosetStreamSOAPCodec;

public class Cache{

    /**
     * First index is cache index, 2nd index is associativity.
     */
    public static CacheEntry[][] cache;
    private static int hits = 0;
    private static int misses = 0;

    private static int cacheSizeBytes;
    private static int blockSize;
    private static int associativity;
    private static ReplacementPolicy replacementPolicy;

    private static int offsetBitCount;
    private static int indexBits;

    /**
     * Always call this before initially starting cache simulator.
     * @param cacheSizeKB cache size (internally, this will be stored in bytes)
     * @param blockSize size of each block (in bytes)
     * @param associativity (must be stored in a power of 2)
     * @param RP Replacement Policy for cache
     * @throws IllegalArgumentException if the cache settings are invalid for simulation
     */
    public static void initializeCache(int cacheSizeKB, int blockSize, int associativity, ReplacementPolicy RP)
    {
        if(!MathHelper.isPowerOf2(associativity)){
            throw new IllegalArgumentException("Associativity isn't a power of 2");
        }
        if(!(associativity>=1 && associativity<=16)){
            throw new IllegalArgumentException("Associativity not between 1 and 16");
        }
        if(cacheSizeKB<1 || cacheSizeKB>8*1014){
            throw new IllegalArgumentException("Cache size not within range of 1 KB to 8 MB");
        }
        if(RP==null){
            throw new IllegalArgumentException("Invalid replacement policy");
        }
        if(!MathHelper.isPowerOf2(blockSize)){
            throw new IllegalArgumentException("Block size not a power of 2");
        }
        if(blockSize<4 || blockSize > 64){
            throw new IllegalArgumentException("Block size not between 4 to 64 bytes");
        }
        cacheSizeBytes = cacheSizeKB*1024;
        Cache.blockSize = blockSize;
        Cache.associativity = associativity;
        replacementPolicy = RP;

        int numOfIndexes = (cacheSizeBytes*8)/((blockSize*8)*associativity);
        offsetBitCount = (int) Math.ceil(MathHelper.log2(blockSize));
        indexBits = (int) Math.ceil(MathHelper.log2(numOfIndexes));

        cache = new CacheEntry[numOfIndexes][associativity];
        for(int y = 0; y < cache.length; y++){
            for(int x = 0; x < cache[y].length; x++){
                cache[y][x] = new CacheEntry(0,false);
            }
        }
    }


    /**
     * Use the toString() method on the return type to get the string representation
     * @return enum representation of replacement policy (call toString() to get string representation)
     * @throws RuntimeException errors when cache isn't initialized
     */
    public static ReplacementPolicy getReplacementPolicy(){
        if(replacementPolicy==null || cache == null){
            throw new RuntimeException("Cache not yet initialized");
        }
        return replacementPolicy;
    }

    /**
     *
     * @return cache size in KB
     * @throws RuntimeException errors when cache isn't initialized
     */
    public static int getCacheSizeKB(){
        if(cacheSizeBytes==0 || cache == null){
            throw new RuntimeException("Cache not yet initialized");
        }
        return cacheSizeBytes/1024;
    }

    /**
     *
     * @return associativity of cache
     * @throws RuntimeException errors when cache isn't initialized
     */
    public static int getAssociativity(){
        if(cache==null || associativity == 0){
            throw new RuntimeException("Cache not yet initialized");
        }
        return associativity;
    }

    /**
     * In bytes
     * @return block size in bytes
     * @throws RuntimeException errors when cache isn't initialized
     */
    public static int getBlockSize(){
        if(cache == null || blockSize == 0){
            throw new RuntimeException("Cache not yet initialized");
        }
        return blockSize;
    }

    public static int getIndexBitSize(){
        if(cache == null || indexBits == 0){
            throw new RuntimeException("Cache not yet initialized");
        }
        return indexBits;
    }

    public static int getOffsetBitSize() {
        if(cache == null || offsetBitCount == 0){
            throw new RuntimeException("Cache not yet initialized");
        }
        return offsetBitCount;
    }

    public static int getTagBitSize(){
        return 32 - getIndexBitSize() - getOffsetBitSize();
    }

    public static int getOverheadMemorySizeBytes(){
        if(cache == null || cacheSizeBytes == 0){
            throw new RuntimeException("Cache not yet initialized");
        }
        int numOfTagBits = getTagBitSize();
        return (int) Math.ceil(((numOfTagBits+1)*cache.length*getAssociativity())/8f);
    }

    public static int getImplementationMemorySizeBytes(){
        return getCacheSizeKB()*1024 + getOverheadMemorySizeBytes();
    }
}