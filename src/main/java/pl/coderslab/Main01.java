package pl.coderslab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

//import pl.coderslab.ConsoleColors;

public class Main01 {

    static Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) {

        String sourceFileName = "tasks.csv",
                commandAdd = "add",
                commandList = "list",
                commandRemove = "remove",
                commandQuit = "exit",
                commandSave = "save",
                commandYes = "yes",
                commandNo = "no",
                commandBack = "back";
        String[][] tasks = new String[0][];

        boolean quitCheck = false;
        File sourceFile = new File(sourceFileName);

        //Weryfikacja, czy tasks.csv istnieje, monit użytkownika o jego utworzenie, jeżeli nie istnieje

        if (!sourceFile.exists()) {
            boolean choiceMade = false;
            while (!choiceMade) {
                System.out.println("File " + sourceFileName + " does not exist");
                System.out.println("Do you want to create it?");
                System.out.println(formatCommand(commandYes) + "/" + formatCommand(commandNo));
                String[] permittedCommands = {commandYes, commandNo};
                switch (validateCommand(userInput.nextLine(), permittedCommands)) {
                    case 0:
                        try {
                            sourceFile.createNewFile();
                            System.out.println("File " + sourceFileName + " created successfully");
                        } catch (IOException e) {
                            System.out.println("Error while creating file");
                            e.printStackTrace();
                            quitCheck = true;
                        }
                        choiceMade = true;
                        break;
                    case 1:
                        quitCheck = true;
                        choiceMade = true;
                        break;
                    default:
                        System.out.println("Unknown command, please try again");
                        break;
                }

            }

        }

        //próba wczytania tasks.csv i walidacja danych w nim zapisanych, w przypadku wystąpienia błędów monit użytkownika o usunięcie wadliwych rekordów

        if (!quitCheck) {
            int errorLineCounter = 0;
            try (Scanner scanFile = new Scanner(sourceFile)) {
                String line = new String();
                String[] lineElements = new String[3];
                while (scanFile.hasNextLine()) {
                    line = scanFile.nextLine();
                    if (validateLine(line)) {
                        lineElements = line.split(", ", 0);
                        tasks = Arrays.copyOf(tasks, tasks.length + 1);
                        tasks[tasks.length - 1] = new String[3];
                        for (int i = 0; i < 3; i++) {
                            tasks[tasks.length - 1][i] = lineElements[i];
                        }
                    } else {
                        errorLineCounter++;
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error while accessing source file");
                e.printStackTrace();
                quitCheck = true;
            }

            if (errorLineCounter > 0) {
                boolean choiceMade = false;

                while (!choiceMade) {
                    System.out.println("Application encountered errors in " + errorLineCounter + ((errorLineCounter > 1) ? "lines" : "line"));
                    System.out.println("Do you want to remove them and launch application?");
                    System.out.println(formatCommand(commandYes) + "/" + formatCommand(commandNo));
                    String[] permittedCommands = {commandYes, commandNo};

                    switch (validateCommand(userInput.nextLine(), permittedCommands)) {

                        case 0:
                            choiceMade = true;
                            break;
                        case 1:
                            choiceMade = true;
                            quitCheck = true;
                            break;
                        default:
                            System.out.println("Unknown command, please try again");
                            break;
                    }
                }

            }
        }

        if (!quitCheck) {
            System.out.println("File " + sourceFileName + " loaded successfully");
        }

        //podstawowe menu programu

        while (!quitCheck) {
            System.out.println(ConsoleColors.BLUE + "Please select an option:" + ConsoleColors.RESET);
            System.out.println(formatCommand(commandList));
            System.out.println(formatCommand(commandAdd));
            System.out.println(formatCommand(commandRemove));
            System.out.println(formatCommand(commandSave));
            System.out.println(formatCommand(commandQuit));
            String[] permittedCommands = {commandList, commandAdd, commandRemove, commandSave, commandQuit};

            switch (validateCommand(userInput.nextLine(), permittedCommands)) {
                case 0:
                    executeList(tasks);
                    break;
                case 1:
                    tasks = ArrayUtils.add(tasks, executeAdd(commandBack));
                    if ((tasks[tasks.length - 1].length) == 0) {
                        tasks = ArrayUtils.remove(tasks, tasks.length - 1);
                    }
                    break;
                case 2:
                    int removeIndex = executeRemove(tasks, commandBack, commandYes, commandNo);
                    if (removeIndex != -1) {
                        tasks = ArrayUtils.remove(tasks, removeIndex);
                    }
                    break;
                case 3:
                    executeSave(tasks, sourceFileName);
                    break;
                case 4:
                    quitCheck = executeQuit(tasks, sourceFileName, commandYes, commandNo);
                    break;
                default:
                    System.out.println("Unknown command, please try again");
                    break;

            }

        }
        userInput.close();

        System.out.println(ConsoleColors.RED + "Application is shutting down\nGoodbye!" + ConsoleColors.RESET);


    }

    //metoda pobiera wczytanego stringa i tablicę dopuszczalnych komend, zwraca indeks komendy z tablicy komend lub -1 jesli stringa nie ma w tablicy
    public static int validateCommand(String command, String[] permittedCommands) {
        for (int i = 0; i < permittedCommands.length; i++) {
            if (command.toLowerCase().equals(permittedCommands[i].toLowerCase()) || command.toLowerCase().equals(permittedCommands[i].substring(0, 1).toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    //weryfikacja, czy linia wczytana z tasks.csv jest poprawnym rekordem - metoda zwraca true w przypadku poprawności danych
    public static boolean validateLine(String line) {
        String[] lineElements = line.split(", ", 0);
        if (lineElements.length != 3) {
            return false;
        }
        if (!checkDescription(lineElements[0]) || !checkDate(lineElements[1]) || !checkPriority(lineElements[2])) {
            return false;
        }
        return true;

    }

    //weryfikacja poprawności opisu procesu - metoda zwraca true w przypadku poprawności danych
    public static boolean checkDescription(String description) {
        return (description.length() < 30) && (description.length() > 0);
    }

    //weryfikacja poprawności daty - metoda zwraca true w przypadku poprawności danych
    public static boolean checkDate(String date) {
        String[] dateElements = date.split("-");
        if (dateElements.length != 3) {
            return false;
        }
        for (String str : dateElements) {
            if (!NumberUtils.isDigits(str)) {
                return false;
            }
        }
        int year = Integer.parseInt(dateElements[0]), month = Integer.parseInt(dateElements[1]), day = Integer.parseInt(dateElements[2]);
        if (year < 1900 || year > 2100) {
            return false;
        }

        if (month < 1 || month > 12) {
            return false;
        }
        if (day < 1 || day > 31) {
            return false;
        }
        if ((month == 4 || month == 6 || month == 9 || month == 11) && (day == 31)) {
            return false;
        }
        if ((month == 2 && day > 29) || (month == 2 && day == 29 && ((year % 4 != 0) || (year % 100 == 0 && year % 400 != 0)))) {
            return false;
        }

        return true;

    }

    //    weryfikacja poprawności priorytetu procesu - metoda zwraca true w przypadku poprawności danych
    public static boolean checkPriority(String priority) {
        return priority.toLowerCase().equals("true") || priority.toLowerCase().equals("false");
    }

    // metoda listująca procesy
    public static void executeList(String[][] tasks) {
        System.out.println();
        if (tasks.length == 0) {
            System.out.println("No tasks to display");
        } else {
            int maxNumberLength = 2, maxDescriptionLength = 11;
            for (int i = 0; i < tasks.length; i++) {
                if (tasks[i][0].length() > maxDescriptionLength) {
                    maxDescriptionLength = tasks[i][0].length();
                }
                if (String.valueOf(i).length() > maxNumberLength) {
                    maxNumberLength = String.valueOf(i).length();
                }
            }
            System.out.print(StringUtils.center("No", maxNumberLength));
            System.out.print("   ");
            System.out.print(StringUtils.center("Description", maxDescriptionLength));
            System.out.print(" ");
            System.out.print(StringUtils.center("Date", 10));
            System.out.print(" ");
            System.out.print("Priority\n");
            String[] date;
            for (int i = 0; i < tasks.length; i++) {
                System.out.print(StringUtils.center(String.valueOf(i), maxNumberLength));
                System.out.print(" : ");
                System.out.print(StringUtils.center(tasks[i][0], maxDescriptionLength));
                System.out.print(" ");
                date = tasks[i][1].split("-");
                if (date[1].length() == 1) {
                    date[1] = "0" + date[1];
                }
                if (date[2].length() == 1) {
                    date[2] = "0" + date[2];
                }
                System.out.print(date[0] + "-" + date[1] + "-" + date[2] + " ");
                System.out.print(StringUtils.center(tasks[i][2], 8) + "\n");
            }
            System.out.println();

        }
    }

    // metoda odpowiedzialna za dodanie nowego procesu - zwraca tablicę będącą nowym rekordem lub pustą tablicę w przypadku wycofania się z dodania danych
    public static String[] executeAdd(String commandBack) {
        String[] newRecord = new String[3];
        String[] permittedCommands = {commandBack};
        boolean goingBack = false;
        System.out.println("Enter required fields or enter " + formatCommand(commandBack) + " at any time to return to main menu");
        while (!goingBack) {
            System.out.println("Type in process description");
            newRecord[0] = userInput.nextLine();
            if (validateCommand(newRecord[0], permittedCommands) == 0) {
                goingBack = true;
            }
            if (checkDescription(newRecord[0])) {
                break;
            } else {
                System.out.println("Description too long or empty");
            }
        }
        while (!goingBack) {
            System.out.println("Type in process expiration date using format YYYY-MM-DD, where year is between 1900 and 2100");
            newRecord[1] = userInput.nextLine();
            if (validateCommand(newRecord[1], permittedCommands) == 0) {
                goingBack = true;
            }
            if (checkDate(newRecord[1])) {
                break;
            } else {
                System.out.println("Incorrect date format");
            }
        }

        while (!goingBack) {
            System.out.println("Type in process priority true/false");
            newRecord[2] = userInput.nextLine();
            if (validateCommand(newRecord[2], permittedCommands) == 0) {
                goingBack = true;
            }
            if (checkPriority(newRecord[2])) {
                break;
            } else {
                System.out.println("Incorrect priority value");
            }
        }

        if (goingBack) {
            newRecord = Arrays.copyOf(newRecord, 0);
        }

        return newRecord;

    }

    //metoda odpowiedzialna za wskazanie indeksu usuwanego procesu, zwraca -1 jesli użytkownik zrezygnuje z usunięcia procesu
    public static int executeRemove(String[][] tasks, String commandBack, String commandYes, String commandNo) {

        boolean goingBack = false;
        String command = new String();
        String[] permittedCommandsSet1 = {commandBack},
                permittedCommandsSet2 = {commandYes, commandNo};
        while (!goingBack) {
            System.out.println("Type in index of the process you wish to remove or enter " + formatCommand(commandBack) + " to return to main menu");
            command = userInput.nextLine();
            if (validateCommand(command, permittedCommandsSet1) == 0) {
                goingBack = true;
            } else if (NumberUtils.isDigits(command)) {
                if (Integer.parseInt(command) >= 0 && Integer.parseInt(command) < tasks.length) {
                    boolean choiceMade = false;
                    while (!choiceMade) {
                        System.out.println("Are you sure want to remove following process:");
                        System.out.println(Integer.parseInt(command) + " : " + tasks[Integer.parseInt(command)][0] + " " + tasks[Integer.parseInt(command)][1] + " " + tasks[Integer.parseInt(command)][2]);
                        System.out.println(formatCommand(commandYes) + "/" + formatCommand(commandNo));

                        switch (validateCommand(userInput.nextLine(), permittedCommandsSet2)) {
                            case 0:
                                return Integer.parseInt(command);
                            case 1:
                                choiceMade = true;
                                break;
                            default:
                                System.out.println("Unknown command, please try again");
                                break;
                        }
                    }

                } else {
                    System.out.println("Index out of bounds");
                }
            } else {
                System.out.println("Enter correct index");
            }
        }

        return -1;

    }

    //metoda odpowiedzialna za zapis tablicy procesów to tasks.csv
    public static void executeSave(String[][] tasks, String sourceFileName) {

        try (FileWriter fw = new FileWriter(sourceFileName, false)) {
            for (String[] task : tasks) {
                fw.append(StringUtils.join(task, ", ") + "\n");
            }
            System.out.println("Tasks saved successfully");
        } catch (IOException e) {
            System.out.println("Error while accessing file, tasks have not been saved");
        }
    }

    //metoda weryfikująca zamiar zakończenia działalnia programu, zwraca true jeśli użytkownik potwierdzi wyłączenie programu
    public static boolean executeQuit(String[][] tasks, String sourceFileName, String commandYes, String commandNo) {

        boolean quitCheck = false, choiceMade = false;
        String[] permittedCommands = {commandYes, commandNo};
        while (!choiceMade) {
            System.out.println("Are you sure you want to exit application?");
            System.out.println(formatCommand(commandYes) + "/" + formatCommand(commandNo));

            switch (validateCommand(userInput.nextLine(), permittedCommands)) {
                case 0:
                    quitCheck = true;
                    choiceMade = true;
                    break;
                case 1:
                    choiceMade = true;
                    break;
                default:
                    System.out.println("Unknown command, please try again");
                    break;
            }
        }
        if (quitCheck) {
            choiceMade = false;

            while (!choiceMade) {
                System.out.println("Do you want to save tasks before quitting?");
                System.out.println(formatCommand(commandYes) + "/" + formatCommand(commandNo));
                switch (validateCommand(userInput.nextLine(), permittedCommands)) {
                    case 0:
                        executeSave(tasks, sourceFileName);
                        choiceMade = true;
                        break;
                    case 1:
                        choiceMade = true;
                        break;
                    default:
                        System.out.println("Unknown command, please try again");
                        break;
                }

            }
        }
        return quitCheck;

    }

    //metoda formatująca wyświetlane komendy
    static public String formatCommand(String command) {
        return (ConsoleColors.YELLOW_UNDERLINED + command.substring(0, 1).toUpperCase() + ((command.length() > 1) ? (ConsoleColors.YELLOW + command.substring(1).toLowerCase()) : "") + ConsoleColors.RESET);
    }


}
