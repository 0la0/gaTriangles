package gaTriangles;

import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

/*
 * Given a model, render and save an image
 */
public class ImageFromJson {

	public ImageFromJson (String filePath, boolean isRender) {
		JSONObject imgData = FileHelper.getJsonFromFile(filePath);
		ImageFormat imgFormat = new ImageFormat(imgData);
		
		JSONObject jsonGenerations = FileHelper.getJsonFromFile(imgFormat.filePath);
		GenerationsNormal gensNormal = new GenerationsNormal(jsonGenerations);
	
		ImageFromDesc imgDesc = new ImageFromDesc(gensNormal, imgFormat);
		
		int userChoice = isRender ? 0 : JOptionPane.showConfirmDialog(new JFrame(), "Save Image?", "Do Not Save", JOptionPane.YES_NO_OPTION);
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
	
	public static void main (String[] args) {
		String basePath = "./files/imageDescriptions/";
		if (args.length >= 1) {
			new ImageFromJson(basePath + args[0], false);
		}
		else {
			System.out.println("Usage: ImageFromJson imageDescription.json");
		}
	}
}
