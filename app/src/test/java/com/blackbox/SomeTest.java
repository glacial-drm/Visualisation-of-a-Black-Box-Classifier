package com.blackbox;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SomeTest {
    @Test
    public void test1() {
        assertTrue(true);
    }

    @Test
    public void test2() {
        assertTrue(true);
    }

    @Test
    public void test3() {
        assertTrue(false);
    }

    @Test
    public void test4() {
        assertTrue(true);
    }

    @Test
    public void test5() {
        assertTrue(false);
    }
}
