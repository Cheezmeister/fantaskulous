package com.luchenlabs.fantaskulous.util;

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

import com.luchenlabs.fantaskulous.core.C;
import com.luchenlabs.fantaskulous.model.FantaskulousModel;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.SigillyTaskList;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskContext;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.model.TaskProject;

/**
 * Utilities
 * 
 * @author cheezmeister
 * 
 */
public class U {

    /** Utils for todo.txt */
    public static final class Todo {

        private static final String TODO_LOWEST = "(E) "; //$NON-NLS-1$
        private static final String TODO_LOW = "(D) "; //$NON-NLS-1$
        private static final String TODO_MEDIUM = "(C) "; //$NON-NLS-1$
        private static final String TODO_HIGH = "(B) "; //$NON-NLS-1$
        private static final String TODO_HIGHEST = "(A) "; //$NON-NLS-1$
        private static final String KEY_GUID = "guid"; //$NON-NLS-1$

        static Priority fromAlphaPriority(char letter) {
            if (letter == TODO_HIGHEST.charAt(1)) { return Priority.HIGHEST; }
            if (letter == TODO_HIGH.charAt(1)) { return Priority.HIGH; }
            if (letter == TODO_MEDIUM.charAt(1)) { return Priority.MEDIUM; }
            if (letter == TODO_LOW.charAt(1)) { return Priority.LOW; }
            if (letter == TODO_LOWEST.charAt(1)) { return Priority.LOWEST; }
            return Priority.NONE;
        }

        private static String getAlphaPriority(Priority p) {
            switch (p) {
                case HIGHEST:
                    return TODO_HIGHEST;
                case HIGH:
                    return TODO_HIGH;
                case MEDIUM:
                    return TODO_MEDIUM;
                case LOW:
                    return TODO_LOW;
                case LOWEST:
                    return TODO_LOWEST;
                case NONE:
                default:
                    return C.EMPTY;
            }
        }

        public static FantaskulousModel modelFromTodoTxt(InputStream input) {
            FantaskulousModel model = new FantaskulousModel();
            model.taskLists = new ArrayList<TaskList>();

            Scanner reader = new Scanner(new InputStreamReader(input));
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.length() == 0)
                    continue;

                Task task = taskFromTodoTxt(line, model.taskLists);

                // TODO try to preserve input order on write
                model.tasks.put(task.getGUID(), task);
            }
            reader.close();

            return model;
        }

        /**
         * Read a line from todo.txt, construct a task and add it to any lists
         * 
         * @param line
         *            The input line
         * @param oLists
         *            Existing {@link TaskList}s. If null, projects and contexts
         *            will be ignored. Any lists not already present in oLists
         *            will be created and appended to it.
         * 
         * @return
         */
        public static Task taskFromTodoTxt(String line, List<TaskList> oLists) {
            Task retVal = new Task();
            final String optionalCompletion = "(x )"; //$NON-NLS-1$
            final String optionalDate = "(\\d{4}-\\d{2}-\\d{2})"; //$NON-NLS-1$
            final String optionalPriority = "(\\([A-Z]\\) )"; //$NON-NLS-1$
            final String text = "(.*?)"; //$NON-NLS-1$
            final String optionalProjects = "((?: \\+[\\w_]+)+)"; //$NON-NLS-1$ 
            final String optionalContexts = "((?: @[\\w_]+)+)"; //$NON-NLS-1$
            // TODO optionalFollowups /YYYY-MM-DD
            // TODO optionalKeyValuePairs

            // Build bigass nasty regex
            Pattern pattern = Pattern.compile(
                    optionalCompletion + '?' +
                            optionalDate + '?' +
                            optionalPriority + '?' +
                            optionalDate + '?' +
                            text +
                            optionalProjects + '?' +
                            optionalContexts + '?');
            Matcher o = pattern.matcher(line);

            // Parse the line
            if (!o.matches())
                throw new IllegalArgumentException(line);

            // Check for an empty line
            if (o.group(0).length() == 0)
                return null;

            // Completion status
            String complete = o.group(1);
            if (complete != null && complete.matches(optionalCompletion)) {
                retVal.setComplete(true);
            } else {
                retVal.setComplete(false);
            }

            // Completion date
            String completionDate = o.group(2);
            if (completionDate != null && completionDate.matches(optionalDate)) {
                retVal.setDate(DateTime.parse(completionDate).toDate());
            }

            // Priority
            retVal.setPriority(Priority.NONE);
            String priority = o.group(3);
            if (priority != null && priority.matches(optionalPriority)) {
                retVal.setPriority(U.Todo.fromAlphaPriority(priority.charAt(1)));
            }

            // TODO String creationDate = o.group(4);

            // For now, accept an empty description if metadata is present. I'm
            // not sure there's a use case for it, but if your todo.txt is full
            // of lines like "x (A) @office", that's your problem.
            retVal.setDescription(o.group(5));

            Set<String> projects = new HashSet<String>();
            if (o.group(6) != null && o.group(6).matches(optionalProjects)) {
                projects.addAll(Arrays.asList(o.group(6).trim().split(C.SPACE)));
            }
            Set<String> contexts = new HashSet<String>();
            if (o.group(7) != null && o.group(7).matches(optionalContexts)) {
                contexts.addAll(Arrays.asList(o.group(7).trim().split(C.SPACE)));
            }

            // Add task to projects/contexts it belongs to
            if (oLists != null) {
                for (TaskList list : oLists) {
                    String name = list.toString();
                    if (projects.contains(name)) {
                        projects.remove(name);
                        list.addTask(retVal);
                    } else if (contexts.contains(name)) {
                        contexts.remove(name);
                        list.addTask(retVal);
                    }
                }

                // Create any lists we didn't find
                TaskList list;
                for (String ctx : contexts) {
                    list = new TaskContext(ctx.substring(1));
                    list.addTask(retVal);
                    oLists.add(list);
                }
                for (String prj : projects) {
                    list = new TaskProject(prj.substring(1));
                    list.addTask(retVal);
                    oLists.add(list);
                }
            }

            return retVal;
        }

        /**
         * Convert a single task to todo.txt format
         * 
         * @param task
         *            The task
         * @return One line in todo.txt format representing the task
         */
        public static CharSequence toTodoTxt(Task task) {
            StringBuilder sb = new StringBuilder()
                    .append(Todo.getAlphaPriority(task.getPriority()))
                    .append(task.isComplete() ? "x " : C.EMPTY) //$NON-NLS-1$
                    .append(task.getDescription()).append(' ');

            // Contexts come first. Why? Contexts are more useful, because I
            // declare it so.
            for (SigillyTaskList context : task.getContexts()) {
                sb.append(context).append(' ');
            }

            // Tack em on.
            for (TaskProject project : task.getProjects()) {
                sb.append(project).append(' ');
            }

            // This is a rather obtuse way to give tasks unique, stable IDs. I
            // should do something more clever instead, but I won't.
            sb.append(KEY_GUID + ':' + task.getGUID());

            return sb;
        }

    }

}
