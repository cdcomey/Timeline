import java.awt.Color;

public class Tag implements Comparable<Tag>{
	private final String title, colorName;
	private final Color color;
	
	public Tag(String title){
		this.title = title;
		color = new Color(128, 128, 128);
		colorName = "gray";
	}
	
	public Tag(String title, Color color){
		this.title = title;
		this.color = color;
		colorName = "[r=" + color.getRed() + ",g=" + color.getGreen() + ",b=" + color.getBlue() + "]";
	}
	
	public Tag(String title, Color color, String colorName){
		this.title = title;
		this.color = color;
		this.colorName = colorName;
	}
	
	public String getTitle(){ return title; }
	public Color getColor(){ return color; }
	
	@Override
	public String toString(){ return title + " [" + colorName + "]"; }
	
	public boolean equals(Tag t){ return title.equals(t.getTitle()) && color.equals(t.getColor()); }
	
	@Override
	public int compareTo(Tag t){ return title.compareTo(t.toString()); }
}