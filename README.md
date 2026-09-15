# Todo Terminal

Todo Terminal is a small browser todo list with a terminal-style interface. Task logic is written in Java, compiled for the browser with TeaVM, and hosted as static files on GitHub Pages. There is no backend, database, login, or server API.

## Architecture

```text
Browser terminal -> JavaScript bridge -> TeaVM Java -> TodoApp / Task
                                      -> browser localStorage and CSV files
```

`Task` is the data model. `TodoApp` owns the list and parses commands. `Main` exposes the command entry point. `web/app.js` handles browser-only concerns such as buttons, files, downloads, and localStorage.

## Persistence and CSV

`data/tasks.csv` is the committed repository default. `localStorage` is the current working copy for one browser and one site. On the first visit, the default CSV is loaded. Later visits use localStorage, so normal refreshes do not overwrite changes. Exporting does not change GitHub. To update the repository copy, export `tasks.csv`, replace `data/tasks.csv`, then commit it.

```text
repository CSV -> first load -> localStorage -> changes -> Export CSV
                                      |                         |
                                      +-------------------------+-> manually commit
```

Import CSV validates the header `id,description,completed`, integer unique IDs, and `true` or `false` values. Quoted fields may contain commas and quotes. Failed imports leave existing data unchanged. Reset removes local data and loads the repository CSV after confirmation.

## Commands

| Command | Example | Purpose |
|---|---|---|
| `help` | `help` | Show command help |
| `add DESCRIPTION` | `add Buy milk` | Add a task |
| `list` | `list` | Show tasks |
| `update NUMBER DESCRIPTION` | `update 1 Buy bread` | Change a task |
| `delete NUMBER` | `delete 1` | Delete a task |
| `mark NUMBER` | `mark 1` | Complete a task |
| `unmark NUMBER` | `unmark 1` | Make a task incomplete |
| `clear` | `clear` | Clear terminal output only |

## Local development

Requirements: JDK 17, Gradle 8 or the included Gradle wrapper, and a modern browser. Run tests and build with:

```bash
gradle test
gradle generateJavaScript
```

On Windows use `gradlew.bat test` and `gradlew.bat generateJavaScript` when the wrapper is present. The static site is generated in `build/site`. Serve it through a local HTTP server because `fetch` may be blocked for `file://` pages:

```bash
cd build/site
python -m http.server 8000
```

Open `http://localhost:8000/`.

## GitHub Pages deployment

The workflow in `.github/workflows/deploy.yml` runs on every push to `main`, sets up Java 17, runs the TeaVM build, uploads `build/site`, and deploys it with GitHub Pages. In repository Settings > Pages, select GitHub Actions as the source if GitHub asks for a source. The site URL is `https://USERNAME.github.io/terminal-todo/`.

For a manual deployment, run `gradle generateJavaScript` and publish the contents of `build/site` with a Pages branch or Pages-compatible deployment tool.

## Project structure

```text
src/main/java/todo/Task.java       task model
src/main/java/todo/TodoApp.java    commands and business logic
src/main/java/todo/Main.java       TeaVM entry point
src/test/java/todo/TodoAppTest.java logic tests
web/index.html                     page structure
web/style.css                      terminal styling
web/app.js                         browser integration
web/data/tasks.csv                 default data
.github/workflows/deploy.yml       Pages deployment
```

TeaVM is needed because GitHub Pages serves files and does not run Java. TeaVM converts the Java entry point into browser JavaScript. Relative paths keep `data/tasks.csv` working under a project URL.

## Limitations and troubleshooting

Data belongs to the current browser. Clearing site data removes it, and different devices do not synchronise. GitHub Pages cannot modify repository files, so CSV updates require a manual commit. If the CSV does not load, use an HTTP server and check that `data/tasks.csv` is under the generated site. For build failures, check JDK 17 and the TeaVM or Gradle error first. For a Pages 404, check the Actions run, Pages source, and project-relative URL. If local data is corrupt, use Reset or clear the `todo-terminal.tasks.v1` localStorage key.

## Extending the application

Add parsing and logic in `TodoApp`, write tests, update `help`, and update this command table. Keep browser-specific code in `web/app.js`.

## Example session

```text
> add Finish CS assignment
Added task:
1. [ ] Finish CS assignment
> mark 1
Marked task:
1. [x] Finish CS assignment
> list
1. [x] Finish CS assignment
```

## Technologies

Java, TeaVM, Gradle, HTML, CSS, JavaScript, localStorage, CSV, GitHub Pages, and GitHub Actions.

