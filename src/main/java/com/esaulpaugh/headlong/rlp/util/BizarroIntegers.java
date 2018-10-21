package com.esaulpaugh.headlong.rlp.util;

import com.esaulpaugh.headlong.rlp.DecodeException;

import static com.esaulpaugh.headlong.util.Utils.EMPTY_BYTE_ARRAY;

/**
 * The mirror image of {@link Integers}. Not compatible with the RLP specification.
 *
 * Negative integers are stored in a minimal big-endian two's complement representation. Non-negative integers are
 * stored full-length. Negative one is represented by the empty byte array. Numbers are sign-extended on decode.
 *
 * -256L ≡ 0x00
 *  -16L ≡ 0xf0
 *   -1L ≡ 0x
 *    0L ≡ 0x0000000000000000
 *    1L ≡ 0x0000000000000001
 *   16L ≡ 0x0000000000000010
 */
public class BizarroIntegers {

    public static byte[] toBytes(byte val) {
        if(val == -1) {
            return EMPTY_BYTE_ARRAY;
        }
        return new byte[] { val };
    }

    public static byte[] toBytes(short val) {
        if(val == -1) {
            return EMPTY_BYTE_ARRAY;
        }
        int n = len(val);
        byte[] bytes = new byte[n];
        putShort(val, bytes, 0);
        return bytes;
    }

    public static byte[] toBytes(int val) {
        if(val == -1) {
            return EMPTY_BYTE_ARRAY;
        }
        int n = len(val);
        byte[] bytes = new byte[n];
        putInt(val, bytes, 0);
        return bytes;
    }

    public static byte[] toBytes(long val) {
        if(val == -1) {
            return EMPTY_BYTE_ARRAY;
        }
        int n = len(val);
        byte[] bytes = new byte[n];
        putLong(val, bytes, 0);
        return bytes;
    }

    public static int putByte(byte val, byte[] o, int i) {
        if(val != -1) {
            o[i] = val;
            return 1;
        }
        return 0;
    }

    public static int putShort(short val, byte[] o, int i) {
        byte b = 0;
        int n = 0;
        if(val != -1) {
            n = 1;
            b = (byte) val;
//            val = (short) (val >>> Byte.SIZE); // ICAST_QUESTIONABLE_UNSIGNED_RIGHT_SHIFT
            val = (short) (val >> Byte.SIZE); // high bytes chopped off either way, see above
            if (val != -1) {
                n = 2;
            }
        }
        switch (n) {
        case 0: return 0;
        case 1: o[i]=b; return 1;
        default: o[i]=(byte)val; o[i+1]=b; return 2;
        }
    }

    public static int putInt(int val, byte[] o, int i) {
        byte b = 0, c = 0, d = 0;
        int n = 0;
        if(val != -1) {
            n = 1;
            d = (byte) val;
            val = val >> Byte.SIZE;
            if (val != -1) {
                n = 2;
                c = (byte) val;
                val = val >> Byte.SIZE;
                if (val != -1) {
                    n = 3;
                    b = (byte) val;
                    val = val >> Byte.SIZE;
                    if (val != -1) {
                        n = 4;
                    }
                }
            }
        }
        switch (n) {
        case 0: return 0;
        case 1: o[i]=d; return 1;
        case 2: o[i]=c; o[i+1]=d; return 2;
        case 3: o[i]=b; o[i+1]=c; o[i+2]=d; return 3;
        default:
        o[i]=(byte)val; o[i+1]=b; o[i+2]=c; o[i+3]=d; return 4;
        }
    }

