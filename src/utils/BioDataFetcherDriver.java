package utils;

public class BioDataFetcherDriver {
	public static void main(String[] args) {
		BioDataFetcher fetcher = new BioDataFetcher();
		/*
		for(int count = 0; count <= 74; count++) {
			fetcher.load(count);
		}//for all blast data files
		*/
		fetcher.packAccessionLists(0);
		//fetcher.reFetch(0);
		System.out.println("processableAccessions: ");
		System.out.println(fetcher.getProcessableAccessions().size());
		System.out.println(fetcher.getProcessableAccessions());
		/*
		System.out.println("uniqueAccessions: ");
		System.out.println(fetcher.getUniqueAccessions().size());
		System.out.println(fetcher.getUniqueAccessions());
		*/
		fetcher.getProcessedAccessions(0);
		System.out.println("processedAccessions: ");
		System.out.println(fetcher.getProcessedAccessions().size());
		System.out.println(fetcher.getProcessedAccessions());		
	}//main
	
	
}//class BioDataFetcherDriver