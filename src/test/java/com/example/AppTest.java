package com.example;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {

    @Test
    public void testAdd() {
        App app = new App();
        int result = app.add(2, 3);
        assertEquals(5, result);
    }

    @Test
    public void testHello() {
        App app = new App();
        assertNotNull(app);
    }
}

