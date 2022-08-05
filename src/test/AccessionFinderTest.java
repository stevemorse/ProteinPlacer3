package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import utils.GenebankDBLoader;

public class AccessionFinderTest {
	GenebankDBLoader loader;

    @Before                                         
    public void setUp() {
    	loader = new GenebankDBLoader();
    }

	@Test
    public void loadTest() {
		
	    loader.setStartFileNum(1);
	    loader.setEndFileNum(1);
	    loader.setTest(true);
	    loader.load();
	}
}
