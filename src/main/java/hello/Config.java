package hello;

import java.util.ArrayList;
import java.util.List;

public class Config {
    //public static int TOTAL_QUESTIONS = 5;
    public static int TIME_MINUTES = 10;
    public static int QUERY_LENGTH = 7;
    public static List<String> answerList = new ArrayList<String>() {{
        add("4");
        add("5");
        add("1");
        add("6");
        add("3");
        add("3");
        add("4");
    }};

    // QUESTION NAMES TUTORIAL: number-options-skill.ext
    // (int) number:  the number of the question.
    // (int) options: how many options to choose from - set to 1 if an open answer is expected
    // (string) skill: the type of skill tested by the question (analytical, programming, visual, etc)
    // ext: the extentsion of the file. it can be  gif, jpg, png or txt.
    // Examples:
    // 1-6-analytical.gif
    // 2-8-logic.jpg
    // 3-0-programming.txt
}
