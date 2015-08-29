package utils;

import gaTriangles.FileHelper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

/*
 * Class to gather all models referenced from the imageDescriptions directory
 * and allow them to be seen by git
 */
public class GitHelper {
	
	String imgDescPath = "./files/imageDescriptions/";
	String modelPath = "/files/models/";
	String[] ignoreList = new String[] {
			".classpath",
			".project",
			".settings",
			"/bin/",
			"/nodeScripts/",
			"/files/logs/*",
			"\n",
			"/files/images/*",
			"!/files/images/1438793585183.png",
			"\n",
			"/files/thumbs/*",
			"!/files/thumbs/1435529169270.png",
			"\n",
			"/files/models/*",
	};
	
	public GitHelper () {
		String [] fileList = new File(imgDescPath).list();
		
		//collect file paths for all models used by img config files
		Set<String> modelList = Arrays.stream(fileList)
				.map(fileName -> {
					String filePath = imgDescPath + fileName;
					JSONObject imgDescObj = FileHelper.getJsonFromFile(filePath);
					String modelPath = (String) imgDescObj.get("filePath");
					if (modelPath.length() > 1) {
						modelPath = modelPath.substring(1, modelPath.length());
					}
					return "!" + modelPath;
				})
				.filter(modelPath -> !modelPath.equals("!"))
				.collect(Collectors.toSet());
		
		//write .gitignore
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(".gitignore");
			for (String line : ignoreList) pw.write(line + "\n");
			for (String modelpath : modelList) pw.write(modelpath + "\n");
		}	
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			pw.close();
		}	
	}
	
	public static void main (String[] args) {
		new GitHelper();
	}

}
