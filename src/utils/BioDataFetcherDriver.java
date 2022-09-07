package utils;

public class BioDataFetcherDriver {
	public static void main(String[] args) {
		BioDataFetcher fetcher = new BioDataFetcher();
		/*
		for(int count = 0; count <= 74; count++) {
			fetcher.load(count);
		}//for all blast data files
		*/
		fetcher.getProcessedAccessions(0);
		System.out.println(fetcher.getProcessedAccessions());
	}//main
	
}//class BioDataFetcherDriver