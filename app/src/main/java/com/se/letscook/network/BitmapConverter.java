package com.se.letscook.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Utils-Class to convert the different image-types
 */
public class BitmapConverter
{

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private BitmapConverter()
    {
        // Private constructor for Utils class
    }

    public static Bitmap hexStringToBitmap(String s)
    {
        if(s == null || s.isEmpty()) return null;
        byte[] byteArray = hexStringToByteArray(s);
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static byte[] hexStringToByteArray(String s)
    {
        if (s == null)
        {
            return new byte[0];
        }
        if(s.length() % 2 != 0) return new byte[0];
        char[] charArray = s.toCharArray();
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(charArray[i], 16) << 4)
                    + Character.digit(charArray[i + 1], 16));
        }
        return data;
    }

    public static String bitmapToHexString(Bitmap bitmap)
    {
        byte[] bytes = bitmapToByteArray(bitmap);
        return byteArrayToHexString(bytes);
    }

    public static String byteArrayToHexString(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++)
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap)
    {
        if(bitmap == null) return new byte[0];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
