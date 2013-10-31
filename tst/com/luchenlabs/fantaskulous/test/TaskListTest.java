/**
 * 
 */
package com.luchenlabs.fantaskulous.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.luchenlabs.fantaskulous.controller.TaskController;
import com.luchenlabs.fantaskulous.controller.TaskListController;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

/**
 * @author cheezmeister
 * 
 */
public class TaskListTest {

    private TaskList list;
    private TaskController controller;
    private TaskListController parent;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        list = new TaskList("Test list");
        list.addTask(new Task(list, "Test task"));
        parent = new TaskListController(list);

    }

    /**
     * Test method for
     * {@link com.luchenlabs.fantaskulous.controller.TaskListController#addTask(java.lang.CharSequence)}
     * .
     */
    @Test
    public final void testAddTask() {
        Task t = (Task) parent.addTask("this is a task");
        assertNotNull(t);
        assertNotNull(parent.getChild(t.getGUID()));
    }

    /**
     * Test method for
     * {@link com.luchenlabs.fantaskulous.controller.TaskListController#getChild(java.util.UUID)}
     * .
     */
    @Test
    public final void testGetChild() {
        Task task = list.getTasks().get(0);
        TaskController child = parent.getChild(task.getGUID());
        assertNotNull(child);
        assertFalse(task.isComplete());
        child.complete(true);
        assertTrue(task.isComplete());
    }

    @Test
    public final void testSanity() {
        // Yay!
    }

}
