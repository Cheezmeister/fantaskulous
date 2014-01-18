package com.luchenlabs.fantaskulous.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.luchenlabs.fantaskulous.controller.MainController;
import com.luchenlabs.fantaskulous.model.TaskList;

public class EverythingTest {

    private static final String NAME = "Empty"; //$NON-NLS-1$
    private MainController controller;
    private List<TaskList> taskLists;

    @Before
    public void setUp() throws Exception {
        taskLists = new ArrayList<TaskList>();
        controller = new MainController();
    }

    @Test
    public final void testCreateTaskList() {
        controller.createTaskList(taskLists, NAME);
        TaskList list = taskLists.get(0);
        assertNotNull(list);
        assertEquals(list.getName(), NAME);
    }

    @Test
    public final void testRemoveNonexistentTaskList() {
        assertFalse(controller.removeTaskList(taskLists, "I don't exist")); //$NON-NLS-1$
    }

    @Test
    public final void testRemoveTaskList() {
        controller.createTaskList(taskLists, NAME);
        assertEquals(taskLists.size(), 1);

        controller.createTaskList(taskLists, NAME);
        assertEquals(taskLists.size(), 2);

        assertTrue(controller.removeTaskList(taskLists, NAME));
        assertEquals(taskLists.size(), 1);

        assertTrue(controller.removeTaskList(taskLists, NAME));
        assertEquals(taskLists.size(), 0);

        assertFalse(controller.removeTaskList(taskLists, NAME));
        assertEquals(taskLists.size(), 0);
    }

}
