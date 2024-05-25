package binary;

import java.util.*;

/**
 * Responsavel por algumas conversoes binárias
 *
 * @author douglas
 */
public class Binario {


    private static char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private static final long UNSIGNED_BASE = (long) 0x7FFFFFFF + (long) 0x7FFFFFFF + (long) 2; // 0xFFFFFFFF+1
    public static String intToBinaryString(int value, int length) {
        char[] result = new char[length];
        int index = length - 1;
        for (int i = 0; i < length; i++) {
            result[index] = (bitValue(value, i) == 1) ? '1' : '0';
            index--;
        }
        return new String(result);
    }
    public static String intToBinaryString(int value) {
        return intToBinaryString(value, 32);
    }
    public static String longToBinaryString(long value, int length) {
        char[] result = new char[length];
        int index = length - 1;
        for (int i = 0; i < length; i++) {
            result[index] = (bitValue(value, i) == 1) ? '1' : '0';
            index--;
        }
        return new String(result);
    }


    public static String longToBinaryString(long value) {
        return longToBinaryString(value, 64);
    }
    public static int binaryStringToInt(String value) {
        int result = value.charAt(0) - 48;
        for (int i = 1; i < value.length(); i++) {
            result = (result << 1) | (value.charAt(i) - 48);
        }
        return result;
    }
    public static long binaryStringToLong(String value) {
        long result = value.charAt(0) - 48;
        for (int i = 1; i < value.length(); i++) {
            result = (result << 1) | (value.charAt(i) - 48);
        }
        return result;
    }
    public static String binaryStringToHexString(String value) {
        int digits = (value.length() + 3) / 4;
        char[] hexChars = new char[digits + 2];
        int position, result, pow, rep;
        hexChars[0] = '0';
        hexChars[1] = 'x';
        position = value.length() - 1;
        for (int digs = 0; digs < digits; digs++) {
            result = 0;
            pow = 1;
            rep = 0;
            while (rep < 4 && position >= 0) {
                if (value.charAt(position) == '1') {
                    result = result + pow;
                }
                pow *= 2;
                position--;
                rep++;
            }
            hexChars[digits - digs + 1] = chars[result];
        }
        return new String(hexChars);
    }

    public static String hexStringToBinaryString(String value) {
        String result = "";
        if (value.indexOf("0x") == 0 || value.indexOf("0X") == 0) {
            value = value.substring(2);
        }
        for (int digs = 0; digs < value.length(); digs++) {
            switch (value.charAt(digs)) {
                case '0':
                    result += "0000";
                    break;
                case '1':
                    result += "0001";
                    break;
                case '2':
                    result += "0010";
                    break;
                case '3':
                    result += "0011";
                    break;
                case '4':
                    result += "0100";
                    break;
                case '5':
                    result += "0101";
                    break;
                case '6':
                    result += "0110";
                    break;
                case '7':
                    result += "0111";
                    break;
                case '8':
                    result += "1000";
                    break;
                case '9':
                    result += "1001";
                    break;
                case 'a':
                case 'A':
                    result += "1010";
                    break;
                case 'b':
                case 'B':
                    result += "1011";
                    break;
                case 'c':
                case 'C':
                    result += "1100";
                    break;
                case 'd':
                case 'D':
                    result += "1101";
                    break;
                case 'e':
                case 'E':
                    result += "1110";
                    break;
                case 'f':
                case 'F':
                    result += "1111";
                    break;
            }
        }
        return result;
    }

    public static char binaryStringToHexDigit(String value) {
        if (value.length() > 4) {
            return '0';
        }
        int result = 0;
        int pow = 1;
        for (int i = value.length() - 1; i >= 0; i--) {
            if (value.charAt(i) == '1') {
                result = result + pow;
            }
            pow *= 2;
        }
        return chars[result];
    }

