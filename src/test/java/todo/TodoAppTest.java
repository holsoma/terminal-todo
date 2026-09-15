package todo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TodoAppTest {
    @Test void commandsManageTasks() { TodoApp app = new TodoApp(); assertTrue(app.execute("add Write tests").contains("Write tests")); assertTrue(app.execute("list").contains("Write tests")); assertTrue(app.execute("update 1 Write Java tests").contains("Write Java tests")); assertTrue(app.execute("mark 1").contains("[x]")); assertTrue(app.execute("unmark 1").contains("[ ]")); assertTrue(app.execute("delete 1").contains("Deleted")); assertEquals("No tasks.", app.execute("list")); }
    @Test void invalidCommandsAreSafe() { TodoApp app = new TodoApp(); assertTrue(app.execute("delete 99").contains("does not exist")); assertTrue(app.execute("update nope").contains("Usage")); assertTrue(app.execute("wat").contains("Unknown")); }
}

