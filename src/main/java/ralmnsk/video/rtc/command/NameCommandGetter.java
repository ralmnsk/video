package ralmnsk.video.rtc.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ralmnsk.video.rtc.Constants.NOTHING;


public class NameCommandGetter {
    public String parse(String input){
        String name = NOTHING;
//        name = "\"event\":\"register\"";
        Pattern pattern = Pattern.compile("(\"event\":\"[a-z]+\")");
        Matcher matcher = pattern.matcher(input);
        while(matcher.find()){
            name = matcher.group();
        }
        name = name.replace(" ",NOTHING);
        name = name.replace("\"",NOTHING);
        return name.replace("event:",NOTHING);
    }

    public static void main(String[] args){
        NameCommandGetter nameCommandGetter=new NameCommandGetter();
        String name = nameCommandGetter.parse("\"event\":\"register\",\"a;evweg\":\"sdlkf\"");
        System.out.println(name);
    }
}
