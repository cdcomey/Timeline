import java.awt.Graphics;
import java.awt.Color;

public class Rectangle{
	
	private final int x, x2, width, height;
	private int y, y2;
	private final GenericEvent event;
	private final boolean detached;
	private final Color color;
	
	public Rectangle(){
		x = y = x2 = y2 = width = height = 0;
		event = null;
		detached = false;
		color = new Color(0, 0, 0);
	}
	
	public Rectangle(int x, int y, int width, int height, Color color){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		event = null;
		detached = false;
		this.color = color;
		x2 = x + width;
		y2 = y + height;
	}
	
	public Rectangle(int x, int y, int width, int height, GenericEvent event, boolean detached, Color color){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.event = event;
		this.detached = detached;
		this.color = color;
		x2 = x + width;
		y2 = y + height;
	}
	
	public int getX(){ return x; }
	public int getY(){ return y; }
	public int getX2(){ return x2; }
	public int getY2(){ return y2; }
	public int getWidth(){ return width; }
	public int getHeight(){ return height; }
	public String getCoords(){ return "(" + x + ", " + y + ", " + width + ", " + height + ")"; }
	public GenericEvent getEvent(){ return event; }
	public boolean isDetached(){ return detached; }
	public Color getColor(){ return color; }
	
	@Override
	public String toString(){ return "(" + x + ", " + y + ", " + x2 + ", " + y2 + ")"; }
	
	public void addY(int num){
		y += num;
		y2 = y + height;
	}
	
	public void drawMe(Graphics g){
		int spacing = 5;
		g.setColor(color);
		if (width > 2*spacing){
			g.fillRect(x, y+spacing, width, height-2*spacing);
			g.fillRect(x+spacing, y, width-2*spacing, height);
			g.fillOval(x, y, 2*spacing, 2*spacing); //top left
			g.fillOval(x+width-2*spacing, y, 2*spacing, 2*spacing); //top right
			g.fillOval(x, y+height-2*spacing, 2*spacing, 2*spacing); //bottom left
			g.fillOval(x+width-2*spacing, y+height-2*spacing, 2*spacing, 2*spacing); //bottom right
		} else {
			g.fillRect(x, y, width, height);
		}

		if (event instanceof Event){
			Event e = (Event)event;
			if (e.getIsImageEvent() && e.getImages().size() > 0){
				e.getImages().get(0).drawFromFile(g, x + width/2, y + (height-2*spacing)/2, width-2*spacing, height-2*spacing, true, true);
			}
		}
	}
	
	public void drawDashedLine(Graphics g, int length, int space){
		g.setColor(color);
		if (width < height){
			for (int i = y; i < y+height; i+=length+space){
				if (i+length > y+height)
					g.fillRect(x, i, width, y+height-i);
				else
					g.fillRect(x, i, width, length);
			}
		}
		
		else{
			for (int i = x; i < x+width; i+=length+space){
				if (i+length > x+width)
					g.fillRect(i, y, x+width-i, height);
				else
					g.fillRect(i, y, length, height);
			}
		}
	}
}