/**
 *
 */
package com.luchenlabs.fantaskulous.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.luchenlabs.fantaskulous.controller.TaskListController;
import com.luchenlabs.fantaskulous.model.FantaskulousModel;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskContext;
import com.luchenlabs.fantaskulous.model.TaskList;

/**
 * @author cheezmeister
 *
 */
@SuppressWarnings("nls")
public class TaskListTest {

    private TaskList list;
    private TaskListController parent;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        list = new TaskContext("Test list");
        list.addTask(new Task(list, "Test task"));
        parent = new TaskListController();

    }

    @Test
    public void testAddsSelfToTask() throws Exception {
        list.addTask(new Task());
        for (Task t : list.getTasks()) {
            assertTrue(t.getContexts().contains(list) || t.getProjects().contains(list));
        }
    }

    /**
     * Test method for
     * {@link com.luchenlabs.fantaskulous.controller.TaskListController#addTask(java.lang.CharSequence)}
     * .
     */
    @Test
    public final void testAddTask() {
        FantaskulousModel model = new FantaskulousModel();
        Task t = parent.addTask(model, list, "this is a task");
        assertNotNull(t);
        assertTrue(list.getTasks().contains(t));
        assertTrue(model.tasks.containsValue(t));
    }

    @Test
    public final void testAlwaysHasGuid() {
        assertNotNull(list.getGuid());
    }

    @Test
    public final void testSanity() {
        // Yay!
    }

    @Test
    public void testSetName() {
        String name = "Bananas";
        list.setName(name);
        assertNotNull(list.getName());
        assertEquals(name, list.getName());
    }

}
