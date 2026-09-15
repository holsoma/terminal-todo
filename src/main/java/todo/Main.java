package todo;

import java.util.ArrayList;
import java.util.List;
import org.teavm.jso.JSExport;

public final class Main {
    private static final TodoApp APP = new TodoApp();
    private Main() {}
    public static TodoApp createApp() { return new TodoApp(); }

    @JSExport
    public static String execute(String command) { return APP.execute(command); }

    @JSExport
    public static void replaceTasks() { APP.replaceTasks(new ArrayList<>()); }

    @JSExport
    public static void importTask(int id, String description, boolean completed) {
        List<Task> tasks = new ArrayList<>(APP.getTasks());
        tasks.add(new Task(id, description, completed));
        APP.replaceTasks(tasks);
    }

    @JSExport
    public static String exportCsv() {
        StringBuilder csv = new StringBuilder("id,description,completed\n");
        for (Task task : APP.getTasks()) {
            csv.append(task.getId()).append(',')
                    .append(escapeCsv(task.getDescription())).append(',')
                    .append(task.isCompleted()).append('\n');
        }
        return csv.toString();
    }

    private static String escapeCsv(String value) {
        if (value.indexOf(',') >= 0 || value.indexOf('"') >= 0
                || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }
}
