package com.se.letscook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.se.letscook.network.BitmapConverter;

import junit.framework.Assert;

import org.junit.Test;

public class BitmapConverterTest
{

    @Test
    public void testHexStringToByteArray()
    {
        //Arrange
        String toTest = "04F2B137C983A67392C75849301D467381DBACF7";
        byte[] expected = new byte[]{4, -14, -79, 55, -55, -125, -90, 115, -110, -57, 88, 73, 48, 29, 70, 115, -127, -37, -84, -9};

        //Act
        byte[] testSolution = BitmapConverter.hexStringToByteArray(toTest);

        //Assert
        Assert.assertTrue(compareByteArrays(expected, testSolution));
    }

    @Test
    public void testByteArrayToHexString()
    {
        //Arrange
        byte[] toTest = new byte[]{4, -14, -79, 55, -55, -125, -90, 115, -110, -57, 88, 73, 48, 29, 70, 115, -127, -37, -84, -9};
        String expected = "04F2B137C983A67392C75849301D467381DBACF7";

        //Act
        String testSolution = BitmapConverter.byteArrayToHexString(toTest);

        //Assert
        Assert.assertEquals(expected, testSolution);
    }

    private static boolean compareByteArrays(byte[] arr1, byte[] arr2)
    {
        if(arr1.length != arr2.length) return false;
        for(int i = 0; i < arr1.length; i++)
        {
            if(arr1[i] != arr2[i]) return false;
        }
        return true;
    }

}
