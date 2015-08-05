package gaTriangles;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageDisplay {

	public ImageDisplay (int width, int height, BufferedImage vizImg) {
		ImgPanel imgPanel = new ImgPanel(width, height, vizImg);
		imgPanel.setMinimumSize(new Dimension(width, height));
		imgPanel.setPreferredSize(new Dimension(width, height));
		
		JFrame imgFrame = new JFrame();
		imgFrame.add(imgPanel);
		imgFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		imgFrame.setSize(width, height);
		imgFrame.pack();
		imgFrame.setLocation(0, 0);
		imgFrame.setVisible(true);
	}
	
	private class ImgPanel extends JPanel {
		
		private BufferedImage img;
		private int w;
		private int h;
		
		public ImgPanel(int w, int h, BufferedImage img){
			this.w = w;
			this.h = h;
			this.img = img;
			repaint();
		}
		
		protected void paintComponent (Graphics g) {
			g.drawImage(this.img, 0, 0, w, h, null);
		}
		
	}

}
