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

        public static List<TaskList> fromTodoTxt(InputStream input) {
            List<TaskList> retVal = new ArrayList<TaskList>();

            Scanner reader = new Scanner(new InputStreamReader(input));
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.length() == 0)
                    continue;

                // TODO FIXME WRITEME
                // Task task = fromTodoTxt(line, retVal);
            }
            return retVal;
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
        public static Task fromTodoTxt(String line, List<TaskList> oLists) {
            Task t = new Task();
            final String optionalCompletion = "(x )"; //$NON-NLS-1$
            final String optionalDate = "(\\d{4}-\\d{2}-\\d{2})"; //$NON-NLS-1$
            final String optionalPriority = "(\\([A-Z]\\) )"; //$NON-NLS-1$
            final String text = "(.*?)"; //$NON-NLS-1$
            final String optionalProjects = "((?: \\+[\\w_]+)+)"; //$NON-NLS-1$ 
            final String optionalContexts = "((?: @[\\w_]+)+)"; //$NON-NLS-1$
            // TODO optionalFollowups /YYYY-MM-DD

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
                t.setComplete(true);
            } else {
                t.setComplete(false);
            }
            t.setPriority(Priority.NONE);

            // Completion date
            String completionDate = o.group(2);
            if (completionDate != null && completionDate.matches(optionalDate)) {
                t.setDate(DateTime.parse(completionDate));
            }

            // Priority
            String priority = o.group(3);
            if (priority != null && priority.matches(optionalPriority)) {
                t.setPriority(U.Todo.fromTodoPriority(priority.charAt(1)));
            }

            // TODO String creationDate = o.group(4);

            // For now, accept an empty description if metadata is present. I'm
            // not sure there's a use case for it, but rejecting lines like
            // "x (A) @office"
            // would likely do more harm than good
            t.setDescription(o.group(5));

            Set<String> projects = new HashSet<String>();
            if (o.group(6) != null && o.group(6).matches(optionalProjects)) {
                projects.addAll(Arrays.asList(o.group(6).trim().split(" ")));
            }
            Set<String> contexts = new HashSet<String>();
            if (o.group(7) != null && o.group(7).matches(optionalContexts)) {
                contexts.addAll(Arrays.asList(o.group(7).trim().split(" ")));
            }

            // Add task to projects/contexts it belongs to
            if (oLists != null) {
                for (TaskList list : oLists) {
                    String name = list.getName();
                    if (projects.contains(name)) {
                        projects.remove(name);
                        list.getTasks().add(t);
                    } else if (contexts.contains(name)) {
                        contexts.remove(name);
                        list.getTasks().add(t);
                    }
                }

                // Create any lists we didn't find
                for (String ctx : contexts) {
                    TaskList list = new TaskContext(ctx);
                    list.addTask(t);
                    oLists.add(list);
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

        public static CharSequence toTodoTxt(TaskList taskList) {
            StringBuilder sb = new StringBuilder();
            for (Task t : taskList.getTasks()) {
                sb.append(U.Todo.toTodoTxt(t, taskList.getName(), null)).append('\n');
            }
            return sb;
        }

    }

}
