package duke;

import java.time.LocalDate;
import java.util.ArrayList;

public class TaskList {
    ArrayList<Task> taskArrayList;
    enum TaskEnum {
        Todo,
        Deadline,
        Event
    }

    TaskList() {
        this.taskArrayList = new ArrayList<>();
    }

    // Todo: include more support for date string
    private static String parseDateTimeString(String arg) {
        String result = arg;

        return result;
    }

    private static LocalDate parseString2LocalDate(String arg) {
        // current supported format: "2009-10-12"
        String dateString = parseDateTimeString(arg);
        LocalDate res = LocalDate.parse(dateString);
        return res;
    }

    protected void readPreCreatedTask(String line) {
        Task curr;
        boolean isDone;
        LocalDate localDate;
        String description;
        if (line.substring(4, 5).equals("0")) {
            isDone = false;
        } else {
            isDone = true;
        }
        switch (line.substring(0, 1)) {
        case "T":
            description = line.split("\\|", 3)[2];
            description = description.substring(1);
            curr = new Todo(description);
            curr.setDone(isDone);
            this.addTask(curr);
            break;

        case "E":
            description = line.split("\\|", 4)[2];
            description = description.substring(1, description.length()-1);
            localDate = LocalDate.parse(line.split("\\|", 4)[3].substring(1));
            curr = new Event(description, localDate);
            curr.setDone(isDone);
            this.addTask(curr);
            break;

        case "D":
            description = line.split("\\|", 4)[2];
            description = description.substring(1, description.length()-1);
            localDate = LocalDate.parse(line.split("\\|", 4)[3].substring(1));
            curr = new Deadline(description, localDate);
            curr.setDone(isDone);
            this.addTask(curr);
            break;

        default:
            // unrecognized pre-created task
        }
    }

    private void createEvent(TaskEnum taskEnum, String command) throws DukeException{
        String args[];
        Task res;
        switch (taskEnum) {
        case Todo:
            res = new Todo(command);
            break;

        case Deadline:
            args = command.split("/", 2);
            if (args.length != 2) {
                throw new DukeException("Invalid Input");
            }
            res = new Deadline(args[0].trim(), TaskList.parseString2LocalDate(args[1].substring(3)));
            break;

        case Event:
            args = command.split("/", 2);
            if (args.length != 2) {
                throw new DukeException("Invalid Input");
            }
            res = new Event(args[0].trim(), TaskList.parseString2LocalDate(args[1].substring(3)));;
            break;

        default:
            throw new DukeException("Invalid Input");
        }
        this.addTask(res);
        Ui.FormatPrint("Got it. I've added this task:\n" + res.toString());
    }

    private static void TaskStateChangePrint(Task t, boolean b) {
        String res;
        if (b) {
            res = "Nice! I've marked this task as done:\n";
        } else {
            res = "OK, I've marked this task as not done yet:\n";
        }
        Ui.FormatPrint(res + t.toString());
    }


    private void handleDelete(String command) throws DukeException {
        String args[] = command.split(" ", 2);
        int index;
        try {
            index = Integer.parseInt(args[1]) - 1;
        } catch (NumberFormatException e) {
            throw new DukeException("Invalid command");
        }
        Task curr = this.taskArrayList.get(index);
        this.remove(index);
        Ui.FormatPrint("Noted. I've removed this task:\n" + curr.toString()
                + "Now you have " + this.taskArrayList.size() + " tasks in the list.");
    }

    void addTask(Task t) {
        this.taskArrayList.add(t);
    }

    private void remove(int index) {
        ArrayList<Task> arr = this.taskArrayList;
        ArrayList<Task> newArr = new ArrayList<>();
        if(index < 0 || index > arr.size()) {
            return;
        }
        for(int i = 0; i < arr.size(); i++) {
            if(i == index) {
                i++;
            }
            newArr.add(arr.get(i));
        }
    }

    private void handleMarkDoneUndone(String[] command) throws DukeException {

        int index = Integer.parseInt(command[1]) - 1;
        if (index < 0 || index > this.taskArrayList.size() - 1) {
            throw new DukeException("Invalid command");
        }
        Task curr = this.taskArrayList.get(index);
        if (command[0].equals("mark")) {
            if (!curr.isDone) {
                curr.isDone = true;
                TaskStateChangePrint(curr, true);
            }
        } else { // command [0].equals("unmark")
            if (curr.isDone) {
                curr.isDone = false;
                TaskStateChangePrint(curr, false);
            }
        }
    }

    void parseInstructions(String args) throws DukeException{
        String arguments[] = args.split(" ", 2);
        try {
            switch (arguments[0]) {
            case "mark": // same flow as case "unmark"
            case "unmark":
                this.handleMarkDoneUndone(arguments);
                break;

            case "delete":
                this.handleDelete(arguments[1]);
                break;

            case "deadline":
                this.createEvent(TaskEnum.Deadline, arguments[1]);
                break;

            case "todo":
                this.createEvent(TaskEnum.Todo, arguments[1]);
                break;

            case "event":
                this.createEvent(TaskEnum.Event, arguments[1]);
                break;

            default:
                throw new DukeException("Unable to parse query");
            }
        } catch (DukeException e) {
            throw new DukeException("Unable to process query");
        }
    }
}
