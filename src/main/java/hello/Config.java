package hello;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static int TOTAL_QUESTIONS = 5;
    public static int TIME_MINUTES = 6;
    public static int QUERY_LENGTH = 5;
    public static List<String> answerList = new ArrayList<String>() {{
        add("4");
        add("5");
        add("1");
        add("6");
        add("3");
    }};
}
