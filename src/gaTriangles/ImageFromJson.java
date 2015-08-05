package gaTriangles;

import org.json.simple.JSONObject;

public class ImageFromJson {

	public ImageFromJson (String filePath) {
		JSONObject imgData = FileHelper.getJSONObject(filePath);
		ImageFormat imgFormat = new ImageFormat(imgData);
		
		JSONObject jsonGenerations = FileHelper.readFromFile(imgFormat.filePath);
		GenerationsNormal gensNormal = new GenerationsNormal(jsonGenerations);
	
		new ImageFromDesc(gensNormal, imgFormat);
	}
	
	public static void main (String[] args) {
		String basePath = "./files/imageDescriptions/";
		StringBuilder filePath = new StringBuilder(basePath);
		if (args.length == 0) {
			filePath.append("test3.json");
			new ImageFromJson(filePath.toString());
		}
		else if (args.length == 1) {
			filePath.append(args[0]);
			new ImageFromJson(filePath.toString());
		}
		else {
			System.out.println("Usage: ImageFromJson [fileDescription.json]");
		}
	}
}
