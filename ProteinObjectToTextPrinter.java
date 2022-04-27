import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProteinObjectToTextPrinter {
	void printToFile() {
		List<Protein> proteinList = new ArrayList<Protein>();	
		//prompt the user
		System.out.println("enter the file number (between 0 and 74 inclusive) to process:");
		//get input file number from user at command line
		Scanner consoleInput = new Scanner(System.in);
		int inputFileInt = 0;
		try{
			inputFileInt = consoleInput.nextInt();
			consoleInput.close();
			if(inputFileInt < 0 || inputFileInt > 74){
				throw new NumberFormatException("integer entered is not in input range");
			}//if out of range
			inputFileNumber = inputFileInt;
		} catch (NumberFormatException nfe) {
			System.out.println("you entery is not a valid input...Usage: 0 or a positive integer >= 74" + nfe.getMessage());
		}catch(Exception e) {
			System.out.println("general exception on input");
		}
		//last catch
		
		proteinsObjectFile = new File(proteinsOutFileBaseString + inputFileNumber + "/proteinsOut_" + inputFileNumber + ".bin");
		proteinsOutTextFile = new File(proteinsOutFileBaseString + inputFileNumber + "/textOut_" + inputFileNumber + ".txt");
	
		//read in protein objects and then write them as text to outfile
		List<Protein> ProteinList = new ArrayList<Protein>();
		try {
			InputStream fin = new FileInputStream(proteinsObjectFile);
			ois = new ObjectInputStream(fin);
			ProteinWriter = new PrintWriter(new FileWriter(proteinsOutTextFile,true));
			while(true){
				try{
					Object obj = ois.readObject();
					Protein currentProtein = (Protein) obj;
					ProteinList.add(currentProtein);
					ProteinWriter.println(currentProtein.toString() + "\n");
					proteinsRead++;
				} catch (EOFException eofe) {
					ois.close();
					fin.close();
					break;
				}//catch EOFException
			}//while
					
		} catch (ClassNotFoundException cnfe) {
			System.err.println("cannot find protein class on file open: " + cnfe.getMessage());
			cnfe.printStackTrace();
		} catch (FileNotFoundException fnfe) {
			System.err.println("error opening intput file: " + fnfe.getMessage());
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			System.err.println("error opening output stream: " + ioe.getMessage());
			ioe.printStackTrace();
		}//last catch
	}//printToFile
}
