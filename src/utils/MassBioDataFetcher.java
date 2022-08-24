package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

public class MassBioDataFetcher {
int inputFileNumber = 0;
	
	public static void main(String args[]) {
		long startTime = System.nanoTime();
		MassBioDataFetcher fetcher = new MassBioDataFetcher();
		fetcher.load();
		long endTime = System.nanoTime();
        long runTime = endTime - startTime;
        System.out.println("system terminates normally, runtime: " + runTime / 1000000000 + " seconds");
	}//main1
	
	public int getFileNum() {
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
		return inputFileNumber;
	}//getFileNum
	
	public void load() {
		int fileNum = this.getFileNum();
		List<String> allAccessions = getallAccessionIds(1,fileNum);
		ListIterator<String> allAccessionsIter = allAccessions.listIterator();
		StringBuilder massAccessionsStr = new StringBuilder("");
		while(allAccessionsIter.hasNext()) {
			String currentStr = allAccessionsIter.next();	
			massAccessionsStr.append(currentStr + ".1,");
			System.out.println("massAccessionsStr: " + allAccessions.size() + " accessions");		
		}//while
		System.out.println("fetching: " + massAccessionsStr);
		this.doPost(massAccessionsStr.toString(), fileNum); 
	}//load
	
	public void doPost(String accessionStr, int fileNum) {
		//String outFileName = "/home/steve/Desktop/ProteinPlacer3/data/features" + fileNum + ".txt";
		//String outFileName = "/home/steve/Desktop/ProteinPlacer3/data/text" + fileNum + ".xml";
		String outFileName = "/home/steve/Desktop/ProteinPlacer3/data/text" + fileNum + ".txt";
		CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("db", "protein"));
            nameValuePairs.add(new BasicNameValuePair("email", "stemors@encs.concordia.ca"));
            nameValuePairs.add(new BasicNameValuePair("id", accessionStr));
            //nameValuePairs.add(new BasicNameValuePair("retmode", "text"));
            //nameValuePairs.add(new BasicNameValuePair("rettype", "ft"));
            nameValuePairs.add(new BasicNameValuePair("retmode", "text"));
            nameValuePairs.add(new BasicNameValuePair("rettype", "native"));
            //nameValuePairs.add(new BasicNameValuePair("usehistory", "y"));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(post);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            String featureStr = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                featureStr = featureStr + line + "\n";
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFileName, true));
            writer.append(featureStr + "\n");       
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}//doPost
	
	public static List<String> getallAccessionIds (int flag, int fileNum){
		List<String> allAccessions = new ArrayList<String>();
		String proteinDataInFileString = 
			"/home/steve/Desktop/ProteinPlacer3/data/Blast2GoXML/results_";
		String blastFileStr = 
				proteinDataInFileString + fileNum + "/blastResult_" + fileNum + ".xml";
		File currentBlastFile = new File(blastFileStr);
		System.out.println("ccollecting accessions from file: " + blastFileStr);
		
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
	
	public static boolean isInList(List<String> allAccessionIds, String accessionStr) {
		boolean isInList = false;
		ListIterator<String> allAccessionIdsIter = allAccessionIds.listIterator();
		while(allAccessionIdsIter.hasNext() && !isInList) {
			String currentStr = allAccessionIdsIter.next();
			if(currentStr.compareTo(accessionStr) == 0) {isInList = true;}//if
		}//while allAccessionIdsIter has next	
		return isInList;
	}//isInList
	
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
			}//if !isInList
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
			if(!isInList(allGINumbersInOneBlastFile,ginumber)) {
				allGINumbersInOneBlastFile.add(ginumber);
			}//if !isInList
			System.out.println("loaded ginumber: " + ginumber);
		}//for
		return allGINumbersInOneBlastFile;	
	}//getAllGINumbersInOneBlastFile
}//class

