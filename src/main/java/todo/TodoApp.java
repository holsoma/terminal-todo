package todo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TodoApp {
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) return "Type help for available commands.";
        String command = input.trim();
        String[] parts = command.split("\\s+", 2);
        String name = parts[0].toLowerCase();
        String rest = parts.length > 1 ? parts[1].trim() : "";
        switch (name) {
            case "help": return help();
            case "add": return add(rest);
            case "list": return list();
            case "update": return update(rest);
            case "delete": return delete(rest);
            case "mark": return setCompleted(rest, true);
            case "unmark": return setCompleted(rest, false);
            case "clear": return "__CLEAR__";
            default: return "Unknown command: " + name + ". Type help for available commands.";
        }
    }

    private String help() { return "help - show commands\nadd DESCRIPTION - add a task\nlist - show all tasks\nupdate NUMBER DESCRIPTION - edit a task\ndelete NUMBER - delete a task\nmark NUMBER - complete a task\nunmark NUMBER - make a task incomplete\nclear - clear terminal output"; }
    private String add(String description) { if (description.isEmpty()) return "Usage: add DESCRIPTION"; Task task = new Task(nextId++, description, false); tasks.add(task); return "Added task:\n" + task; }
    private String list() { if (tasks.isEmpty()) return "No tasks."; StringBuilder out = new StringBuilder(); for (Task task : tasks) { if (out.length() > 0) out.append('\n'); out.append(task); } return out.toString(); }
    private String update(String rest) { String[] p = rest.split("\\s+", 2); if (p.length < 2 || p[1].trim().isEmpty()) return "Usage: update NUMBER DESCRIPTION"; Task task = find(p[0]); if (task == null) return missing(p[0]); task.updateDescription(p[1].trim()); return "Updated task:\n" + task; }
    private String delete(String value) { Task task = find(value); if (task == null) return missing(value); tasks.remove(task); return "Deleted task:\n" + task; }
    private String setCompleted(String value, boolean completed) { Task task = find(value); if (task == null) return missing(value); if (completed) task.mark(); else task.unmark(); return (completed ? "Marked" : "Unmarked") + " task:\n" + task; }
    private Task find(String value) { try { int id = Integer.parseInt(value); for (Task task : tasks) if (task.getId() == id) return task; } catch (Exception ignored) {} return null; }
    private String missing(String value) { return "Task " + value + " does not exist."; }
    public List<Task> getTasks() { return Collections.unmodifiableList(tasks); }
    public void replaceTasks(List<Task> imported) { tasks.clear(); tasks.addAll(imported); nextId = 1; for (Task task : tasks) nextId = Math.max(nextId, task.getId() + 1); }
}

