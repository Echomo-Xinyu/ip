package duke;

public class Duke {

    private static final String filePath = "data/duke.txt";
    private static Storage storage;

    public static void main(String[] args) {
        TaskList taskList = new TaskList();

        storage = new Storage(filePath);
        storage.readResult(taskList);

        // opening
        Ui.greet();

        int statusCode = 1;

        // respond to the input
        while (true) {
            String str = Ui.readInput();
            statusCode = Parser.parseCommand(str, taskList, storage);
            if (statusCode == 0) {
                break;
            }
        }
    }
}
