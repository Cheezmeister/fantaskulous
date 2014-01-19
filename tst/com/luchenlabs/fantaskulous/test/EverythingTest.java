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
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.model.TaskLists;

public class EverythingTest {

    private static final String NAME = "Empty"; //$NON-NLS-1$
    private MainController controller;
    private List<TaskList> taskLists;
    private TaskLists listsObject;

    @Before
    public void setUp() throws Exception {
        listsObject = new TaskLists();
        listsObject.lists = new ArrayList<TaskList>();
        taskLists = listsObject.lists;
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
    public final void testMoveToList() {
        controller.createTaskList(taskLists, NAME);
        controller.createTaskList(taskLists, NAME);
        Task task = new Task();
        TaskList oldList = taskLists.get(0);
        oldList.addTask(task);
        TaskList newList = taskLists.get(1);

        assertTrue(oldList.getTasks().contains(task));
        assertFalse(newList.getTasks().contains(task));
        assertTrue(controller.moveTaskToList(task, oldList, newList));
        assertFalse(oldList.getTasks().contains(task));
        assertTrue(newList.getTasks().contains(task));
        assertTrue(controller.moveTaskToList(task, newList, oldList));
        assertTrue(oldList.getTasks().contains(task));
        assertFalse(newList.getTasks().contains(task));
        assertTrue(controller.moveTaskToNextList(task, oldList, taskLists));
        assertFalse(oldList.getTasks().contains(task));
        assertTrue(newList.getTasks().contains(task));

        assertFalse(controller.moveTaskToList(task, oldList, newList));

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
