package gaTriangles;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class ImageFromDesc {

	private AffineTransform canvasIdentity;
	private ImageFormat imgFormat;
	private GenerationsNormal generations;
	private Graphics2D g;
	private int triCnt = 0;
	private BufferedImage img;
	
	private double virtualHeight;
	private double verticalBuffer;
	private double virtualWidth;
	private double horizontalBuffer;
	
	
	public ImageFromDesc (GenerationsNormal generations, ImageFormat imgFormat) {
		this.generations = generations;
		this.imgFormat = imgFormat;
		this.calcImageDims();
		
		this.img = createImage();
		new ImageDisplay(this.imgFormat.displayWidth, this.imgFormat.displayHeight, this.img);
	}
	
	private void calcImageDims () {
		int height = this.imgFormat.bufferedImgHeight;
		virtualHeight = this.imgFormat.vertScale * height;
		double virtualHeightRatio = 1 - (virtualHeight / (height * 1.0));
		verticalBuffer = (virtualHeightRatio * height) / 2.0;
		
		int width = this.imgFormat.bufferedImgWidth;
		virtualWidth = this.imgFormat.horizScale * width;
		double virtualWidthRatio = 1 - (virtualWidth / (width * 1.0));
		horizontalBuffer = (virtualWidthRatio * width) / 2.0;
	}
	
	public BufferedImage createImage () {
		int w = this.imgFormat.bufferedImgWidth;
		int h = this.imgFormat.bufferedImgHeight;
		
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.fillRect(0,  0,  w,  h);

		canvasIdentity = g.getTransform();
	    
		//remove generations outside render range
		int lb = this.imgFormat.startIndex;
		int ub = this.imgFormat.endIndex;
		AtomicInteger indexCnt = new AtomicInteger(0);
		generations.populations = generations.populations.stream()
				.filter(population -> {
					int index = indexCnt.getAndIncrement();
					return index >= lb && index <= ub;
				})
				.collect(Collectors.toList());
		
		
		//renders all generations
		AtomicInteger genIndex = new AtomicInteger();
		generations.populations.forEach(population -> {
			
			int intGenIndex = genIndex.getAndIncrement();
			population.individuals.forEach(individual -> {
				mapIndividualToTri(individual, intGenIndex);
			});	
			
		});
				
		return img;
	}
	
	private double getVerticalMapping (Double vertGene) {
		return (virtualHeight * vertGene) + verticalBuffer;
	}
	
	private double getHorizontalMapping (int generationNum) {
		double generationPercent = generationNum / (this.generations.populations.size() * 1.0);
		return (generationPercent * virtualWidth) + horizontalBuffer;
	}
	
	private void mapIndividualToTri (IndividualNormal individual, int genNum) {
		if (Math.random() > this.imgFormat.renderThresh) {
			return;
		}
		triCnt++;
		List<Double> genome = individual.genome;
		
		double tx = this.getHorizontalMapping(genNum);
		double ty = this.getVerticalMapping(genome.get(0));
		double sx = Math.pow(this.imgFormat.scaleMult * genome.get(1), 1.5); // include exp in JSON
		double sy = Math.pow(this.imgFormat.scaleMult * genome.get(2), 1.5); // include exp in JSON
		double theta = 2 * Math.PI * genome.get(3);
		
		int colorR = (int) Math.floor( 255 * genome.get(4) );
		int colorG = (int) Math.floor( 255 * genome.get(5) );
		int colorB = (int) Math.floor( 255 * genome.get(6) ); 
		int colorA = (int) Math.floor(  this.imgFormat.alpha * genome.get(7) );

		g.setColor(new Color(colorR, colorG, colorB, colorA));
		printTriangle(tx, ty, sx, sy, theta);
	}
	
	private void printTriangle (double tx, double ty, double sx, double sy, double theta) {
		int[] xPoints = new int[]{0, 1, 2};
		int[] yPoints = new int[]{0, 1, 0};
		
		double invSx = 1 / sx;
		double invSy = 1 / sy;

		Polygon tri = new Polygon(xPoints, yPoints, xPoints.length);
		g.scale(sx, sy);
		g.translate(tx * invSx, ty * invSy);
		g.rotate(theta);
		g.fillPolygon(tri);
		g.setTransform(canvasIdentity);
	}
	
	public BufferedImage getImage () {
		return this.img;
	}
	
}
