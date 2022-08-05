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
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class GenebankDBLoader {
	static String inBaseStr = "/home/steve/genebankData/gbbct";
	static String inTestStr = "/home/steve/genebankData/test.txt";
	String inStr = "";
	boolean test = false;
	int loadedaccessionInFile = 0;
	int startFileNum = 1;
	int endFileNum = 789;
	List<String> allAccessionIds = null;
	
	public int getStartFileNum() {
		return startFileNum;
	}
	public void setStartFileNum(int startFileNum) {
		this.startFileNum = startFileNum;
	}
	public int getEndFileNum() {
		return endFileNum;
	}
	public void setEndFileNum(int endFileNum) {
		this.endFileNum = endFileNum;
	}
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
	public boolean isTest() {
		return test;
	}
	public void setTest(boolean test) {
		this.test = test;
	}
	public GenebankDBLoader() {}
		
	public static void main(String[] args) {
		GenebankDBLoader loader = new GenebankDBLoader();
		loader.load();
	}//main
	
	public void load() {
		if(allAccessionIds == null) {
			allAccessionIds = getallAccessionIds();
		}//if
		System.out.println("loaded " + allAccessionIds.size() + " Accessions");
		for(int fileNum = startFileNum; fileNum <= endFileNum; fileNum++) {
			long startTime = System.nanoTime();
			if(test) {
				inStr = inTestStr;
				System.out.println("running test");
			}//if test
			else {
				inStr = inBaseStr + fileNum + ".seq";
			}//if not test		
			loadedaccessionInFile = 0;
			int accessionInFile = 0;
			String accessionStr = "";
			Connection conn = null;
		    PreparedStatement preparedStatement = null;
			File inFile = new File(inStr);
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
				String[] terms = genebankStr.split("LOCUS");
				accessionInFile = terms.length - 1;
				for(int termsCount = 1; termsCount < terms.length; termsCount++) {
					int acessionBegin = terms[termsCount].indexOf("ACCESSION");
					int acessionEnd = terms[termsCount].indexOf("VERSION");
					accessionStr = terms[termsCount].substring(acessionBegin + "ACCESSION".length(), acessionEnd);
					accessionStr = accessionStr.trim();
					acessionEnd = accessionStr.indexOf(' ');
					if(acessionEnd != -1) {
						accessionStr = accessionStr.substring(0, acessionEnd);
						accessionStr = accessionStr.trim();
						accessionStr.replaceAll("\\p{C}", ""); 
					}//if
					//System.out.println("Accession is: " + accessionStr);
					//System.out.println("length of text is: " + terms[termsCount].length());
					//System.out.println("indexOf /s: " + acessionEnd);
					if(isInList(allAccessionIds, accessionStr)){
						System.out.println("Accession loaded: " + accessionStr);
						preparedStatement = conn.prepareStatement("insert into  genebank values (?, ?)");
						preparedStatement.setString(1, accessionStr);
						preparedStatement.setString(2, terms[termsCount]);
						preparedStatement.executeUpdate();
						loadedaccessionInFile++;
					}//if isInList
				}//for
			} catch (Exception e) {
				System.out.println("Exception: " + e.getMessage());
	        } finally {
	            //close();
	        }//try/catch
	        System.out.println("processed file: " + inStr);
	        System.out.println("Accessions in file: " + accessionInFile);
	        System.out.println("Accessions loaded from file : " + loadedaccessionInFile);
	        long endTime = System.nanoTime();
	        long runTime = endTime - startTime;
	        System.out.println("in " + runTime / 1000000000 + " seconds");
		}//for fileNum
	}//load
	
	public static boolean isInList(List<String> allAccessionIds, String accessionStr) {
		boolean isInList = false;
		ListIterator<String> allAccessionIdsIter = allAccessionIds.listIterator();
		while(allAccessionIdsIter.hasNext() && !isInList) {
			String currentStr = allAccessionIdsIter.next();
			if(currentStr.compareTo(accessionStr) == 0) {isInList = true;}//if
		}//while allAccessionIdsIter has next	
		return isInList;
	}//isInList
	
	public static List<String> getallAccessionIds (){
		List<String> allAccessions = new ArrayList<String>();
		String proteinDataInFileString = 
			"/home/steve/Desktop/ProteinPlacer3/data/Blast2GoXML/results_";
		for(int blastFileCount = 0; blastFileCount <= 74; blastFileCount++) {
			String blastFileStr = 
					proteinDataInFileString + blastFileCount + "/blastResult_" + blastFileCount + ".xml";
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
			List<String> allAccessionInOneBlastFile = getAllAccessionInOneBlastFile(oneBlastFileStr);
			allAccessions.addAll(allAccessionInOneBlastFile);
		}//for blastFileCount
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
			allAccessionInOneBlastFile.add(accession);
			System.out.println("loaded accession: " + accession);
		}//for
		return allAccessionInOneBlastFile;		
	}//getAllAccessionInOneBlastFile
}//class
