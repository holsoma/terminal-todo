package todo;

public class Task {
    private final int id;
    private String description;
    private boolean completed;

    public Task(int id, String description, boolean completed) {
        this.id = id;
        this.description = description;
        this.completed = completed;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public void mark() { completed = true; }
    public void unmark() { completed = false; }
    public void updateDescription(String value) { description = value; }

    @Override
    public String toString() {
        return id + ". [" + (completed ? "x" : " ") + "] " + description;
    }
}

