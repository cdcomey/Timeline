import java.awt.Color;
import java.util.ArrayList;

public class Event extends GenericEvent{
	
	private String alignment;
	private boolean isImageEvent;
	
	//blank event/period
	public Event(){
		title = "New event";
		description = "";
		date = new Date(1, 1, 1);
		alignment = "Centered";
		color = new Color(128, 128, 128);
		hex = RGBtoHex(color.getRed(), color.getGreen(), color.getBlue());
		category = "<category>";
		tags = new ArrayList<String>();
		images = new ArrayList<MyImage>();
		isImageEvent = false;
	}
	
	//standard event with separate dates and a defined color
	public Event(String title, String description, int month, int day, int year, int red, int green, int blue, 
	String category, String alignment, ArrayList<String> tags, ArrayList<MyImage> images, boolean isImageEvent){
		this.title = title;
		this.description = capitalize(description);
		date = new Date(month, day, year);
		color = new Color(red, green, blue);
		hex = RGBtoHex(red, green, blue);
		this.category = category;
		this.alignment = alignment;
		// this.tags = setTags(tags);
		this.tags = new ArrayList<String>();
		for (int i = 0; i < tags.size(); i++)
			this.tags.add(tags.get(i));
		this.images = new ArrayList<MyImage>();
		for (int i = 0; i < images.size(); i++)
			this.images.add(images.get(i));
		this.isImageEvent = isImageEvent;
	}

	public String getAlignment(){ return alignment; }
	
	@Override
	public String toString(){
		String s = date.monthString() + " " + date.getDay() + ", " + date.getYear();
		s += "\n" + title + "\n\n" + description;
		return s;
	}
	
	@Override
	public String toString(int currentYear, boolean modernDating){
		String s = date.longForm(currentYear, modernDating);
		if (tags.size() > 0){
			s += "\nTags: ";
			for (int i = 0; i < tags.size(); i++){
				s += tags.get(i) + ", ";
			}
			s = s.substring(0, s.length() - 2);
		}
		
		return s + "\n" + title + "\n\n" + description;
	}
	
	@Override
	public String toStringVerbose(){
		String s = "";
		if (title.charAt(title.length()-1) == '\n')
			title = title.substring(0, title.length()-1);
		if (description.length() > 0 && description.charAt(description.length()-1) == '\n')
			description = description.substring(0, description.length()-1);
		s += "Title: " + title + "\n";
		s += "Description: " + description + "\n";
		s += "Type: " + (isImageEvent ? "ImageEvent" : "Event") + "\n";
		s += "Date: " + date.shortForm() + "\n";
		s += "Color: " + color.getRed() + "/" + color.getGreen() + "/" + color.getBlue() + "\n";
		s += "Category: " + category + "\n";
		s += "Alignment: " + alignment + "\n";
		s += "Tags: " + getTagString() + "\n";
		s += "Images: ";
		for (int i = 0; i < images.size(); i++)
			s += images.get(i).toString() + " | ";
		if (images.size() > 0)
			s = s.substring(0, s.length()-2);
		else
			s += "none";
		
		return s;
	}
}