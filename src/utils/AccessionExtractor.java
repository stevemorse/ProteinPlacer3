package utils;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;



public class AccessionExtractor {
	static String fileName = "";
	static String tagName = "";
	
	public static void main(String[] args) {
		fileName = "/home/steve/Desktop/ProteinPlacer3/data/Blast2GoXML/results_0/blastResult_0.xml";
		tagName = "Hit_accession";
		AccessionExtractor ae = new AccessionExtractor();
		ae.extract(fileName, tagName);
	}//main
	
	public void extract(String fileName, String tagName){
		try {
	        File inFile = new File(fileName);
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
	        Document doc = docBuilder.parse(inFile);
	        doc.getDocumentElement().normalize();
	        NodeList nodeList = doc.getElementsByTagName(tagName);
	        for (int nodeCounter = 0; nodeCounter < nodeList.getLength(); nodeCounter++) {
	        	Node node = nodeList.item(nodeCounter);
	            System.out.println("\nCurrent Element :" + node.getNodeName());
	            
	        	
	        }//for nodeCounter
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//try/catch	
	}//extract
}//class