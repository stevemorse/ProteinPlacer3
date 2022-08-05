package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import utils.GenebankDBLoader;

public class DbLoaderTest {
	GenebankDBLoader loader;

    @Before                                         
    public void setUp() {
    	loader = new GenebankDBLoader();
    }

	@Test
    public void loadTest() {
		List<String> accessions = new ArrayList<String>();
		accessions.add("AE004437");
		accessions.add("AE004092");
		accessions.add("AE004091");
		accessions.add("AE003853");
		accessions.add("AE003852");
		accessions.add("AE003851");
		accessions.add("AE003850");
		accessions.add("AE003849");
		accessions.add("AE002565");
		accessions.add("AE002162");
		accessions.add("AB000100");
		accessions.add("AB000106");
		accessions.add("AB020092");
		accessions.add("AB020093");
		accessions.add("AB000111");
	    loader.setStartFileNum(1);
	    loader.setEndFileNum(1);
	    loader.setAllAccessionIds(accessions);
	    loader.load();
	    //assertEquals(loader.getLoadedaccessionInFile(),accessions.size());
	}
}
