package com.luchenlabs.fantaskulous.test;

import com.luchenlabs.fantaskulous.controller.TaskController;
import com.luchenlabs.fantaskulous.core.C;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskContext;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.model.TaskProject;
import com.luchenlabs.fantaskulous.util.U;
import com.luchenlabs.fantaskulous.util.U.Todo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TaskTest {

    private static final String TITLE = "bleh"; //$NON-NLS-1$
    private Task task;
    private TaskController controller;
    private UUID guid;

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
    public void testDiscardPriorityOnComplete() {
        Priority old = task.getPriority();
        assertNotNull(old);
        assertNotSame(Priority.NONE, old);
        task.setComplete(true);
        assertEquals(Priority.NONE, task.getPriority());

    }

    @Test
    public final void testPriority() {
        Priority old = task.getPriority();
        controller.cyclePriority(task);
        assertNotSame(old, task.getPriority());
    }

    @Test
    public final void testTodoTxtReadBasicComplete() {
        Task t = U.Todo.taskFromTodoTxt("x Call mom", null);
        assertNotNull(t);
        assertTrue(t.isComplete());
        assertEquals(Priority.NONE, t.getPriority());
    }

    @Test
    public final void testTodoTxtReadBasicIncomplete() {
        Task t = U.Todo.taskFromTodoTxt("", null);
        assertNull(t);

        t = U.Todo.taskFromTodoTxt("Do stuff", null);
        assertNotNull(t);
        assertEquals("Do stuff", t.getDescription());
        assertEquals(Priority.NONE, t.getPriority());
        assertNotNull(t.getGUID());
    }

    @Test
    public final void testTodoTxtReadComplete() {
        task = Todo.taskFromTodoTxt("x Call mom", null);
        assertNotNull(task);
        assertTrue(task.isComplete());
        assertEquals(Priority.NONE, task.getPriority());
    }

    @Test
    public void testTodoTxtReadCompletedWithPriority() {
        Task t = U.Todo.taskFromTodoTxt("x (B) Check Reddit again", null);
        assertNotNull(t);
        assertEquals(Priority.HIGH, t.getPriority());
    }

    @Test
    public final void testTodoTxtReadIncomplete() {
        task = Todo.taskFromTodoTxt("", null);
        assertNull(task);

        task = Todo.taskFromTodoTxt("Do stuff @dontcare", null);
        assertNotNull(task);
        assertEquals("Do stuff", task.getDescription());
        assertEquals(Priority.NONE, task.getPriority());
        assertNotNull(task.getGUID());
    }

    @Test
    public void testTodoTxtReadWithGuid() throws Exception {
        String desc = "Correct people on the internet";
        guid = UUID.randomUUID();
        task = Todo.taskFromTodoTxt(desc + " guid:" + guid, null);
        assertNotNull(task);
        assertEquals(desc, task.getDescription());
        assertNotNull(task.getGUID());
        assertEquals(guid, task.getGUID());
    }

    @Test
    public final void testTodoTxtReadWithPriority() {
        Task t = U.Todo.taskFromTodoTxt("(B) Do stuff", null);
        assertNotNull(t);
        assertEquals("Do stuff", t.getDescription());
        assertEquals(Priority.HIGH, t.getPriority());

        t = U.Todo.taskFromTodoTxt("(C) Do stuff", null);
        assertNotNull(t);
        assertEquals("Do stuff", t.getDescription());
        assertEquals(Priority.MEDIUM, t.getPriority());

        t = U.Todo.taskFromTodoTxt("(D) Do stuff", null);
        assertNotNull(t);
        assertEquals("Do stuff", t.getDescription());
        assertEquals(Priority.LOW, t.getPriority());

        // IMPORTANT
        t = U.Todo.taskFromTodoTxt("(F) Do stuff", null);
        assertNotNull(t);
        assertEquals("Do stuff", t.getDescription());
        assertEquals(Priority.NONE, t.getPriority());
    }

    @Test
    public final void testTodoTxtReadWithProjectsAndContexts() {
        List<TaskList> lol = new ArrayList<TaskList>();
        assertEquals(0, lol.size());
        Task t = U.Todo.taskFromTodoTxt("Eat food erryday +Gangsta +Alimentation @lunch @wendys", lol);
        assertNotNull(t);
        assertEquals(4, lol.size());
        for (TaskList l : lol) {
            List<Task> tasks = l.getTasks();
            assertEquals(1, tasks.size());
            assertEquals(t, tasks.get(0));
        }

        boolean a = false, b = false, c = false, d = false;
        for (TaskList l : lol) {
            if (l.getName().equals("Gangsta") && l instanceof TaskProject)
                a = true;
            if (l.getName().equals("Alimentation") && l instanceof TaskProject)
                b = true;
            if (l.getName().equals("lunch") && l instanceof TaskContext)
                c = true;
            if (l.getName().equals("wendys") && l instanceof TaskContext)
                d = true;
        }
        assertEquals(true, a);
        assertEquals(true, b);
        assertEquals(true, c);
        assertEquals(true, d);
    }

}
