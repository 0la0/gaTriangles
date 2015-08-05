package gaTriangles;

import org.json.simple.JSONObject;

public class ImageFromJson {

	private String descPath = "./files/imageDescriptions/test3.json";
	
	public ImageFromJson () {
		JSONObject imgData = FileHelper.getJSONObject(descPath);
		ImageFormat imgFormat = new ImageFormat(imgData);
		
		JSONObject jsonGenerations = FileHelper.readFromFile(imgFormat.filePath);
		GenerationsNormal gensNormal = new GenerationsNormal(jsonGenerations);
	
		new ImageFromDesc(gensNormal, imgFormat);
	}
	
	public static void main (String[] args) {
		new ImageFromJson();
	}
}
