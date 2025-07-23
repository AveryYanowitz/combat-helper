package com.tools.dnd;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.tools.dnd.combat_flow.InputHandler;

public class AskUtilsTest {
    private InputHandler input;

    @BeforeAll
    void init() {
        input = new InputHandler();
    }

    @Test
    void testGetArray() throws Exception {
        String[] expected = {"foo", "bar", "baz"};
        SystemLambda.withTextFromSystemIn("foo,bar,baz").execute(() -> {
            String[] actual = input.getArray("");
            assertArrayEquals(expected, actual);
        });
    }

    public static enum TestEnum { THIS, IS, A, TEST }
    @Test
    void testGetEnum() throws Exception {
        TestEnum expected = TestEnum.TEST;
        SystemLambda.withTextFromSystemIn("test").execute(() -> {
            TestEnum actual = input.getEnum(TestEnum.class, "");
            assertEquals(expected, actual);
        });
    }

    @Test
    void testGetInt() throws Exception {
        SystemLambda.withTextFromSystemIn("not an int","3").execute(() -> {
            int actual = input.getInt("");
            assertEquals(3, actual);
        });
    }

    @Test
    void testGetIntWithException() throws Exception {
        SystemLambda.withTextFromSystemIn("exception").execute(() -> {
            int actual = input.getInt("", "exception", 42);
            assertEquals(42, actual);
        });
    }

    @Test
    void testGetIntString() throws Exception {
        String expected = "3";
        SystemLambda.withTextFromSystemIn("not an int","3").execute(() -> {
            String actual = input.getIntString("");
            assertEquals(expected, actual);
        });
    }

    @Test
    void testGetString() throws Exception {
        String expected = "expected";
        SystemLambda.withTextFromSystemIn("expected").execute(() -> {
            String actual = input.getString("");
            assertEquals(expected, actual);
        });
    }

    @Test
    void testGetYesNo() throws Exception {
        SystemLambda.withTextFromSystemIn("not a boolean","y").execute(() -> {
            assertTrue(input.getYesNo(""));
        });
    }
}
