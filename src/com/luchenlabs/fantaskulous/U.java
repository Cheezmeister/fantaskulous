package com.luchenlabs.fantaskulous;

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
    private static final String TODO_LOW = "(H) "; //$NON-NLS-1$
    private static final String TODO_MEDIUM = "(G) "; //$NON-NLS-1$
    private static final String TODO_HIGH = "(F) "; //$NON-NLS-1$
    private static final String KEY_GUID = "guid"; //$NON-NLS-1$

    private static Priority fromTodoPriority(char letter) {
        if (letter == TODO_HIGH.charAt(1)) { return Priority.HIGH; }
        if (letter == TODO_MEDIUM.charAt(1)) { return Priority.MEDIUM; }
        if (letter == TODO_LOW.charAt(1)) { return Priority.LOW; }
        return Priority.NONE;
    }

    public static Task fromTodoTxt(String string) {
        Task t = new Task();
        String optionalCompletion = "(x )?"; //$NON-NLS-1$
        String optionalPriority = "(\\([A-Z]\\) )?"; //$NON-NLS-1$
        String optionalDate = "(\\d{4}-\\d{2}-\\d{2})?"; //$NON-NLS-1$
        String text = "(.*)"; //$NON-NLS-1$
        String optionalProjects = "( \\+[\\w_]+)*"; //$NON-NLS-1$
        String optionalContexts = "( @[\\w_]+)*"; //$NON-NLS-1$
        Pattern pattern = Pattern.compile(
                optionalCompletion +
                        optionalDate +
                        optionalPriority +
                        optionalDate +
                        text +
                        optionalProjects +
                        optionalContexts);

        Matcher o = pattern.matcher(string);

        if (!o.matches())
            throw new IllegalArgumentException(string);

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
            // TODO
        }

        String priority = o.group(3);
        if (priority != null && priority.matches("^\\([A-Z]\\)$")) {
            t.setPriority(fromTodoPriority(priority.charAt(1)));
        }
        String creationDate = o.group(4);

        String description = o.group(5);
        t.setDescription(description);

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
                .append(getTodoPriority(task.getPriority()))
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
            sb.append(toTodoTxt(t, taskList.getName(), null)).append('\n');
        }
        return sb;
    }

}