    public static String intToHexString(int d) {
        String leadingZero = new String("0");
        String leadingX = new String("0x");
        String t = Integer.toHexString(d);
        while (t.length() < 8) {
            t = leadingZero.concat(t);
        }

        t = leadingX.concat(t);
        return t;
    }
    public static String longToHexString(long value) {
        return binaryStringToHexString(longToBinaryString(value));
    }

    public static String unsignedIntToIntString(int d) {
        return (d >= 0) ? Integer.toString(d) : Long.toString(UNSIGNED_BASE + d);
    }

    public static int stringToInt(String s) throws NumberFormatException {
        String work = new String(s);
        int result = 0;
        try {
            result = Integer.decode(s).intValue();
        } catch (NumberFormatException nfe) {
            work = work.toLowerCase();
            if (work.length() == 10 && work.startsWith("0x")) {
                String bitString = "";
                int index;
                for (int i = 2; i < 10; i++) {
                    index = Arrays.binarySearch(chars, work.charAt(i));
                    if (index < 0) {
                        throw new NumberFormatException();
                    }
                    bitString = bitString + intToBinaryString(index, 4);
                }
                result = binaryStringToInt(bitString);
            } else {
                throw new NumberFormatException();
            }
        }
        return result;
    }
    public static long stringToLong(String s) throws NumberFormatException {
        String work = new String(s);
        long result = 0;
        try {
            result = Long.decode(s).longValue();
        } catch (NumberFormatException nfe) {
            work = work.toLowerCase();
            if (work.length() == 18 && work.startsWith("0x")) {
                String bitString = "";
                int index;
                for (int i = 2; i < 18; i++) {
                    index = Arrays.binarySearch(chars, work.charAt(i));
                    if (index < 0) {
                        throw new NumberFormatException();
                    }
                    bitString = bitString + intToBinaryString(index, 4);
                }
                result = binaryStringToLong(bitString);
            } else {
                throw new NumberFormatException();
            }
        }
        return result;
    }
    public static int highOrderLongToInt(long longValue) {
        return (int) (longValue >> 32); // high order 32 bits
    }
    public static int lowOrderLongToInt(long longValue) {
        return (int) (longValue << 32 >> 32); // low order 32 bits
    }
    public static long twoIntsToLong(int highOrder, int lowOrder) {
        return (((long) highOrder) << 32) | (((long) lowOrder) & 0xFFFFFFFFL);
    }
    public static int bitValue(int value, int bit) {
        return 1 & (value >> bit);
    }
    public static int bitValue(long value, int bit) {

        return (int) (1L & (value >> bit));
    }
    public static int setBit(int value, int bit) {
        return value | (1 << bit);
    }
    public static int clearBit(int value, int bit) {
        return value & ~(1 << bit);
    }
    public static int setByte(int value, int bite, int replace) {
        return value & ~(0xFF << (bite << 3)) | ((replace & 0xFF) << (bite << 3));
    }
    public static int getByte(int value, int bite) {
        return value << ((3 - bite) << 3) >>> 24;
    }
    public static boolean isHex(String v) {
        try {
            try {
                Binario.stringToInt(v);
            } catch (NumberFormatException nfe) {
                try {
                    Binario.stringToLong(v);
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            if ((v.charAt(0) == '-')
                    && 
                    (v.charAt(1) == '0')
                    && (Character.toUpperCase(v.charAt(1)) == 'X')) {
                return true;
            } else if ((v.charAt(0) == '0')
                    && (Character.toUpperCase(v.charAt(1)) == 'X')) {
                return true;
            }
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        }

        return false;
    }
    public static boolean isOctal(String v) {
        try {
            int dontCare = Binario.stringToInt(v);

            if (isHex(v)) {
                return false;
            }
            if ((v.charAt(0) == '-')
                    && 
                    (v.charAt(1) == '0')
                    && (v.length() > 1))
            {
                return true;
            } else if ((v.charAt(0) == '0')
                    && (v.length() > 1))
            {
                return true;
            }
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        } catch (NumberFormatException e) {
            return false;
        }

        return false;
    }

}
