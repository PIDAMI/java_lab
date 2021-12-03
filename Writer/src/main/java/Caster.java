import com.java_polytech.pipeline_interfaces.TYPE;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;

public class Caster {


    public static TYPE getCommonTypes(TYPE[] o1, TYPE[] o2){
        TYPE res = null;
        for (TYPE firstType: o1){
            for (TYPE secondType: o2){
                if (firstType.equals(secondType)){
                    res = firstType;
                    break;
                }
            }
        }
        return res;
    }



    public static int[] charsToInt(char[] chars, int nonEmptySize){

        ByteBuffer byteBuffer = ByteBuffer.allocate(nonEmptySize * Character.BYTES);
        CharBuffer charBuffer = byteBuffer.asCharBuffer();
        charBuffer.put(chars);
        IntBuffer intBuf = byteBuffer.asIntBuffer();
        int[] result = new int[intBuf.remaining()];
        intBuf.get(result);


        return result;
    }


    public static char[] intsToChars(int[] ints, int nonEmptySize){


        ByteBuffer byteBuffer = ByteBuffer.allocate(nonEmptySize * Integer.BYTES);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(ints);
        CharBuffer charBuf = byteBuffer.asCharBuffer();
        char[] result = new char[charBuf.remaining()];
        charBuf.get(result);

        return result;
    }

    public static byte[] charsToBytes(char[] chars, int nonEmptySize) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(nonEmptySize * Character.BYTES);
        byteBuffer.asCharBuffer().put(chars);
        return byteBuffer.array();
    }


    public static char[] bytesToChars (byte[] bytes, int nonEmptySize) {

        CharBuffer charBuffer = ByteBuffer
                .wrap(bytes,0,nonEmptySize)
                .asCharBuffer();
        char[] result = new char[charBuffer.remaining()];
        charBuffer.get(result);
        return result;
    }


    public static int[] bytesToInts(byte[] bytes, int nonEmptySize){


        IntBuffer intBuf = ByteBuffer
                .wrap(bytes,0,nonEmptySize)
                .asIntBuffer();
        int[] result = new int[intBuf.remaining()];
        intBuf.get(result);
        return result;
    }

    public static byte[] intsToBytes(int[] ints, int nonEmptySize){


        ByteBuffer byteBuffer = ByteBuffer.allocate(nonEmptySize * Integer.BYTES);
        byteBuffer.asIntBuffer().put(ints);
        return byteBuffer.array();
    }
}