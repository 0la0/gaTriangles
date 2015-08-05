package gaTriangles;

import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

public class ImageFromJson {

	public ImageFromJson (String filePath, boolean isRender) {
		JSONObject imgData = FileHelper.getJSONObject(filePath);
		ImageFormat imgFormat = new ImageFormat(imgData);
		
		JSONObject jsonGenerations = FileHelper.readFromFile(imgFormat.filePath);
		GenerationsNormal gensNormal = new GenerationsNormal(jsonGenerations);
	
		ImageFromDesc imgDesc = new ImageFromDesc(gensNormal, imgFormat);
		
		int userChoice;
		userChoice = isRender ? 0 : JOptionPane.showConfirmDialog(new JFrame(), "Save Image?", "Do Not Save", JOptionPane.YES_NO_OPTION);
		
//		if (isRender) {
//			userChoice = 0;
//		}
//		else {
//			userChoice = JOptionPane.showConfirmDialog(new JFrame(), "Save Image?", "Do Not Save", JOptionPane.YES_NO_OPTION);
//		}
	
		if (userChoice == 0) {
			this.renderImage(imgDesc.getImage());
		}
		System.exit(0);
	}

	
	private void renderImage (BufferedImage img) {
		String now = new Date().getTime() + "";
		String imgFilePath = "./files/images/" + now;
		FileHelper.writeImage(img, imgFilePath);
		System.out.println("image written: " + imgFilePath);
	}
	
	//TODO: optional render flag
	public static void main (String[] args) {
		String basePath = "./files/imageDescriptions/";
		StringBuilder filePath = new StringBuilder(basePath);
		if (args.length == 0) {
			filePath.append("test3.json");
			new ImageFromJson(filePath.toString(), false);
		}
		else if (args.length == 1) {
			filePath.append(args[0]);
			new ImageFromJson(filePath.toString(), false);
		}
		else {
			System.out.println("Usage: ImageFromJson [fileDescription.json]");
		}
	}
}
