package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
/**
 * 
 * @author steve
 *
 */
public class seqSizeCounter {
	static int size0bucket = 0;
	static int size100bucket  = 0;
	static int size200bucket  = 0;
	static int size300bucket  = 0;
	static int size400bucket  = 0;
	static int size500bucket  = 0;
	static int size600bucket  = 0;
	static int size700bucket  = 0;
	static int size800bucket  = 0;
	static int size900bucket  = 0;
	static int size1000bucket  = 0;
	private static String sourceBaseString = "/home/steve/Desktop/ProteinPlacerBk/Fasta";
	//private static String sourceBaseString = "/ProteinPlacer/fasta";
	private static String outFileString = "/home/steve/Desktop/ProteinPlacer/data/buckets.csv";
	static List<List<String>> split = null;
	
	public static void main (String[] args){
		int fileNumberStart = 0;
		int fileNumberEnd = 74;
		for(int count = fileNumberStart; count <= fileNumberEnd; count++) {
			File sourceTextInFile = new File(sourceBaseString + count +  ".txt");
			
			//load source text
			char[] sourceInFileBuffer = new char[(int) sourceTextInFile.length()];
			
			//read source data from current file
			try {
				FileReader sourceFileReader = new FileReader(sourceTextInFile);
				sourceFileReader.read(sourceInFileBuffer);
				sourceFileReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IOException: " + e.getMessage());
				e.printStackTrace();
			}
			String sourcesStr = new String(sourceInFileBuffer);
			split(sourcesStr);
		}//for
		printSizes();
	}//main	
	
	public static void split (String sourcesStr){
		List<String> proteinList = new ArrayList<String>();
		String[] proteins = sourcesStr.split(">");
		for(int sourcesCount = 1; sourcesCount < proteins.length; sourcesCount++){
			//System.out.println(proteins[sourcesCount]);
			proteinList.add(proteins[sourcesCount]);
		}
		ListIterator<String> proteinListIter = proteinList.listIterator();
		int proteinCount = 0;
		while(proteinListIter.hasNext()){
			String current = proteinListIter.next();
			//System.out.println(current + "\n");			
			String sequence = current.replaceAll("Contig", "");
			sequence = sequence.replaceAll("consensus_sequence", "");
			sequence = sequence.replaceAll("Locus.*Length","");
			sequence = sequence.replaceAll("_\\d++_?", "");
			sequence = sequence.replaceAll("No_Hits_Assembly_","");
			sequence = sequence.trim();
			System.out.println(sequence + "\n");	
			
			int currentSize = sequence.length();
			System.out.println(currentSize);
			//add sizes to buckets
			if(currentSize > 0 && currentSize < 100) {
				size0bucket++;
			}else if (currentSize >100 && currentSize < 200) {
				size100bucket++;
			}else if (currentSize >200 && currentSize < 300) {
				size200bucket++;			
			}else if (currentSize >300 && currentSize < 400) {
				size300bucket++;
			}else if (currentSize >400 && currentSize < 500) {
				size400bucket++;
			}else if (currentSize >500 && currentSize < 600) {
				size500bucket++;
			}else if (currentSize >600 && currentSize < 700) {
				size600bucket++;
			}else if (currentSize >700 && currentSize < 800) {
				size700bucket++;
			}else if (currentSize >800 && currentSize < 900) {
				size800bucket++;
			}else if (currentSize >900 && currentSize < 1000) {
				size900bucket++;
			}else if (currentSize > 1000) {size1000bucket++;}
		}//while
		System.out.println("0s "  + size0bucket);
		System.out.println("100s " + size100bucket);
		System.out.println("200s " + size200bucket);
		System.out.println("300s " + size300bucket);
		System.out.println("400s " + size400bucket);
		System.out.println("500s " + size500bucket);
		System.out.println("600s " + size600bucket);
		System.out.println("700s " + size700bucket);
		System.out.println("800s " + size800bucket);
		System.out.println("900s " + size900bucket);
		System.out.println("1000s " + size1000bucket);
	}//split
	
	public static void printSizes() {
		PrintWriter printWriter = null;

		try {
			File file = new File(outFileString);
			FileWriter fileWriter = new FileWriter(file);
			printWriter = new PrintWriter(fileWriter);
			
			printWriter.println("1-100" + "," + size0bucket);
			printWriter.println("100-200" + "," + size100bucket);
			printWriter.println("200-300" + "," + size200bucket);
			printWriter.println("300-400" + "," + size300bucket);
			printWriter.println("400-500" + "," + size400bucket);
			printWriter.println("500-600" + "," + size500bucket);
			printWriter.println("600-700" + "," + size600bucket);
			printWriter.println("700-800" + "," + size700bucket);
			printWriter.println("800-900" + "," + size800bucket);
			printWriter.println("900-1000" + "," + size900bucket);
			printWriter.println("1000+" + "," + size1000bucket);		     
		}	catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}//if
		}//finally
	}//printSizes
}//class
