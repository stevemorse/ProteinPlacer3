package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class GenebankDBLoader {
	private String inFileBaseStr = "/home/steve/Desktop/ProteinPlacer3/data/features";
	private int loadedaccessionInFile = 0;
	private int flag = 1;
	private List<String> allAccessionIds = null;
	private List<String> allGINumbers = null;
	private List<String> uniqueAccessions = null;
	private int inputStartFileNumber = 0;
	private int inputEndFileNumber = 74;
	
	public List<String> getAllAccessionIds() {
		return allAccessionIds;
	}
	public void setAllAccessionIds(List<String> allAccessionIds) {
		this.allAccessionIds = allAccessionIds;
	}
	public int getLoadedaccessionInFile() {
		return loadedaccessionInFile;
	}
	public void setLoadedaccessionInFile(int loadedaccessionInFile) {
		this.loadedaccessionInFile = loadedaccessionInFile;
	}
	
	public GenebankDBLoader() {}
		
	public static void main(String[] args) {
		GenebankDBLoader loader = new GenebankDBLoader();
		loader.load();
	}//main
	
	public void load() {
		//getFileNums();
		//for loop through file nums
		for(int fileNum = inputStartFileNumber; fileNum <= inputEndFileNumber; fileNum++) {
			String inFileName = inFileBaseStr + fileNum + ".txt";
			if(allAccessionIds == null) {
				allAccessionIds = getallAccessionIds(flag,fileNum);
			}//if allAccessionIds
			if(uniqueAccessions == null) {
				uniqueAccessions = new ArrayList<String>();
			}//if uniqueAccesions
			System.out.println("loaded " + allAccessionIds.size() + " Accessions");		
			long startTime = System.nanoTime();	
			loadedaccessionInFile = 0;
			int accessionInFile = 0;
			String accessionStr = "";
			Connection conn = null;
		    PreparedStatement preparedStatement = null;
			File inFile = new File(inFileName);
			char[] dataBuffer = new char[(int) inFile.length()];
			
			try {
				FileReader fileReader = new FileReader(inFile);
				fileReader.read(dataBuffer);
				fileReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IOException: " + e.getMessage());
				e.printStackTrace();
			}
			
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost/genebank?"
		                        + "user=genebank&password=gene^11^Bank");
				String genebankStr = new String(dataBuffer);
				String[] terms = genebankStr.split(">Feature");
				accessionInFile = terms.length - 1;
				for(int termsCount = 1; termsCount < terms.length; termsCount++) {
					int acessionBegin = terms[termsCount].indexOf("|");
					//System.out.println("acessionBegin is: " + acessionBegin);
					int acessionEnd = terms[termsCount].indexOf(".1|", acessionBegin);
					//System.out.println("acessionEnd is: " + acessionEnd);
					accessionStr = terms[termsCount].substring(acessionBegin + "|".length(), acessionEnd);
					accessionStr.replaceAll("\\p{C}", ""); 
					accessionStr = accessionStr.trim();
					//System.out.println("Accession is: " + accessionStr);
					//System.out.println("length of text is: " + terms[termsCount].length());
					//System.out.println("text: " + terms[termsCount]);
					//check that accession is valid and also not already processed
					if((isInList(allAccessionIds, accessionStr)) && 
							(!isInList(uniqueAccessions, accessionStr))){
						System.out.println("Accession loaded: " + accessionStr);
						preparedStatement = conn.prepareStatement("insert into features values (?, ?)");
						//preparedStatement = conn.prepareStatement("insert into text values (?, ?)");
						//preparedStatement = conn.prepareStatement("insert into genebank values (?, ?)");
						preparedStatement.setString(1, accessionStr);
						preparedStatement.setString(2, terms[termsCount]);
						preparedStatement.executeUpdate();
						loadedaccessionInFile++;
					}//if isInList
					if(!isInList(uniqueAccessions, accessionStr)) {
						uniqueAccessions.add(accessionStr);
					}//
				}//for termsCount
			} catch (Exception e) {
				System.out.println("Exception: " + e.getMessage());
		    } finally {
		        //close();
		    }//try/catch
		    System.out.println("processed file: " + inFileName);
		    System.out.println("Accessions in file: " + accessionInFile);
		    System.out.println("Accessions loaded from file : " + loadedaccessionInFile);
		    long endTime = System.nanoTime();
		    long runTime = endTime - startTime;
		    System.out.println("in " + runTime / 1000000000 + " seconds");
		}//for filenums
	}//load
	
	public void getFileNums() {
		//prompt the user
		System.out.println("enter the start file number (between 0 and 74 inclusive) to process:");
		//get input file number from user at command line
		Scanner consoleInput = new Scanner(System.in);
		int inputStartFileInt = 0;
		int inputEndFileInt = 9;
		try{
			inputStartFileInt = consoleInput.nextInt();
			if(inputStartFileInt < 0 || inputStartFileInt > 74){
				throw new NumberFormatException("integer entered is not in input range");
			}//if out of range
			inputStartFileNumber = inputStartFileInt;
			System.out.println("enter the end file number (between 0 and 74 inclusive) to process:");
			inputEndFileInt = consoleInput.nextInt();
			consoleInput.close();
			if(inputEndFileInt < 0 || inputEndFileInt > 74){
				throw new NumberFormatException("integer entered is not in input range");
			}//if out of range
			inputEndFileNumber = inputEndFileInt;		
		} catch (NumberFormatException nfe) {
			consoleInput.close();
			System.out.println("you entery is not a valid input...Usage: 0 or a positive integer >= 74" + nfe.getMessage());
		}catch(Exception e) {
			System.out.println("general exception on input");
		}
		//last catch
	}//getFileNum
	
	public static boolean isInList(List<String> allAccessionIds, String accessionStr) {
		boolean isInList = false;
		ListIterator<String> allAccessionIdsIter = allAccessionIds.listIterator();
		while(allAccessionIdsIter.hasNext() && !isInList) {
			String currentStr = allAccessionIdsIter.next();
			if(currentStr.compareTo(accessionStr) == 0) {isInList = true;}//if
		}//while allAccessionIdsIter has next	
		return isInList;
	}//isInList
	
	public static List<String> getallAccessionIds (int flag, int FileNum){
		List<String> allAccessions = new ArrayList<String>();
		String proteinDataInFileString = 
			"/home/steve/Desktop/ProteinPlacer3/data/Blast2GoXML/results_";
		
		String blastFileStr = 
				proteinDataInFileString + FileNum + "/blastResult_" + FileNum + ".xml";
		File currentBlastFile = new File(blastFileStr);
		
		char[] dataBuffer = new char[(int) currentBlastFile.length()];
		try {
			FileReader fileReader = new FileReader(currentBlastFile);
			fileReader.read(dataBuffer);
			fileReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			e.printStackTrace();
		}//try/catch
		String oneBlastFileStr = new String(dataBuffer);
		if(flag == 1) {
		List<String> allAccessionInOneBlastFile = getAllAccessionInOneBlastFile(oneBlastFileStr);
		allAccessions.addAll(allAccessionInOneBlastFile);
		}// if flag is 1
		else if(flag == 2) {
		List<String> getAllGINumbersInOneBlastFile = getAllGINumbersInOneBlastFile(oneBlastFileStr);
		}// if flag is 2

		return allAccessions;
	}//getallAccessionIds
	
	public static List<String> getAllAccessionInOneBlastFile(String oneBlastFileStr){
		List<String> allAccessionInOneBlastFile = new ArrayList<String>();
		String[] hits = oneBlastFileStr.split("<Hit_id>");
		for(int hitCount = 1; hitCount < hits.length; hitCount++) {
			//System.out.println("hit:\n" + hits[hitCount]);
			int accessionBegin = hits[hitCount].indexOf("<Hit_accession>") + "<Hit_accession>".length();
			int accessionEnd = hits[hitCount].indexOf("</Hit_accession>");
			String accession = hits[hitCount].substring(accessionBegin, accessionEnd);
			accession = accession.replaceAll("\\p{C}", ""); 
			if(!isInList(allAccessionInOneBlastFile,accession)) {
				allAccessionInOneBlastFile.add(accession);
				System.out.println("loaded accession: " + accession);
			}//if isInList
		}//for
		return allAccessionInOneBlastFile;		
	}//getAllAccessionInOneBlastFile
	
	public static List<String> getAllGINumbersInOneBlastFile(String oneBlastFileStr){
		String ginumber = "";
		List<String> allGINumbersInOneBlastFile = new ArrayList<String>();
		String[] hits = oneBlastFileStr.split("<Hit_id>");
		for(int hitCount = 1; hitCount < hits.length; hitCount++) {
			//System.out.println("hit:\n" + hits[hitCount]);
			int accessionBegin = hits[hitCount].indexOf("gi|") + "gi|".length();
			int accessionEnd = hits[hitCount].indexOf("|",accessionBegin);
			ginumber = hits[hitCount].substring(accessionBegin, accessionEnd);
			System.out.println("ginumber: " + ginumber);
			ginumber = ginumber.replaceAll("\\p{C}", "");
			if(!isInList(allGINumbersInOneBlastFile,ginumber)) {
				allGINumbersInOneBlastFile.add(ginumber);
				System.out.println("loaded ginumber: " + ginumber);
			}//if isInList
		}//for
		return allGINumbersInOneBlastFile;	
	}//getAllGINumbersInOneBlastFile
}//class
