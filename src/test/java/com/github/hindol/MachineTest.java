package com.github.hindol;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class MachineTest {

    @Test
    public void testReset() throws Exception {
        Machine<String, String> machine = new Machine.Builder<String, String>()
                .addTransition("0S", "1R", "1S")
                .addTransition("1S", "BUY", "COMPLETED")
                .addTerminalState("COMPLETED")
                .build("0S"); // Initial state

        machine.process("1R");
        String current = machine.peek();
        machine.reset();
        String postReset = machine.peek();

        assertNotEquals(current, postReset);
        assertEquals("0S", postReset);
    }

    @Test
    public void testProcess() throws Exception {
        Machine<String, String> machine = new Machine.Builder<String, String>()
                .addTransition("0S", "1R", "1S")
                .addTransition("1S", "1R", "2S")
                .addTransition("2S", "1R", "3S")
                .addTransition("3S", "BUY", "COMPLETED")
                .addTerminalState("COMPLETED")
                .build("0S"); // Initial state

        machine.process("1R", "1R", "1R", "BUY");
        assertEquals(machine.peek(), "COMPLETED");
    }

    @Test
    public void testPeek() throws Exception {
        Machine<String, String> machine = new Machine.Builder<String, String>()
                .addTransition("0S", "1R", "1S")
                .addTransition("1S", "BUY", "COMPLETED")
                .addTerminalState("COMPLETED")
                .build("0S"); // Initial state

        assertEquals(machine.peek(), "0S");
        machine.process("1R");
        assertEquals(machine.peek(), "1S");
    }

    @Test
    public void testIsTerminated() throws Exception {
        Machine<String, String> machine = new Machine.Builder<String, String>()
                .addTransition("0S", "1R", "1S")
                .addTransition("1S", "1R", "2S")
                .addTransition("2S", "1R", "3S")
                .addTransition("3S", "BUY", "COMPLETED")
                .addTerminalState("COMPLETED")
                .build("0S"); // Initial state

        machine.process("1R", "1R", "1R", "BUY");
        assertTrue(machine.isTerminated());
    }
}