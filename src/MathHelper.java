public class MathHelper {

    public static boolean isPowerOf2(int num){
        if(num<0){
            throw new IllegalArgumentException("Number less than 0: "+num);
        }
        return ((num & num-1)==0);
    }

    public static double log2(int num){
        return Math.log(num)/Math.log(2);
    }
}
