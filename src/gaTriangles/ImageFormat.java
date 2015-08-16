package gaTriangles;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.json.simple.JSONObject;

public class ImageFormat {
	
	public String filePath;
	public int bufferedImgWidth;
	public int bufferedImgHeight;
	public int displayWidth;
	public int displayHeight;
	public int startIndex;
	public int endIndex;
	public double renderThresh;
	public double xPosMult;
	public double scaleMult;
	public int alpha;
	public int alphaJitter;
	public int widthToScale;
	public double vertScale;
	public double horizScale;
	public double scaleExp;
	
	public ImageFormat () {
		this.setScale();
	}
	
	public ImageFormat (JSONObject jsonObj) {
		this.mapJson(jsonObj);
		this.setScale();
		System.out.println(this.toString());
	}
	
	private void setScale () {
		this.scaleMult = this.scaleMult * ( this.bufferedImgWidth / (this.widthToScale * 1.0) );
	}
	
	public String toString () {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("filePath:\t %s \n", this.filePath));
		sb.append(String.format("imgWidth:\t\t %d \n", this.bufferedImgWidth));
		sb.append(String.format("imgHeight:\t\t %d \n", this.bufferedImgHeight));
		sb.append(String.format("displayWidth:\t\t %d \n", this.displayWidth));
		sb.append(String.format("displayHeight:\t\t %d \n", this.displayHeight));
		sb.append(String.format("startIndex: \t %d \n", this.startIndex));
		sb.append(String.format("endIndex: \t %d \n", this.endIndex));
		sb.append(String.format("renderThresh: \t %f \n", this.renderThresh));
		sb.append(String.format("xPosMult: \t %f \n", this.xPosMult));
		sb.append(String.format("scaleMult: \t %f \n", this.scaleMult));
		sb.append(String.format("alpha: \t %d \n", this.alpha));
		sb.append(String.format("alphaJitter: \t %d \n", this.alphaJitter));
		sb.append(String.format("widthToScale: \t %d \n", this.widthToScale));
		sb.append(String.format("vertScale: \t %f \n", this.vertScale));
		sb.append(String.format("horizScale: \t %f \n", this.horizScale));
		sb.append(String.format("scaleExp: \t %f \n", this.scaleExp));
		return sb.toString();
	}
	
	private void mapJson (JSONObject jsonObj) {
		Field[] allFields = ImageFormat.class.getDeclaredFields();
		Arrays.stream(allFields).forEach(field -> {
			String type = field.getType().toString();
			String key = field.getName();
			try {
				if (type.equals("class java.lang.String")) {
					String jsonVal = (String) jsonObj.get(key);
					field.set(this, jsonVal);
				}
				else if (type.equals("int")) {
					int jsonVal = ((Number) jsonObj.get(key)).intValue();
					field.set(this, jsonVal);
				}
				else if (type.equals("double")) {
					double jsonVal = ((Number) jsonObj.get(key)).doubleValue();
					field.set(this, jsonVal);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}	
		});
		
		
		
	}
	
	
}
