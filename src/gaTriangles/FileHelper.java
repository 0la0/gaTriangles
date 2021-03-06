package gaTriangles;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FileHelper {
	
	/**
	 * @param filePath (String)
	 * @return JSONObject
	 */
	public static JSONObject getJsonFromFile (String filePath) {
		try {
			Scanner scanner = new Scanner(new File(filePath));
			StringBuilder sb = new StringBuilder();
			while (scanner.hasNextLine()) {
				sb.append(scanner.next());
			}
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(sb.toString());
			
			return jsonObj;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Write buffered image to file (.png)
	 * @param img (BufferedImage)
	 * @param filePath (String)
	 */
	public static void writeImage (BufferedImage img, String filePath) {
		try {
		    ImageIO.write(img, "png", new File(filePath + ".png"));
		} catch (IOException e) {
		    System.out.println("ImageFromDesc.writeImageToFile IOException:");
		    System.out.println(e);
		}
	}
	
	/**
	 * Write json to file
	 * @param popObj (JSONObject)
	 * @return
	 */
	public static String printModelToFile (JSONObject popObj) {
		String fileName = new Date().getTime() + "";
		FileWriter fw = null;
		try {
			fw = new FileWriter("./files/models/" + fileName + ".json");
			fw.write(popObj.toJSONString());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}
	

}
