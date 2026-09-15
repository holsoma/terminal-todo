package todo;

import org.teavm.jso.JSExport;

public final class Main {
    private static final TodoApp APP = new TodoApp();
    private Main() {}
    public static TodoApp createApp() { return new TodoApp(); }
    @JSExport("execute")
    public static String execute(String command) { return APP.execute(command); }
}
