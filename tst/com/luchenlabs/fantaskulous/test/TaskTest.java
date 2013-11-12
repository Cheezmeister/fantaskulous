package com.luchenlabs.fantaskulous.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.luchenlabs.fantaskulous.C;
import com.luchenlabs.fantaskulous.controller.TaskController;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;

public class TaskTest {

    private static final String TITLE = "bleh"; //$NON-NLS-1$
    private Task task;
    TaskController controller;

    @Before
    public void setUp() throws Exception {
        task = new Task();
        controller = new TaskController(task);
    }

    @Test
    public final void testChangeDescription() {
        controller.changeDescription(TITLE);
        assertEquals(TITLE, task.getDescription());
        controller.changeDescription(null);
        assertEquals(C.EMPTY, task.getDescription());
        String looong = "Some really ungodly abysmally long string that you would never want to type out on a mobile, Some really ungodly abysmally long string that you would never want to type out on a mobile, Some really ungodly abysmally long string that you would never want to type out on a mobile"; //$NON-NLS-1$
        controller.changeDescription(looong);
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
