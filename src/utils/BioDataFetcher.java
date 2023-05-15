package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class BioDataFetcher {
	private static int inputFileNumber = 0;
	private List<String> uniqueAccessions = null;
	private List<String> processableAccessions = null;
	private List<String> processedAccessions = null;
	private List<String> allAccessions = null;
	private String proteinDataInFileString = "";
	boolean firstAccess = false;
	boolean lastAccess = false;
	
	public int getInputFileNumber() {
		return inputFileNumber;
	}
	public void setInputFileNumber(int inputFileNumber) {
		this.inputFileNumber = inputFileNumber;
	}
	public List<String> getUniqueAccessions() {
		return uniqueAccessions;
	}
	public void setUniqueAccessions(List<String> uniqueAccessions) {
		this.uniqueAccessions = uniqueAccessions;
	}
	public List<String> getProcessableAccessions() {
		return processableAccessions;
	}
	public void setProcessableAccessions(List<String> processableAccessions) {
		this.processableAccessions = processableAccessions;
	}
	public List<String> getAllAccessions() {
		return allAccessions;
	}
	public void setAllAccessions(List<String> allAccessions) {
		this.allAccessions = allAccessions;
	}
	public List<String> getProcessedAccessions() {
		return processedAccessions;
	}
	public void setProcessedAccessions(List<String> processedAccessions) {
		this.processedAccessions = processedAccessions;
	}
	
	public static void main(String args[]) {
		long startTime = System.nanoTime();
		int fileNum = getFileNum();
		BioDataFetcher biofetcher = new BioDataFetcher();
		biofetcher.load(fileNum);
		long endTime = System.nanoTime();
        long runTime = endTime - startTime;
        System.out.println("in " + runTime / 1000000000 + " seconds");
	}//main
	
	public static int getFileNum() {
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
			//inputFileNumber = inputFileInt;
		} catch (NumberFormatException nfe) {
			System.out.println("you entery is not a valid input...Usage: 0 or a positive integer >= 74" + nfe.getMessage());
		}catch(Exception e) {
			System.out.println("general exception on input");
		}
		//last catch
		return inputFileInt;
	}//getFileNum
	
	public void load(int fileNum) {
		//set first accession tag
		firstAccess = true;
		packAccessionLists(fileNum);
		ListIterator<String> processableAccessionsIter = processableAccessions.listIterator();
		while(processableAccessionsIter.hasNext()) {
			String currentStr = processableAccessionsIter.next();
			if(!processableAccessionsIter.hasNext()) {lastAccess = true;}//set last accession tag on last
			this.doPost(currentStr + ".1", fileNum); //add version number
		}//while
		ResourceFetcher fetcher = ResourceFetcher.getInstance();
		String uniqueAccessionsFileName = fetcher.getResources("uniqueAccessionsFileName");
		writeUniqueAccessions(uniqueAccessions, uniqueAccessionsFileName);
	}//load
	
	public void packAccessionLists(int fileNum) {
		allAccessions = getallAccessionIds(1,fileNum);
		processableAccessions = new ArrayList<String>();
		ResourceFetcher fetcher = ResourceFetcher.getInstance();
		String uniqueAccessionsFileName = fetcher.getResources("uniqueAccessionsFileName");
		File f = new File(uniqueAccessionsFileName);
		if(f.exists()) {
			uniqueAccessions = readUniqueAccessions(uniqueAccessionsFileName);
		}//if exists
		else {
			List<String> ProcessedAccessions = getProcessedAccessions();
			uniqueAccessions = ProcessedAccessions;
		}//else
		ListIterator<String> allAccessionsIter = allAccessions.listIterator();
		int numAccessions = allAccessions.size();
		int count = 1;
		HashSet<String> processableAccessionsSet = new HashSet<String>();
		while(allAccessionsIter.hasNext()) {
			//System.out.println("fetching accession: " + count++ + " of: " + numAccessions);
			String currentStr = allAccessionsIter.next();
			int versionBeginNum = currentStr.indexOf(".1");
            if(versionBeginNum != -1) {
            	currentStr = currentStr.substring(0, versionBeginNum);
            }
			if(!containsIgnoreCase(new ArrayList<String>(processableAccessionsSet),currentStr)) {
				processableAccessionsSet.add(currentStr);
			}//if unique accession
		}//while allAccessionsIter;'
		processableAccessions.addAll(processableAccessionsSet);
		System.out.println("found " + processableAccessions.size() + " accessions ");
	}//packAccessionLists
	
	public void doPost(String accessionStr, int fileNum) {
		ResourceFetcher fetcher = ResourceFetcher.getInstance();
		//ResourceFetcher fetcher = new ResourceFetcher();
		//String outFileName = fetcher.getResources("featuresOutBaseStr") + fileNum + ".txt";
		String outFileName = fetcher.getResources("textOutBaseStr") + fileNum +".xml";
		CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("db", "protein"));
            nameValuePairs.add(new BasicNameValuePair("email", "stemors@encs.concordia.ca"));
            nameValuePairs.add(new BasicNameValuePair("id", accessionStr));
            //nameValuePairs.add(new BasicNameValuePair("retmode", "text"));
            //nameValuePairs.add(new BasicNameValuePair("rettype", "ft"));
            nameValuePairs.add(new BasicNameValuePair("retmode", "xml"));
            nameValuePairs.add(new BasicNameValuePair("rettype", "native"));
            //nameValuePairs.add(new BasicNameValuePair("usehistory", "y"));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = client.execute(post);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            String featureStr = "";
            String line = "";
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFileName, true));
            if(firstAccess) {
            	writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n");
            	writer.write("<ENTRIES>" + "\n");
            	firstAccess = false;
            }//if firstAccess
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                if(line.compareTo("<?xml version=\"1.0\" encoding=\"UTF-8\"  ?>") != 0) {
                	featureStr = featureStr + line + "\n";
                }//if not begin of file xml tag
            }//while
            //trim terminal version number from accessionStr
            int versionBeginNum = accessionStr.indexOf(".1");
            if(versionBeginNum != -1) {
            	accessionStr = accessionStr.substring(0, versionBeginNum);
            }
            uniqueAccessions.add(accessionStr);
            writer.append("<ACCESSION>" + accessionStr + "</ACCESSION>"  + "\n");
            writer.append("<ENTRY>" + "\n" + featureStr + "\n" + "</ENTRY>" + "\n");
            if(lastAccess) {
            	writer.write("</ENTRIES>" + "\n");
            }//if last accession
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}//doPost
	
	public static boolean containsIgnoreCase(List<String> allAccessionIds, String accessionStr) {
		boolean isInList = false;
		ListIterator<String> allAccessionIdsIter = allAccessionIds.listIterator();
		while(allAccessionIdsIter.hasNext() && !isInList) {
			String currentStr = allAccessionIdsIter.next();
			if(currentStr.compareToIgnoreCase(accessionStr) == 0) {isInList = true;}//if
		}//while allAccessionIdsIter has next	
		return isInList;
	}//isInList
	
	public List<String> getallAccessionIds (int flag, int fileNum){
		List<String> allAccessions = new ArrayList<String>();
		ResourceFetcher fetcher = ResourceFetcher.getInstance();
		proteinDataInFileString = fetcher.getResources("proteinDataInFileString");
		String blastFileStr = 
				proteinDataInFileString + fileNum + "/blastResult_" + fileNum + ".xml";
		File currentBlastFile = new File(blastFileStr);
		System.out.println("collecting accessions from file: " + blastFileStr);		
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
			allAccessionInOneBlastFile.add(accession);
			System.out.println("loaded accession: " + accession);
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
			allGINumbersInOneBlastFile.add(ginumber);
			System.out.println("loaded ginumber: " + ginumber);
		}//for
		return allGINumbersInOneBlastFile;	
	}//getAllGINumbersInOneBlastFile
	
	public List<String> readUniqueAccessions(String filename){
		if(uniqueAccessions == null) {
			uniqueAccessions = new ArrayList<String>();
		}//if
		File uniqueAccessionsFile = new File(filename);
		if(uniqueAccessionsFile.isFile()) {
			char[] dataBuffer = new char[(int) uniqueAccessionsFile.length()];
			try {
				FileReader fileReader = new FileReader(uniqueAccessionsFile);
				fileReader.read(dataBuffer);
				fileReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IOException: " + e.getMessage());
				e.printStackTrace();
			}//try/catch
			String uniqueAccessionsFileStr = new String(dataBuffer);
			String[] accessions = uniqueAccessionsFileStr.split("<ACCESSION>");
			for(int accCount = 1; accCount < accessions.length; accCount++) {
				int endOfOneAccession = accessions[accCount].indexOf("</ACCESSION>");
				String oneAccessionStr = accessions[accCount].substring(0, endOfOneAccession);
				oneAccessionStr = oneAccessionStr.trim();
				oneAccessionStr = oneAccessionStr.replaceAll("\\p{C}", "");
				uniqueAccessions.add(oneAccessionStr);
			}//for all stored accessions
		}//if uniqueAccessionsFile is file
		return uniqueAccessions;
	}//readUniqueAccessions
	
	public void writeUniqueAccessions(List<String> uniqueAccessions, String filename) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
			String accessionStr = "";
			ListIterator<String> uniqueAccessionsLiter = uniqueAccessions.listIterator();
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n");
			writer.write("<UNIQUEACCESSIONS>" + "\n");
			while(uniqueAccessionsLiter.hasNext()) {
				accessionStr = uniqueAccessionsLiter.next();
						writer.write("<ACCESSION>" + accessionStr + "</ACCESSION>" + "\n");
			}//while  
			writer.write("</UNIQUEACCESSIONS>");
	        writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//catch
	}//writeUniqueAccessions
	
	public void getProcessedAccessionsFASTAFile(int fileNum){
		ResourceFetcher fetcher = ResourceFetcher.getInstance();
		if(processedAccessions == null) {
			processedAccessions = new ArrayList<String>();
		}//if processedAccessions is null
		if(fileNum == 0 || fileNum == 2  || fileNum == 5 || fileNum == 6) {
			HashSet<String> processedAccessionsSet = new HashSet<String>();
			List<File> files = getFiles(fileNum);
			for(File file : files) {
				processedAccessionsSet.addAll(getProcessedAccesionsOfOneFile(file));
				System.out.println(" in processedAccessions fileName: " + file.getName());
				System.out.println(processedAccessionsSet.size());
				//System.out.println(processedAccessions);	
			}//for fileCount
			processedAccessions.addAll(processedAccessionsSet);
		}//fileNum 0 treated differently due to size issues
		else {
			String fileName = fetcher.getResources("textOutBaseStr") + fileNum +".xml";
			File processedAccessionsFile = new File(fileName);
			processedAccessions = getProcessedAccesionsOfOneFile(processedAccessionsFile);
		}//else not fileNum 0
	}//getProcessedAccessions
	
	public List<String> getProcessedAccesionsOfOneFile(File processedAccessionsFile){
		List<String> accessionsInFileList = new ArrayList<String>();
		HashSet<String> accessionsInFileSet = new HashSet<String>();
		if(processedAccessionsFile.isFile()) {
			char[] dataBuffer = new char[(int) processedAccessionsFile.length()];
			try {
				FileReader fileReader = new FileReader(processedAccessionsFile);
				fileReader.read(dataBuffer);
				fileReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IOException: " + e.getMessage());
				e.printStackTrace();
			}//try/catch
			String processedAccessionsFileStr = new String(dataBuffer);
			String[] accessions = processedAccessionsFileStr.split("<ACCESSION>");
			for(int accCount = 1; accCount < accessions.length; accCount++) {
				int endOfOneAccession = accessions[accCount].indexOf("</ACCESSION>");
				if(endOfOneAccession != -1) {
					String oneAccessionStr = accessions[accCount].substring(0, endOfOneAccession);
					//trim version number
					int versionBeginNum = oneAccessionStr.indexOf(".1");
		            if(versionBeginNum != -1) {
		            	oneAccessionStr = oneAccessionStr.substring(0, versionBeginNum);
		            }
					oneAccessionStr = oneAccessionStr.trim();
					oneAccessionStr = oneAccessionStr.replaceAll("\\p{C}", "");
					accessionsInFileSet.add(oneAccessionStr);
				}//if
			}//for all stored accessions
		}//if processedAccessionsFileStr is file
		accessionsInFileList.addAll(accessionsInFileSet);
		return accessionsInFileList;
	}//getProcessedAccesionsOfOneFile
	
	public List<File> getFiles(int fileNum) {
		ResourceFetcher fetcher = ResourceFetcher.getInstance();
		File dir = new File(fetcher.getResources("dataFilesDirectory"));
		//check for valid directory
		if (!(dir.exists() && dir.isDirectory())) {
		  System.out.println("Directory " + dir + " does not exist");
		}//if not dir
		FileFilter filter = (file) -> {
			  return file.getName().endsWith(".xml") && file.getName().contains(fileNum + "_");
			};
		File[] files = dir.listFiles(filter);
		return Arrays.asList(files);
	}//getFiles
	
	public boolean removeAllIgnoreCase(List<String> reFetchAccessions, List<String> processedAccessions) {
		boolean altered = false;
		List<String> reFetchAccessionsCopy = new ArrayList<String>(reFetchAccessions);
		for(String accession:reFetchAccessions) {
			if(containsIgnoreCase(processedAccessions, accession)) {
				reFetchAccessionsCopy.remove(accession);
				altered = true;
			}//if
		}//for
		reFetchAccessions = reFetchAccessionsCopy;
		return altered;
	}//removeAllIgnoreCase
	
	public void reFetch(int FileNum) {
		boolean done = false;
		while(!done) {
			List<String> reFetchAccessions = new ArrayList<String>();
			reFetchAccessions.addAll(processableAccessions);
			getProcessedAccessionsFASTAFile(FileNum);
			done = !removeAllIgnoreCase(reFetchAccessions,processedAccessions);
			ListIterator<String> reFetchIter = reFetchAccessions.listIterator();
			while(reFetchIter.hasNext()) {
				String currentStr = reFetchIter.next();
				if(!reFetchIter.hasNext()) {lastAccess = true;}//set last accession tag on last
				doPost(currentStr,FileNum);
			}//while
			if(FileNum == 0) {
				LargeEntryFileSplitter splitter = new LargeEntryFileSplitter();
				splitter.split(FileNum);
			}//if FileNum
		}//while !done
		ResourceFetcher fetcher = ResourceFetcher.getInstance();
		String uniqueAccessionsFileName = fetcher.getResources("uniqueAccessionsFileName");
		writeUniqueAccessions(uniqueAccessions, uniqueAccessionsFileName);
	}//reFetch
}//class
