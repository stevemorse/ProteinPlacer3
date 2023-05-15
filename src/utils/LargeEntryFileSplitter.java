package utils;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class LargeEntryFileSplitter {
	private int maxEntries = 250;
	static int fileNum = 0;
	public static void main(String[] args) {
		LargeEntryFileSplitter splitter = new LargeEntryFileSplitter();
		splitter.split(fileNum);
	}//main
	
	public void split(int fileNum) {
		ResourceFetcher fetcher = ResourceFetcher.getInstance();
		String fileIn = fetcher.getResources("textOutBaseStr") + fileNum +".xml";
		FileInputStream fis = null;
		Scanner scanner = null;
		BufferedWriter writer = null;
		boolean done = false;
		try {
		    fis = new FileInputStream(fileIn);
		    scanner = new Scanner(fis, "UTF-8");
		    int fileCount = 0;
		    while (!done) {
		        //set up new write file resources and variables
		    	boolean firstLine = true;
		    	boolean rootNodeClosed = false;
		    	int entryCount = 0;
		    	String fileOut = fetcher.getResources("textOutBaseStr") + fileNum + "_" + fileCount + ".xml";
		    	writer = new BufferedWriter(new FileWriter(fileOut));
		    	//write entries in blocks of 1000 to new file
		        while(entryCount < maxEntries && scanner.hasNextLine()) {
		        	//now test lines and write entries to output file
		        	StringBuilder entryStrBuilder = new StringBuilder();
		        	boolean currentEntryDone = false;
		        	while(!currentEntryDone && scanner.hasNextLine()) {
			        	String line = scanner.nextLine();
			        	if(firstLine) {
				        	if(line.compareTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?>") != 0) {
				        		entryStrBuilder = entryStrBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n");
				        		entryStrBuilder = entryStrBuilder.append("<ENTRIES>" + "\n");
			                }//if firstLine not begin of file xml tag
				        	entryStrBuilder = entryStrBuilder.append(line + "\n");
				        	firstLine = false;
			        	}//if firstLine
			        	else {
			        		entryStrBuilder = entryStrBuilder.append(line + "\n");
			        		if(line.compareTo("</ENTRY>") == 0) {
			        			entryCount++;
			        			currentEntryDone = true;
			        			writer.write(entryStrBuilder.toString());
			        			//System.out.println(entryStrBuilder.toString());
			        		}//if end of one entry
			        	}//else not firstLine of entries
			        	if(line.compareTo("</ENTRIES>") == 0) {
			        		rootNodeClosed = true;
			        		currentEntryDone = true;
			        	}//if close of xml file root node
		        	}//while currentEntryDone
		        	System.out.println("entryCount is: " + entryCount);
		        }//while entryCount < 1000 && scanner.hasNextLine()
		        if(!rootNodeClosed) {
		        	writer.write("</ENTRIES>");
		        }//if rootNode not closed for write file then close it
		        if(!scanner.hasNextLine()) {
		        	done = true;
		        }
		        writer.flush();
		        writer.close();
		        fileCount++;   
		    }//while not done
		    //manually throw Scanner exceptions if extant
		    if (scanner.ioException() != null) {
		        throw scanner.ioException();
		    }//if
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}//try/catch
		//close input io classes
		if (fis != null) {
	        try {
				fis.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}//try/catch
	    }//if
	    if (scanner != null) {
	        scanner.close();
	    }//if
	}//split
	
	public void count (int filenum) {
		ResourceFetcher fetcher = ResourceFetcher.getInstance();
		String fileIn = fetcher.getResources("textOutBaseStr") + fileNum +".xml";
		FileInputStream fis = null;
		Scanner scanner = null;
		boolean done = false;
		int accesssionStarts =0;
		int accesssionEnds =0;
		try {
		    fis = new FileInputStream(fileIn);
		    scanner = new Scanner(fis, "UTF-8");
		    int fileCount = 0;
	        //set up new write file resources and variables
	    	boolean firstLine = true;
	    	boolean rootNodeClosed = false;
	    	int entryCount = 0;
	    	String fileOut = fetcher.getResources("textOutBaseStr") + fileNum + "_" + fileCount + ".xml";
	    	while(scanner.hasNextLine()) {
	    		String line = scanner.nextLine();
	    		if(line.compareTo("<ACCESSION") == 0) {
	    			accesssionStarts++;
	    		}
	    		if(line.compareTo("<ACCESSION") == 0) {
	    			accesssionEnds++;
	    		}
	    	}//while scanner
	    } catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();	
	    }
		if (accesssionStarts != accesssionEnds) {
			System.out.println("start end mismatch error");
		}
		System.out.println("Accession starts: " + accesssionStarts);
		System.out.println("Accession ends: " + accesssionEnds);
	}
}//class
