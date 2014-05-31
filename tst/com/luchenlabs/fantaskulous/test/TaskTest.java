package com.luchenlabs.fantaskulous.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.luchenlabs.fantaskulous.controller.TaskController;
import com.luchenlabs.fantaskulous.core.C;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.util.U.Todo;

public class TaskTest {

    private static final String TITLE = "bleh"; //$NON-NLS-1$
    private Task task;
    TaskController controller;

    @Before
    public void setUp() throws Exception {
        task = new Task();
        controller = new TaskController();
    }

    @Test
    public final void testAlwaysHasGuid() {
        Task task = new Task();
        assertNotNull(task.getGUID());

        task = Todo.taskFromTodoTxt("O", null);
        assertNotNull(task.getGUID());

    }

    @Test
    public final void testChangeDescription() {
        controller.changeDescription(task, TITLE);
        assertEquals(TITLE, task.getDescription());
        controller.changeDescription(task, null);
        assertEquals(C.EMPTY, task.getDescription());
        String looong = "Some really ungodly abysmally long string that you would never want to type out on a mobile, Some really ungodly abysmally long string that you would never want to type out on a mobile, Some really ungodly abysmally long string that you would never want to type out on a mobile"; //$NON-NLS-1$
        controller.changeDescription(task, looong);
    }

    @Test
    public final void testComplete() {
        assertFalse(task.isComplete());
        controller.complete(task, true);
        assertTrue(task.isComplete());
    }

    @Test
    public final void testPriority() {
        Priority old = task.getPriority();
        controller.cyclePriority(task);
        assertNotEquals(old, task.getPriority());
    }

    @Test
    public final void testTodoTxtReadComplete() {
        task = Todo.taskFromTodoTxt("x Call mom", null);
        assertNotNull(task);
        assertTrue(task.isComplete());
        assertEquals(Priority.NONE, task.getPriority());
    }

    @Test
    public final void testTodoTxtReadIncomplete() {
        task = Todo.taskFromTodoTxt("", null);
        assertNull(task);

        task = Todo.taskFromTodoTxt("Do stuff", null);
        assertNotNull(task);
        assertEquals("Do stuff", task.getDescription());
        assertEquals(Priority.NONE, task.getPriority());
        assertNotNull(task.getGUID());
    }
}
