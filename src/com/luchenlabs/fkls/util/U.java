package com.luchenlabs.fkls.util;

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

import android.content.Context;
import android.util.Log;

import com.luchenlabs.fkls.R;
import com.luchenlabs.fkls.core.C;
import com.luchenlabs.fkls.model.FantaskulousModel;
import com.luchenlabs.fkls.model.Priority;
import com.luchenlabs.fkls.model.SigillyTaskList;
import com.luchenlabs.fkls.model.Task;
import com.luchenlabs.fkls.model.TaskContext;
import com.luchenlabs.fkls.model.TaskList;
import com.luchenlabs.fkls.model.TaskProject;

/**
 * Utilities
 *
 * @author cheezmeister
 *
 */
public class U {

    public static final class Android {
        public static String ex(Context context, Exception e, int resId, Object... args) {
            return context.getString(resId, args) + "\n" //$NON-NLS-1$
                    + context.getString(R.string.fmt_the_exception_thrown_was, e.toString());
        }
    }

    /**
     * Utils for todo.txt
     *
     */
    public static final class Todo {

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
                if (line.length() == 0) continue;

                Log.w("U", "Line: " + line);

                Task task = taskFromTodoTxt(line, model.taskLists);

                // TODO try to preserve input order on write
                model.tasks.put(task.getGUID(), task);
            }
            reader.close();

            return model;
        }

        private static void processKeyValue(Task retVal, String key, String value) {
            if (KEY_GUID.equals(key)) {
                retVal.setGUID(value);
            }
        }

        private static void resolveLists(List<TaskList> oLists, Task task,
                Set<String> projects, Set<String> contexts) {
            boolean unfiled = (projects.size() == 0 && contexts.size() == 0);

            if (unfiled) {
                TaskList list = null;
                for (TaskList l : oLists) {
                    if (l.getName().equals(UNFILED_TITLE)) {
                        list = l;
                        break;
                    }
                }
                if (list == null) {
                    list = new TaskList(UNFILED_TITLE);
                    oLists.add(list);
                }
                list.addTask(task);
                return;
            }

            for (TaskList list : oLists) {
                String name = list.toString();
                if (projects.contains(name)) {
                    projects.remove(name);
                    list.addTask(task);
                } else if (contexts.contains(name)) {
                    contexts.remove(name);
                    list.addTask(task);
                }
            }

            // Create any lists we didn't find
            TaskList list;
            for (String ctx : contexts) {
                list = new TaskContext(ctx.substring(1));
                list.addTask(task);
                oLists.add(list);
            }
            for (String prj : projects) {
                list = new TaskProject(prj.substring(1));
                list.addTask(task);
                oLists.add(list);
            }
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
            final String optionalKeyValuePairs = "((?: \\w+:\\S+)+)"; //$NON-NLS-1$
            // TODO optionalFollowups /YYYY-MM-DD

            // Build bigass nasty regex
            Pattern pattern = Pattern.compile(
                    optionalCompletion + '?' +
                            optionalDate + '?' +
                            optionalPriority + '?' +
                            optionalDate + '?' +
                            text +
                            optionalProjects + '?' +
                            optionalContexts + '?' +
                            optionalKeyValuePairs + '?');
            Matcher o = pattern.matcher(line);

            // Parse the line
            if (!o.matches()) throw new IllegalArgumentException(line);

            // Check for an empty line
            if (o.group(0).length() == 0) return null;

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

            // Projects / contexts
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
                resolveLists(oLists, retVal, projects, contexts);
            }

            if (o.group(8) != null && o.group(8).matches(optionalKeyValuePairs)) {
                String[] pairs = o.group(8).trim().split(C.SPACE);
                for (String pair : pairs) {
                    String[] parts = pair.split(":"); //$NON-NLS-1$
                    processKeyValue(retVal, parts[0], parts[1]);
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

        private static final String UNFILED_TITLE = "(Unfiled)";
        private static final String TODO_LOWEST = "(E) "; //$NON-NLS-1$

        private static final String TODO_LOW = "(D) "; //$NON-NLS-1$

        private static final String TODO_MEDIUM = "(C) "; //$NON-NLS-1$

        private static final String TODO_HIGH = "(B) "; //$NON-NLS-1$

        private static final String TODO_HIGHEST = "(A) "; //$NON-NLS-1$

        private static final String KEY_GUID = "guid"; //$NON-NLS-1$

        private static final String KEY_BLOCKS = "blocks"; //$NON-NLS-1$

        private static final String KEY_BLOCKED_BY = "blocked_by"; //$NON-NLS-1$

    }

}
