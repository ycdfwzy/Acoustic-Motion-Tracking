package com.example.asoustic_motion_tracking_receiver;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_conv() {
        double[] A = {1,2,3};
        double[] B = {4,5,6,7};
        double[] C = SignalProcessingUtil.conv(A, B);
        for (double c: C)
            System.out.println(c);
        assertEquals(4, 2 + 2);
    }
}