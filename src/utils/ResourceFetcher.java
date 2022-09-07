package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
/**
 * A Singleton that gets the value of a resource from the resource file
 * **/
public class ResourceFetcher {
	
    private static ResourceFetcher instance = null;
    private String resourcefileName = "resources/res.json";
    private ResourceFetcher() {}
    
    public static ResourceFetcher getInstance() {
	    if (instance == null) {
	        instance = new ResourceFetcher();
	    }
	    return instance;
	}
	/**
     * Gets the value of a resource from the resource file when requested by key
     * @param key String that is the key (or resource type name) of a json key value pair
     * @return String that is the value of the resource requested by its key (type name)
     */
    public String getResources(String key) {
            StringBuilder value = new StringBuilder();
            List<String> lines = null;
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(resourcefileName).getFile());             
            try {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    String [] arrLines = content.split(",");
                    lines = Arrays.asList(arrLines);
            } catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                    ioe.printStackTrace();
            }//catch
            lines.stream().forEach(line-> {
                    if(line.contains(key)) {
                            //extract value
                            String [] pair = line.split(":");
                            if(!(value.length() == 0)) {
                                    value.delete(0, value.length());
                            }//if not empty
                            value.append(pair[1].trim().replaceAll("\"", "").replaceAll("}", "").replaceAll("\r|\n", ""));
                    }//if key in line
            });
            System.out.println("resource file: " + resourcefileName + " key: " + key + " value returned: " + value);
            return value.toString();
    }//getResources

}