package com.luchenlabs.fantaskulous.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.commons.io.input.CharSequenceInputStream;
import org.junit.Before;
import org.junit.Test;

import com.luchenlabs.fantaskulous.IPersister;
import com.luchenlabs.fantaskulous.JsonPersister;
import com.luchenlabs.fantaskulous.TodoTxtPersister;
import com.luchenlabs.fantaskulous.controller.MainController;
import com.luchenlabs.fantaskulous.controller.TaskListController;
import com.luchenlabs.fantaskulous.core.C;
import com.luchenlabs.fantaskulous.model.FantaskulousModel;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskContext;
import com.luchenlabs.fantaskulous.model.TaskList;

public class EverythingTest {

    private static final String NAME = "Empty"; //$NON-NLS-1$
    private MainController controller;
    private List<TaskList> taskLists;
    private FantaskulousModel model;
    private final String guid = "64517894-D7D5-11E3-B47E-D62D9A125B5A".toLowerCase();
    private Map<UUID, Task> tasks;

    @Before
    public void setUp() throws Exception {
        model = new FantaskulousModel();
        model.taskLists = new ArrayList<TaskList>();
        model.tasks = new HashMap<UUID, Task>();
        taskLists = model.taskLists;
        tasks = model.tasks;
        controller = new MainController();
    }

    @Test
    public final void testCleanupWithNoCompletedTask() {
        TaskList list = controller.createTaskList(taskLists, NAME);
        List<Task> tasks = list.getTasks();

        assertEquals(0, tasks.size());
        controller.removeAllCompletedTasks(taskLists);
        assertEquals(0, tasks.size());
    }

    @Test
    public final void testCleanupWithOneCompletedTask() {
        TaskList list = controller.createTaskList(taskLists, NAME);
        TaskListController tlc = new TaskListController();
        Task task = tlc.addTask(list, "This is a dummy task");
        List<Task> tasks = list.getTasks();

        controller.removeAllCompletedTasks(taskLists);
        assertEquals(1, tasks.size());

        task.setComplete(true);
        controller.removeAllCompletedTasks(taskLists);
        assertEquals(0, tasks.size());
    }

    @Test
    public final void testCleanupWithTwoCompletedTasks() {
        TaskList list = controller.createTaskList(taskLists, NAME);
        TaskListController tlc = new TaskListController();
        tlc.addTask(list, "I'm complete").setComplete(true);
        tlc.addTask(list, "I'm complete too").setComplete(true);
        tlc.addTask(list, "I'm incomplete");

        List<Task> tasks = list.getTasks();
        assertEquals(3, tasks.size());

        controller.removeAllCompletedTasks(taskLists);
        assertEquals(1, tasks.size());

        controller.removeAllCompletedTasks(taskLists);
        assertEquals(1, tasks.size());
    }

    @Test
    public final void testCreateTaskList() {
        controller.createTaskList(taskLists, NAME);
        TaskList list = taskLists.get(0);
        assertNotNull(list);
        assertEquals(list.getName(), NAME);
    }

    @Test
    public final void testJsonRead() {
        IPersister p = new JsonPersister();

        InputStream sr = new CharSequenceInputStream("{ lists: [{ name: 'moo'}, {tasks: [{}, {}]}] }", "UTF-8");
        List<TaskList> o = p.load(sr).taskLists;

        assertEquals(2, o.size());

        assertEquals("moo", o.get(0).getName());
        assertEquals(0, o.get(0).getTasks().size());

        assertEquals("", o.get(1).getName());
        assertEquals(2, o.get(1).getTasks().size());
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

        controller.createTaskList(taskLists, NAME + '2');
        assertEquals(taskLists.size(), 2);

        assertTrue(controller.removeTaskList(taskLists, NAME + '2'));
        assertEquals(taskLists.size(), 1);

        assertTrue(controller.removeTaskList(taskLists, NAME));
        assertEquals(taskLists.size(), 0);

        assertFalse(controller.removeTaskList(taskLists, NAME));
        assertEquals(taskLists.size(), 0);
    }

    @Test
    public final void testTodoTxtWriteBasic() {
        IPersister p = new TodoTxtPersister();

        Task task1 = new Task();
        task1.setDescription("Uno");
        task1.setPriority(Priority.HIGH);

        Task task2 = new Task();
        task2.setDescription("Dos");
        task2.setComplete(true);

        Task task3 = new Task();
        task3.setDescription("Tres");
        task3.setGUID(guid);

        TaskList list1 = new TaskContext("Jupiter");
        list1.addTask(task1);
        list1.addTask(task3);

        TaskList list2 = new TaskContext(C.EMPTY);
        list2.addTask(task2);

        taskLists.add(list1);
        taskLists.add(list2);

        tasks.put(task1.getGUID(), task1);
        tasks.put(task2.getGUID(), task2);
        tasks.put(task3.getGUID(), task3);

        String line1 = "(B) Uno @Jupiter guid:" + task1.getGUID() + '\n';
        String line2 = "x Dos @ guid:" + task2.getGUID();
        String line3 = "(C) Tres @Jupiter guid:" + guid + '\n';

        OutputStream os = new ByteArrayOutputStream();
        try {
            model.taskLists = taskLists;
            model.tasks = tasks;
            p.save(os, model);
            os.close();
        } catch (IOException e) {
            Assert.fail(e.toString());
        }

        String result = os.toString();
        assertTrue(result.contains(line1));
        assertTrue(result.contains(line2));
        assertTrue(result.contains(line3));
    }

}
