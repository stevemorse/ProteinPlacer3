package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BioDataFetcherDriver {
	public static void main(String[] args) {
		BioDataFetcher fetcher = new BioDataFetcher();
		/*
		for(int count = 0; count <= 74; count++) {
			fetcher.load(count);
		}//for all blast data files
		*/
		
		List<File> files = fetcher.getFiles(0);
		System.out.println("files:");
		for(File f : files) {
			System.out.println(f.getName());
		}
		//LargeEntryFileSplitter fs = new LargeEntryFileSplitter();
		//fs.split(0);
		fetcher.packAccessionLists(0);
		fetcher.reFetch(0);
		
		/*
		fetcher.packAccessionLists(74);
		System.out.println("processableAccessions: ");
		System.out.println(fetcher.getProcessableAccessions().size());
		System.out.println(fetcher.getProcessableAccessions().toString());
		HashSet set1 = new HashSet(fetcher.getProcessableAccessions());
		System.out.println(set1.size()); 
		System.out.println(set1.toString());
		List<String> list1 = fetcher.getProcessableAccessions();
		boolean foo = list1.removeAll(set1);
		System.out.println(list1.size()); 
		System.out.println(list1.toString()); 
		*/
		/*
		fetcher.getProcessedAccessions(0);
		System.out.println("processedAccessions: ");
		System.out.println(fetcher.getProcessedAccessions().size());
		//System.out.println(fetcher.getProcessedAccessions());	
		//fetcher.setUniqueAccessions(fetcher.getProcessedAccessions());
		HashSet set4 = new HashSet(fetcher.getProcessedAccessions());
		System.out.println(set4.size()); 
		//System.out.println(set4.toString());
		List<String> list4 = new ArrayList<String>(set4);
		System.out.println(list4.size());
		*/
		/*
		LargeEntryFileSplitter fs = new LargeEntryFileSplitter();
		fs.split(0);
		ResourceFetcher rf = ResourceFetcher.getInstance();
		fetcher.getProcessedAccessions(0);
		System.out.println("processedAccessions: ");
		System.out.println(fetcher.getProcessedAccessions().size());
		fetcher.setUniqueAccessions(fetcher.getProcessedAccessions());
		String fileName = rf.getResources("uniqueAccessionsFileName");
		fetcher.writeUniqueAccessions(fetcher.getUniqueAccessions(), fileName);
		*/
		/*
		System.out.println("uniqueAccessions: ");
		System.out.println(fetcher.getUniqueAccessions().size());
		HashSet theSet = new HashSet(fetcher.getUniqueAccessions());
		System.out.println(theSet.size()); 
		*/
		
	}//main
	
	
}//class BioDataFetcherDriver