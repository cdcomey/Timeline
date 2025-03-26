import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Regex{
    public static void main(String[] args){
        String subject = "";
        try{
            subject = new String(Files.readAllBytes(Paths.get("Timelines/USA/regex.txt")));
        } catch (Exception e){}


        String regex = "\\b \\i \n}\n\n\\i0\\b0 ";
        // System.out.println(subject);
        String result = subject.replace(regex, "");
        System.out.println(result);
        System.out.println(result.length());
    }
}