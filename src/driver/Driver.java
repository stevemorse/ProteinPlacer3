package driver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import protein.Protein;
import NLP.WebInterrogator;

/**
 * The Driver for the entire program. Calls the ChokedWebInteragator.
 * @author Steve Morse
 * @version 1.0
 */
public class Driver {
		private static boolean debug = true;
		private static final int THREADPOOL_SIZE = 5;
		private static int inputFileNumber = 0;
		
		
	/**
	 * The main method for the entire program
	 * @param args
	 */
	public static void main(String[] args){
		List<Protein> proteinList = new ArrayList<Protein>();	
		//prompt the user
		System.out.println("enter the file number (between 0 and 74 inclusive) to process:");
		//get input file number from user at command line
		Scanner consoleInput = new Scanner(System.in);
		int inputFileInt = 0;
		try{
			inputFileInt = consoleInput.nextInt();
			consoleInput.close();
			if (inputFileInt == -1) {
				System.out.println("you have chosen to run a test");
			}
			else if(inputFileInt < 0 || inputFileInt > 74){
				throw new NumberFormatException("integer entered is not in input range");
			}//if out of range
			inputFileNumber = inputFileInt;
		} catch (NumberFormatException nfe) {
			System.out.println("you entery is not a valid input...Usage: 0 or a positive integer >= 74" + nfe.getMessage());
		}catch(Exception e) {
			System.out.println("general exception on input");
		}
		//last catch
		
		//get and process data for text and GO
		WebInterrogator webInterrogator = new WebInterrogator(proteinList,THREADPOOL_SIZE, inputFileNumber, debug);
		webInterrogator.interrogate();
	}//main
}//class