package com.luchenlabs.fantaskulous;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.luchenlabs.fantaskulous.model.ITaskList;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.model.Task;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

/**
 * Utilities
 * 
 * @author cheezmeister
 * 
 */
public class U {

    public static final class Todo {

        private static final String TODO_LOW = "(H) "; //$NON-NLS-1$
        private static final String TODO_MEDIUM = "(G) "; //$NON-NLS-1$
        private static final String TODO_HIGH = "(F) "; //$NON-NLS-1$
        private static final String KEY_GUID = "guid"; //$NON-NLS-1$

        static Priority fromTodoPriority(char letter) {
            if (letter == TODO_HIGH.charAt(1)) { return Priority.HIGH; }
            if (letter == TODO_MEDIUM.charAt(1)) { return Priority.MEDIUM; }
            if (letter == TODO_LOW.charAt(1)) { return Priority.LOW; }
            return Priority.NONE;
        }

        public static List<ITaskList> fromTodoTxt(InputStream input) {
            List<ITaskList> retVal = new ArrayList<ITaskList>();

            Scanner reader = new Scanner(new InputStreamReader(input));
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.length() == 0)
                    continue;

                Task task = fromTodoTxt(line, retVal);
            }
            return retVal;
        }

        /**
         * Read a line from todo.txt, construct a task and add it to any lists
         * 
         * @param line
         *            The input line
         * @param oLists
         *            Existing {@link ITaskList}s. May be null. If the lists
         *            this task belongs to don't exist they will be created.
         * 
         * @return
         */
        public static Task fromTodoTxt(String line, List<ITaskList> oLists) {
            Task t = new Task();
            String optionalCompletion = "(x )?"; //$NON-NLS-1$
            String optionalPriority = "(\\([A-Z]\\) )?"; //$NON-NLS-1$
            String optionalDate = "(\\d{4}-\\d{2}-\\d{2})?"; //$NON-NLS-1$
            String text = "(.*)"; //$NON-NLS-1$
            String optionalProjects = "( \\+[\\w_]+)*"; //$NON-NLS-1$
            String optionalContexts = "( @[\\w_]+)*"; //$NON-NLS-1$
            // TODO optionalFollowups /YYYY-MM-DD
            Pattern pattern = Pattern.compile(
                    optionalCompletion +
                            optionalDate +
                            optionalPriority +
                            optionalDate +
                            text +
                            optionalProjects +
                            optionalContexts);

            Matcher o = pattern.matcher(line);

            if (!o.matches())
                throw new IllegalArgumentException(line);

            if (o.group(0).length() == 0)
                return null;

            String complete = o.group(1);
            if (complete != null && complete.matches("x ")) {
                t.setComplete(true);
            } else {
                t.setComplete(false);
            }
            t.setPriority(Priority.NONE);

            String completionDate = o.group(2);
            if (completionDate != null) {
                t.setDate(DateTime.parse(completionDate));
            }

            String priority = o.group(3);
            if (priority != null && priority.matches("^\\([A-Z]\\)$")) {
                t.setPriority(U.Todo.fromTodoPriority(priority.charAt(1)));
            }
            String creationDate = o.group(4);

            String description = o.group(5);
            t.setDescription(description);

            Set<String> projects = null;
            if (o.group(6) != null) {
                projects = new HashSet<String>(Arrays.asList(o.group(6).trim().split(" ")));
            }
            Set<String> contexts = null;
            if (o.group(7) != null) {
                contexts = new HashSet<String>(Arrays.asList(o.group(7).trim().split(" ")));
            }

            // Add task to projects/contexts it belongs to
            if (oLists != null) {
                for (ITaskList list : oLists) {
                    String name = list.getName();
                    if (projects.contains(name)) {
                        projects.remove(name);
                        list.getTasks().add(t);
                    } else if (contexts.contains(name)) {
                        contexts.remove(name);
                        list.getTasks().add(t);
                    }
                }

                // Add any lists we didn't find
                for (String ctx : contexts) {
                    TaskList context = new TaskContext(ctx);
                    context.addTask(t);
                    oLists.add(context);
                }
                for (String prj : projects) {
                    TaskProject project = new TaskProject(prj);
                    project.addTask(t);
                    oLists.add(project);
                }
            }

            return t;
        }

        private static String getTodoPriority(Priority p) {
            switch (p) {
                case HIGH:
                    return TODO_HIGH;
                case MEDIUM:
                    return TODO_MEDIUM;
                case LOW:
                    return TODO_LOW;
                case NONE:
                default:
                    return C.EMPTY;
            }
        }

        public static CharSequence toTodoTxt(TaskList taskList) {
            StringBuilder sb = new StringBuilder();
            for (Task t : taskList.getTasks()) {
                sb.append(U.Todo.toTodoTxt(t, taskList.getName(), null)).append('\n');
            }
            return sb;
        }

        public static CharSequence toTodoTxt(Task task, String context, String project) {
            StringBuilder sb = new StringBuilder()
                    .append(Todo.getTodoPriority(task.getPriority()))
                    .append(task.isComplete() ? "x " : C.EMPTY) //$NON-NLS-1$
                    .append(task.getDescription()).append(' ');

            if (project != null && project.length() > 0) {
                sb.append('+' + project).append(' ');
            }

            if (context != null && context.length() > 0) {
                sb.append('@' + context).append(' ');
            }

            sb.append(KEY_GUID + ':' + task.getGUID());

            return sb;
        }

    }

}
