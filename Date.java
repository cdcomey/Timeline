public class Date implements Comparable<Date>{
	
	private final int month, day, year;
	private static final String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	private static final int[] monthLengths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	
	public Date(int month, int day, int year){
		this.month = month;
		this.day = day;
		this.year = year;
	}
	
	public Date(String monthString, int day, int year){
		this.day = day;
		this.year = year;
		
		int monthTemp = 0;
		
		for (int i = 0; i < monthNames.length; i++){
			if (monthNames[i].equals(monthString)){
				monthTemp = i+1;
				break;
			}
		}
		
		month = monthTemp;
	}
	
	public Date(String date){
		String[] arr = date.split("/");
		int monthTemp = 0;
		int dayTemp = 0;
		int yearTemp = 0;
		
		try {
			monthTemp = Integer.parseInt(arr[0]);
			dayTemp = Integer.parseInt(arr[1]);
			yearTemp = Integer.parseInt(arr[2]);
		} catch (NumberFormatException ex){
			System.err.println(ex);
		} catch (IndexOutOfBoundsException ex){
			System.err.println(ex);
		}
		
		month = monthTemp;
		day = dayTemp;
		year = yearTemp;
	}
	
	public int getMonth(){ return month; }
	public int getDay(){ return day; }
	public int getYear(){ return year; }
	public String monthString(){ return monthNames[month-1]; }
	
	public String longForm(int currentYear, boolean modernDating){
		int yearsAgo = currentYear - year;
		return monthString() + " " + day + ", " + yearString(year, modernDating) + " (" + yearsAgo + " year" + (yearsAgo == 1 ? "" : "s") + " ago)";
	}
	
	public String shortForm(){
		String date = "";
		date += numFormat(month, 2) + "/";
		date += numFormat(day, 2) + "/";
		date += numFormat(year, 4);
		return date;
	}
	
	public int getDayOfYear(){
		int num = 0;
		for (int i = 0; i < month-1; i++)
			num += monthLengths[i];
		num += day;
		
		if (month >= 3 && isLeapYear(year))
			num++;
		
		return num;
	}
	
	public static String numFormat(int num, int digits){
		int digitsInNum = String.valueOf(num).length();
		String s = "";
		if (digits > digitsInNum){
			int diff = digits - digitsInNum;
			for (int i = 0; i < diff; i++)
				s += "0";
			s += num;
		} else 
			s += num;
		
		return s;
	}
	
	public static String[] getMonthNames(){ return monthNames; }
	public static int[] getMonthLengths(){ return monthLengths; }
	
	public static boolean isLeapYear(int year){
		if (year % 400 == 0)
			return true;
		if (year % 100 == 0)
			return false;
		return year % 4 == 0;
	}
	
	public static String yearString(int year, boolean modernDating){
		if (year >= 1000)
			return "" + year;
		else if (year < 1000 && year > 0 && modernDating)
			return year + " CE";
		else if (year < 1000 && year > 0 && !modernDating)
			return "AD " + year;
		else if (year <= 0 && modernDating)
			return "" + (-year+1) + " BCE";
		else if (year <= 0 && !modernDating)
			return "" + (-year+1) + " BC";
		
		return "1";
	}
	
	public static String dateDiff(Date d1, Date d2){
		if (d1.equals(d2))
			return "";
		String s = "";
		int yearDiff = d2.getYear() - d1.getYear();
		int monthDiff = d2.getMonth() - d1.getMonth();
		int dayDiff = d2.getDay() - d1.getDay();
		
		if (yearDiff > 0 && monthDiff >= 0)
			s += yearDiff + " year" + (yearDiff == 1 ? "" : "s");
		if (yearDiff > 0 && monthDiff < 0){
			yearDiff--;
			monthDiff += 12;
			
			if (yearDiff > 0)
				s = yearDiff + " year" + (yearDiff == 1 ? "" : "s");
		} else if (yearDiff > 0 && monthDiff == 0 && dayDiff < 0){
			yearDiff--;
			monthDiff = 11;
			dayDiff = monthLengths[d1.getMonth()-1] - dayDiff + 1;
			s = "";
		}
		if (monthDiff > 0)
			s += (s.length() > 0 ? ", " : "") + monthDiff + " month" + (monthDiff == 1 ? "" : "s");
		if (dayDiff > 0)
			s += (s.length() > 0 ? ", " : "") + dayDiff + " day" + (dayDiff == 1 ? "" : "s");
		
		return s + " long";
	}
	
	@Override
	public boolean equals(Object o){
		Date d = (Date)o;
		return month == d.getMonth() && day == d.getDay() && year == d.getYear();
	}
	
	@Override
	public int compareTo(Date d){
		if (year == d.getYear())
			return getDayOfYear() - d.getDayOfYear();
		return year - d.getYear();
	}
}