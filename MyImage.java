import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
	
public class MyImage{
	private String imageName, caption;
	
	public MyImage(String imageName){
		this.imageName = imageName;
		caption = "No description";
	}
	
	public MyImage(String imageName, String caption){
		this.imageName = imageName;
		this.caption = caption;
	}
	
	@Override
	public String toString(){
		return imageName + ";" + caption;
	}
	
	public String getImageName(){ return imageName; }
	public String getCaption(){ return caption; }
	
	public void drawFromFile(Graphics g, int x, int y, int maxWidth, int maxHeight, boolean xCentered, boolean yCentered){
		BufferedImage img = null;
		// print("image name = " + imageName);
        try {

			File file = new File(imageName);
            img = ImageIO.read(file);
			
			int width = img.getWidth();
			int height = img.getHeight();
			
			// System.out.println(width + ", " + height + ", " + maxWidth + ", " + maxHeight);
			// g.setColor(Color.white);
			// g.drawRect(x - maxWidth/2, y - maxHeight/2, maxWidth, maxHeight);
			
			if (width > maxWidth && height <= maxHeight){
				// System.out.println("case 1");
				height = (int)((double)(maxWidth)/width * height);
				width = maxWidth;
			} else if (width <= maxWidth && height > maxHeight){
				// System.out.println("case 2");
				width = (int)((double)(maxHeight)/height * width);
				height = maxHeight;
			} else if (width > maxWidth && height > maxHeight){
				if (height - maxHeight >= width - maxWidth){
					// System.out.println("case 3a");
					width = (int)((double)(maxHeight)/height * width);
					height = maxHeight;
				} else {
					// System.out.println("case 3b");
					height = (int)((double)(maxWidth)/width * height);
					width = maxWidth;
				}
			}
			// print(y + " - " + (height/2));
			// print("" + yCentered);
			if (xCentered)
				x -= width/2;
			if (yCentered)
				y -= height/2;
			// print("y after: " + y);
			g.drawImage(img, x, y, width, height, null);
        } catch (IOException e) {
			System.err.println("IOException in MyImage.drawFromFile()\n" + e);
		}
	}
	
	public void drawFromURL(Graphics g, int x, int y, int maxWidth, int maxHeight){
		BufferedImage img = null;
        try {
			URL link = new URL(imageName);
            img = ImageIO.read(link);
			
			int width = img.getWidth();
			int height = img.getHeight();
			
			if (width > maxWidth && height <= maxHeight){
				height = (int)((double)(maxWidth)/width * height);
				width = maxWidth;
			} else if (width <= maxWidth && height > maxHeight){
				width = (int)((double)(maxHeight)/height * width);
				height = maxHeight;
			} else if (width > maxWidth && height > maxHeight){
				if (height - maxHeight >= width - maxWidth){
					width = (int)((double)(maxHeight)/height * width);
					height = maxHeight;
				} else {
					height = (int)((double)(maxWidth)/width * height);
					width = maxWidth;
				}
			}
			
			g.drawImage(img, x - width/2, y - height/2, width, height, null);
        } catch (IOException e) {
			System.err.println("IOException in MyImage.drawFromURL()\n" + e);
		}
	}

	private static void print(String s){
		System.out.println(s);
	}
}