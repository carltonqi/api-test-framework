package com.merico.inftest.commonutils;


import org.testng.Assert;

import java.lang.reflect.Array;

public class InterfaceAssert {

    public static boolean assertEquals(Object actual, Object expected) {

        if (expected != null && expected.getClass().isArray()) {

            return assertArrayEquals(actual, expected);
        }

        if ((expected == null) && (actual == null)) {
            return true;
        }
        if (expected == null ^ actual == null) {
            return false;
        }
        return expected.equals(actual) && actual.equals(expected);

    }

    private static boolean assertArrayEquals(Object actual, Object expected) {

        if (expected == actual) {
            return true;
        }
        if (null == expected) {
            return false;
        }
        if (null == actual) {
            return false;
        }
        //is called only when expected is an array
        if (actual.getClass().isArray()) {
            int expectedLength = Array.getLength(expected);
            if (expectedLength == Array.getLength(actual)) {
                for (int i = 0; i < expectedLength; i++) {
                    Object _actual = Array.get(actual, i);
                    Object _expected = Array.get(expected, i);
                    boolean result = assertEquals(_actual, _expected);
                    if (!result) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