    public static int putLong(long val, byte[] o, int i) {
        byte b = 0, c = 0, d = 0, e = 0, f = 0, g = 0, h = 0;
        int n = 0;
        if(val != -1) {
            n = 1;
            h = (byte) val;
            val = val >> Byte.SIZE;
            if (val != -1) {
                n = 2;
                g = (byte) val;
                val = val >> Byte.SIZE;
                if (val != -1) {
                    n = 3;
                    f = (byte) val;
                    val = val >> Byte.SIZE;
                    if (val != -1) {
                        n = 4;
                        e = (byte) val;
                        val = val >> Byte.SIZE;
                        if (val != -1) {
                            n = 5;
                            d = (byte) val;
                            val = val >> Byte.SIZE;
                            if (val != -1) {
                                n = 6;
                                c = (byte) val;
                                val = val >> Byte.SIZE;
                                if (val != -1) {
                                    n = 7;
                                    b = (byte) val;
                                    val = val >> Byte.SIZE;
                                    if (val != -1) {
                                        n = 8;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        switch (n) {
        case 0: return 0;
        case 1: o[i]=h; return 1;
        case 2: o[i]=g; o[i+1]=h; return 2;
        case 3: o[i]=f; o[i+1]=g; o[i+2]=h; return 3;
        case 4: o[i]=e; o[i+1]=f; o[i+2]=g; o[i+3]=h; return 4;
        case 5: o[i]=d; o[i+1]=e; o[i+2]=f; o[i+3]=g; o[i+4]=h; return 5;
        case 6: o[i]=c; o[i+1]=d; o[i+2]=e; o[i+3]=f; o[i+4]=g; o[i+5]=h; return 6;
        case 7: o[i]=b; o[i+1]=c; o[i+2]=d; o[i+3]=e; o[i+4]=f; o[i+5]=g; o[i+6]=h; return 7;
        default:
        o[i]=(byte)val; o[i+1]=b; o[i+2]=c; o[i+3]=d; o[i+4]=e; o[i+5]=f; o[i+6]=g; o[i+7]=h; return 8;
        }
    }

    private static byte _getByte(byte[] buffer, int i, int len) throws DecodeException {
        switch (len) {
        case 0: return 0;
        case 1: return buffer[i];
        default: throw new DecodeException(new IllegalArgumentException("len is out of range: " + len));
        }
    }

    private static short _getShort(byte[] buffer, int i, int len) throws DecodeException {
        int shiftAmount = 0;
        int val = 0;
        switch (len) { /* cases 2 through 1 fall through */
        case 2: val = buffer[i+1] & 0xFF; shiftAmount = Byte.SIZE; // & 0xFF to promote to int before left shift
        case 1: val |= (buffer[i] & 0xFF) << shiftAmount;
        case 0: return (short) val;
        default: throw new DecodeException(new IllegalArgumentException("len is out of range: " + len));
        }
    }

    public static int _getInt(byte[] buffer, int i, int len) throws DecodeException {
        int shiftAmount = 0;
        int val = 0;
        switch (len) { /* cases 4 through 1 fall through */
        case 4: val = buffer[i+3] & 0xFF; shiftAmount = Byte.SIZE;
        case 3: val |= (buffer[i+2] & 0xFF) << shiftAmount; shiftAmount += Byte.SIZE;
        case 2: val |= (buffer[i+1] & 0xFF) << shiftAmount; shiftAmount += Byte.SIZE;
        case 1: val |= (buffer[i] & 0xFF) << shiftAmount;
        case 0: return val;
        default: throw new DecodeException(new IllegalArgumentException("len is out of range: " + len));
        }
    }

    private static long _getLong(final byte[] buffer, final int i, final int len) throws DecodeException {
        int shiftAmount = 0;
        long val = 0L;
        switch (len) { /* cases 8 through 1 fall through */
        case 8: val = buffer[i+7] & 0xFF; shiftAmount = Byte.SIZE;
        case 7: val |= (buffer[i+6] & 0xFF) << shiftAmount; shiftAmount += Byte.SIZE;
        case 6: val |= (buffer[i+5] & 0xFF) << shiftAmount; shiftAmount += Byte.SIZE;
        case 5: val |= (buffer[i+4] & 0xFFL) << shiftAmount; shiftAmount += Byte.SIZE;
        case 4: val |= (buffer[i+3] & 0xFFL) << shiftAmount; shiftAmount += Byte.SIZE;
        case 3: val |= (buffer[i+2] & 0xFFL) << shiftAmount; shiftAmount += Byte.SIZE;
        case 2: val |= (buffer[i+1] & 0xFFL) << shiftAmount; shiftAmount += Byte.SIZE;
        case 1: val |= (buffer[i] & 0xFFL) << shiftAmount;
        case 0: return val;
        default: throw new DecodeException(new IllegalArgumentException("len is out of range: " + len));
        }
    }

    public static byte getByte(byte[] buffer, int index, int len) throws DecodeException {
        switch (len) {
        case 0: return (byte) 0xFF;
        case 1: return _getByte(buffer, index, 1);
        default: throw new IllegalArgumentException("len is out of range: " + len);
        }
    }

    public static short getShort(byte[] buffer, int index, int len) throws DecodeException {
        // do sign extension for negative shorts, i.e. len < 2
        switch (len) {
        case 0: return (short) 0xFFFF;
        case 1: return (short) (0xFFFFFF00 | _getByte(buffer, index, 1));
        case 2: return _getShort(buffer, index, 2);
        default: throw new IllegalArgumentException("len is out of range: " + len);
        }
    }

    public static int getInt(byte[] buffer, int index, int len) throws DecodeException {
        // do sign extension for negative ints, i.e. len < 4
        switch (len) {
        case 0: return 0xFFFFFFFF;
        case 1: return 0xFFFFFF00 | _getByte(buffer, index, 1);
        case 2: return 0xFFFF0000 | _getShort(buffer, index, 2);
        case 3: return 0xFF000000 | _getInt(buffer, index, 3);
        case 4: return _getInt(buffer, index, 4);
        default: throw new IllegalArgumentException("len is out of range: " + len);
        }
    }

    public static long getLong(final byte[] buffer, final int index, final int len) throws DecodeException {
        // do sign extension for negative longs, i.e. len < 8
        switch (len) {
        case 0: return 0xFFFFFFFF_FFFFFFFFL;
        case 1: return 0xFFFFFFFF_FFFFFF00L | _getByte(buffer, index, 1);
        case 2: return 0xFFFFFFFF_FFFF0000L | _getShort(buffer, index, 2);
        case 3: return 0xFFFFFFFF_FF000000L | _getInt(buffer, index, 3);
        case 4: return 0xFFFFFFFF_00000000L | _getInt(buffer, index, 4);
        case 5: return 0xFFFFFF00_00000000L | _getLong(buffer, index, 5);
        case 6: return 0xFFFF0000_00000000L | _getLong(buffer, index, 6);
        case 7: return 0xFF000000_00000000L | _getLong(buffer, index, 7);
        case 8: return _getLong(buffer, index, 8);
        default: throw new IllegalArgumentException("len is out of range: " + len);
        }
    }

    public static int len(byte val) {
        if(val == -1)
            return 0;
        return 1;
    }

    public static int len(short val) {
        int n = 0;
        if(val != -1) {
            n = 1;
//            val = (short) (val >>> Byte.SIZE); // ICAST_QUESTIONABLE_UNSIGNED_RIGHT_SHIFT
            val = (short) (val >> Byte.SIZE); // high bytes chopped off either way, see above
            if (val != -1) {
                return 2;
            }
        }
        return n;
    }

    public static int len(int val) {
        int n = 0;
        if(val != -1) {
            n = 1;
            val = val >> Byte.SIZE;
            if (val != -1) {
                n = 2;
                val = val >> Byte.SIZE;
                if (val != -1) {
                    n = 3;
                    val = val >> Byte.SIZE;
                    if (val != -1) {
                        return 4;
                    }
                }
            }
        }
        return n;
    }

    public static int len(long val) {
        int n = 0;
        if(val != -1) {
            n = 1;
            val = val >> Byte.SIZE;
            if (val != -1) {
                n = 2;
                val = val >> Byte.SIZE;
                if (val != -1) {
                    n = 3;
                    val = val >> Byte.SIZE;
                    if (val != -1) {
                        n = 4;
                        val = val >> Byte.SIZE;
                        if (val != -1) {
                            n = 5;
                            val = val >> Byte.SIZE;
                            if (val != -1) {
                                n = 6;
                                val = val >> Byte.SIZE;
                                if (val != -1) {
                                    n = 7;
                                    val = val >> Byte.SIZE;
                                    if (val != -1) {
                                        return 8;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return n;
    }

    /**
     * Will always return {@link Long#SIZE} for non-negative integers. See also {@link Integers#bitLen(long)}.
     * @param val
     * @return
     */
    public static int bitLen(long val) {
        return Long.SIZE - Long.numberOfLeadingZeros(~val);
    }

//    private static void testNegativeLongs(long start, long end) throws Exception {
//        byte[] dest = new byte[10];
//        for (long i = start; i < end; i++) {
//            int n = putLong(i, dest, 1);
//            if(n != len(i)) throw new Exception("len doesn't match");
//            long result = getLong(dest, 1, n);
//            if(i != result) {
//                System.out.println(Hex.toHexString(Integers.toBytes(i)) + " vs \n" + Hex.toHexString(Integers.toBytes(result)));
//                throw new Exception(i + " != " + result);
//            }
//        }
//    }
//
//    private static class CustomThread extends Thread {
//
//        private long start, end;
//
//        public CustomThread(long start, long end) {
//            System.out.println(Hex.toHexString(Integers.toBytes(start)) + " --> " + Hex.toHexString(Integers.toBytes(end)));
//            this.start = start;
//            this.end = end;
//        }
//
//        @Override
//        public void run() {
//            try {
//                testNegativeLongs(start, end);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static void testMultithreaded(long veryStart, long finalEnd) throws Exception {
//
//        long len = finalEnd - veryStart;
//        long subLen = len / 8;
//
//        Thread[] threads = new Thread[7];
//
//        long nextStart = veryStart;
//        for (int i = 0; i < 7; i++) {
//            long end = nextStart + subLen;
//            Thread t = new CustomThread(nextStart, end);
//            t.start();
//            threads[i] = t;
//
//            nextStart = end;
//        }
//
//        System.out.println(Hex.toHexString(Integers.toBytes(nextStart)) + " --> " + Hex.toHexString(Integers.toBytes(finalEnd)));
//        testNegativeLongs(nextStart, finalEnd);
//
//        for (Thread t : threads) {
//            t.join();
//        }
//    }
//
//    public static void main(String[] args0) throws Exception {
//          if(true)return;
////        System.out.println((short) (byte) -2);
//
////        for(int i = 0; i < 256; i++) {
////            byte b = (byte) i;
////            System.out.println(b);
////            System.out.println(0xFFL & b);
////        }
//
////        if(true) return;
//
////        System.out.println(getShort(toBytes((short) 16), 0, 2));
////        System.out.println(getShort(toBytes((short) -16), 0, 1));
//
//        System.out.println("0x" + Hex.toHexString(toBytes(-256L)));
//        System.out.println("0x" + Hex.toHexString(toBytes(-16L)));
//        System.out.println("0x" + Hex.toHexString(toBytes(-1L)));
//        System.out.println("0x" + Hex.toHexString(toBytes(0L)));
//        System.out.println("0x" + Hex.toHexString(toBytes(1L)));
//        System.out.println("0x" + Hex.toHexString(toBytes(16L)));
//
//        testNegativeLongs(Long.MIN_VALUE / 256 + 10_000_000, Long.MIN_VALUE / 256 + 100_000_000);
//
//        byte[] x = new byte[8];
//        for (int i = 0; i < 20; i++) {
//            Arrays.fill(x, (byte) 0);
//            int n = putLong(i, x, 0);
//            System.out.print(n + " " + Hex.toHexString(x) + " ");
//            long lo = getLong(x, 0, n);
//            System.out.println(lo);
//        }
//
//        for (byte i = -3; i < 3; i++) {
//            byte[] z = toBytes(i);
//            System.out.println(i + " " + Hex.toHexString(z) + " " + getByte(z, 0, z.length));
//        }
//
//        for (short i = -3; i < 3; i++) {
//            byte[] z = toBytes(i);
//            System.out.println(i + " " + Hex.toHexString(z) + " " + getShort(z, 0, z.length));
//        }
//
//        for (int i = -3; i < 3; i++) {
//            byte[] z = toBytes(i);
//            System.out.println(i + " " + Hex.toHexString(z) + " " + getInt(z, 0, z.length));
//        }
//
//        for (long i = -3; i < 3; i++) {
//            byte[] z = toBytes(i);
//            System.out.println(i + " " + Hex.toHexString(z) + " " + getLong(z, 0, z.length));
//        }
//
//        long veryStart = 0xFFFF_FFF0_0000_0000L;
//        long veryEnd = 0xFFFF_FFFF_F000_0000L;
//
//        System.out.println(veryEnd - veryStart);
//
//        System.out.println(Hex.toHexString(Integers.toBytes(Short.MAX_VALUE)));
//        System.out.println(Hex.toHexString(Integers.toBytes((short) Byte.MAX_VALUE)));
//        System.out.println(Hex.toHexString(Integers.toBytes((short) Byte.MIN_VALUE)));
//        System.out.println(Hex.toHexString(Integers.toBytes(Short.MIN_VALUE)));
//
//        System.out.println(Hex.toHexString(toBytes(Short.MAX_VALUE)));
//        System.out.println(Hex.toHexString(toBytes((short) Byte.MAX_VALUE)));
//        System.out.println(Hex.toHexString(toBytes((short) Byte.MIN_VALUE)));
//        System.out.println(Hex.toHexString(toBytes(Short.MIN_VALUE)));
//
////        System.out.println(Hex.toHexString(Integers.toBytes(Long.MAX_VALUE)));
////        System.out.println(Hex.toHexString(Integers.toBytes((long) Integer.MAX_VALUE)));
////        System.out.println(Hex.toHexString(Integers.toBytes((long) Short.MAX_VALUE)));
////        System.out.println(Hex.toHexString(Integers.toBytes((long) Byte.MAX_VALUE)));
////        System.out.println(Hex.toHexString(Integers.toBytes((long) Byte.MIN_VALUE)));
////        System.out.println(Hex.toHexString(Integers.toBytes((long) Short.MIN_VALUE)));
////        System.out.println(Hex.toHexString(Integers.toBytes((long) Integer.MIN_VALUE)));
////        System.out.println(Hex.toHexString(Integers.toBytes(Long.MIN_VALUE)));
////
////        System.out.println(Hex.toHexString(toBytes(Long.MAX_VALUE)));
////        System.out.println(Hex.toHexString(toBytes((long) Integer.MAX_VALUE)));
////        System.out.println(Hex.toHexString(toBytes((long) Short.MAX_VALUE)));
////        System.out.println(Hex.toHexString(toBytes((long) Byte.MAX_VALUE)));
////        System.out.println(Hex.toHexString(toBytes((long) Byte.MIN_VALUE)));
////        System.out.println(Hex.toHexString(toBytes((long) Short.MIN_VALUE)));
////        System.out.println(Hex.toHexString(toBytes((long) Integer.MIN_VALUE)));
////        System.out.println(Hex.toHexString(toBytes(Long.MIN_VALUE)));
//    }
}
