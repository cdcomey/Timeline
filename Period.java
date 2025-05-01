import java.awt.Color;
import java.util.ArrayList;

// periods are events with a start and end date
public class Period extends GenericEvent{
	
	private Date date2;
	
	//blank event/period
	public Period(int year){
		title = "New period";
		splitTitle = super.splitRTFString(title);
		date = new Date(1, 1, year);
        date2 = new Date(1, 1, year+1);
	}
	
	//standard period with separate dates and a defined color
	public Period(String title, String description, int month, int day, int year, int month2, int day2, int year2, int red, int green, int blue, String category, ArrayList<String> tags, ArrayList<MyImage> images){
		this.title = title;
		splitTitle = super.splitRTFString(title);
		this.description = description;
		date = new Date(month, day, year);
		date2 = new Date(month2, day2, year2);
		color = new Color(red, green, blue);
		hex = RGBtoHex(red, green, blue);
		this.category = category;
		// this.tags = setTags(tags);
		this.tags = new ArrayList<String>();
		for (int i = 0; i < tags.size(); i++)
			this.tags.add(tags.get(i));
		this.images = new ArrayList<MyImage>();
		for (int i = 0; i < images.size(); i++)
			this.images.add(images.get(i));
	}
	
	//period with separate dates, a defined color, and with the end date set to the present day
	public Period(String title, String description, int month, int day, int year, boolean present, int red, int green, int blue, String category, ArrayList<String> tags, ArrayList<MyImage> images){
		this.title = title;
		splitTitle = super.splitRTFString(title);
		this.description = description;
		date = new Date(month, day, year);
		color = new Color(red, green, blue);
		hex = RGBtoHex(red, green, blue);
		this.category = category;
		this.present = true;
		// this.tags = setTags(tags);
		this.tags = new ArrayList<String>();
		for (int i = 0; i < tags.size(); i++)
			this.tags.add(tags.get(i));
		this.images = new ArrayList<MyImage>();
		for (int i = 0; i < images.size(); i++)
			this.images.add(images.get(i));
	}
	
	public Date getDate2(){ return date2; }
	public int getMonth2(){ return date2.getMonth(); }
	public int getDay2(){ return date2.getDay(); }
	public int getYear2(){ return date2.getYear(); }
	
	@Override
	public String toString(){
		String s = date.monthString() + " " + date.getDay() + ", " + date.getYear();
        s += " - " + date2.monthString() + " " + date2.getDay() + ", " + date2.getYear();
		s += "\n" + title + "\n\n" + description;
		return s;
	}
	
    @Override
	public String toString(int currentYear, boolean modernDating){
		String s = date.longForm(currentYear, modernDating);
        s += " - " + (present ? "present" : (date2.longForm(currentYear, modernDating) + " : " + Date.dateDiff(date, date2)));
		if (tags.size() > 0){
			s += "\\\nTags: ";
			for (int i = 0; i < tags.size(); i++){
				s += tags.get(i) + ", ";
			}
			s = s.substring(0, s.length() - 2);
		}
		
		return s + "\\\n" + title + "\\\n\\\n" + description;
	}
	
    @Override
	public String toStringVerbose(){
		String s = "";
		String endStr = "\\\n";
		if (title.charAt(title.length()-1) == '\n')
			title = title.substring(0, title.length()-1);
		if (description.length() > 0 && description.charAt(description.length()-1) == endStr.charAt(endStr.length()-1))
			description = description.substring(0, description.length()-1);
		s += "Title: " + title + endStr;
		s += "Description: " + description + endStr;
		s += "Type: Period" + endStr;
		s += "Date: " + date.shortForm();
        s += " - " + (present ? "present\\\n" : (date2.shortForm() + endStr));
		s += "Color: " + color.getRed() + "/" + color.getGreen() + "/" + color.getBlue() + endStr;
		s += "Category: " + category + endStr;
		s += "Alignment: null" + endStr;
		s += "Tags: " + getTagString() + endStr;
		s += "Images: ";
		for (int i = 0; i < images.size(); i++)
			s += images.get(i).toString() + " | ";
		if (images.size() > 0)
			s = s.substring(0, s.length()-2);
		else
			s += "none\\";
		
		return s;
	}
}