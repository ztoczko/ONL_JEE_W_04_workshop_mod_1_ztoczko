import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;

public class Main01{
	
	public static void main (String[] args) {
		
		String sourceFileName = "tasks.csv",
				commandAdd = "add",
				commandList = "list",
				commandRemove = "remove",
				commandQuit = "exit",
				commandSave = "save",
				commandYes = "yes",
				commandNo = "no";
		String[][] tasks = new String[0][];
		boolean quitCheck = false;
		int counter = 0;
		File sourceFile = new File(sourceFileName);

		//Weryfikacja, czy tasks.csv istnieje, monit użytkownika o jego utworzenie, jeżli nie istnieje
		
		if (!sourceFile.exists()) {
			Scanner scanCommand = new Scanner(System.in);
			boolean choiceMade = false;
			while (!choiceMade) {
				System.out.println ("File " + sourceFileName + " does not exist");
				System.out.println ("Do you want to create it?");
				System.out.print (commandYes.charAt(0) + ((commandYes.length() > 1) ? commandYes.substring(1) : ""));
				System.out.println ("/" + commandNo.charAt(0) + ((commandNo.length() > 1) ? commandNo.substring(1) : ""));
				String[] permittedCommands = {commandYes, commandNo};
				switch (validateCommand(scanCommand.nextLine(), permittedCommands)) {
				case 0: 
					try	{
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
			scanCommand.close();
				
			}
			
		}
		
		if (!quitCheck){
			int errorLineCounter = 0;
			try (Scanner scanFile = new Scanner(sourceFileName)){			
				String line = new String();
				String[] lineElements = new String[3];
				while (scanFile.hasNextLine()) {
					line = scanFile.nextLine();
					if (validateLine(line)) {
						lineElements = line.split(", ",0);
						ArrayUtils.add(tasks, lineElements);	
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
				Scanner scanCommand = new Scanner(System.in);
				boolean choiceMade = false;
				while (!choiceMade) {
					System.out.println("Application encountered errors in " + errorLineCounter + ((errorLineCounter > 1) ? "lines" : "line"));
					System.out.println ("Do you want to remove them and launch application?");
					System.out.print (commandYes.charAt(0) + ((commandYes.length() > 1) ? commandYes.substring(1) : ""));
					System.out.println ("/" + commandNo.charAt(0) + ((commandNo.length() > 1) ? commandNo.substring(1) : ""));
					String[] permittedCommands = {commandYes, commandNo};
					switch (validateCommand(scanCommand.nextLine(), permittedCommands)) {
					
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
				scanCommand.close();
				}			
		}
		
		if (!quitCheck) {
			System.out.println("File " + sourceFileName + " loaded successfully");
		}
		
		
//		while (true) {
//			System.out.println("Enter command:");
			
//		}
//		scan.close();
		
//		System.out.println("Hello");
		
	}
	
	
}
