package com.luchenlabs.fantaskulous.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.luchenlabs.fantaskulous.controller.TaskController;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;

public class TaskTest {

    private Task task;
    TaskController controller;

    @Before
    public void setUp() throws Exception {
        task = new Task();
        controller = new TaskController(task, null);
    }

    @Test
    public final void testComplete() {
        assertFalse(task.isComplete());
        controller.complete(true);
        assertTrue(task.isComplete());
    }

    @Test
    public final void testPriority() {
        Priority old = task.getPriority();
        controller.cyclePriority();
        assertNotEquals(old, task.getPriority());
    }
}
