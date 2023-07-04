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

	private int[] findDimsWithImage(BufferedImage img, int maxWidth, int maxHeight){
		int width = img.getWidth();
		int height = img.getHeight();
		
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

		int dims[] = {width, height};
		return dims;
	}

	public int[] findDims(int maxWidth, int maxHeight){
		BufferedImage img = null;
		int dims[] = {0, 0};
		// print("image name = " + imageName);
        try {

			File file = new File(imageName);
            img = ImageIO.read(file);
			return findDimsWithImage(img, maxWidth, maxHeight);
			
        } catch (IOException e) {
			System.err.println("IOException in MyImage.findDims()\n" + e);
		}

		return dims;
	}
	
	public void drawFromFile(Graphics g, int x, int y, int maxWidth, int maxHeight, boolean xCentered, boolean yCentered){
		BufferedImage img = null;
		// print("image name = " + imageName);
        try {

			File file = new File(imageName);
            img = ImageIO.read(file);

			int dims[] = findDimsWithImage(img, maxWidth, maxHeight);
			int width = dims[0];
			int height = dims[1];
			// print(width + " " + height);
			if (xCentered)
				x -= width/2;
			if (yCentered)
				y -= height/2;
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
			
			int dims[] = findDimsWithImage(img, maxWidth, maxHeight);
			int width = dims[0];
			int height = dims[1];
			
			g.drawImage(img, x - width/2, y - height/2, width, height, null);
        } catch (IOException e) {
			System.err.println("IOException in MyImage.drawFromURL()\n" + e);
		}
	}

	private static void print(String s){
		System.out.println(s);
	}
}