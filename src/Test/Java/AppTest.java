package com.example;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AppTest {

    @Test
    public void testGetStatus() {
        Application myApp = new Application();
        String result = myApp.getStatus();
        assertEquals("OK", result);
    }
}