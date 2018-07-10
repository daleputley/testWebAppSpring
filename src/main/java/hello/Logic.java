package hello;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

import static hello.Config.QUERY_LENGTH;

@Service
public class Logic {

    //replaces characters in the string or answers
    public String replaceCharAt(String s, int pos, String c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }

    //analyses string and returns quiz results
    public String evaluate(int[] results) {
        String mark = "0";
        int hits = 0;
        int percentage;

        for (int i = 0; i < hello.Config.answerList.size(); i++) {
            int correctAnswer = Integer.parseInt(hello.Config.answerList.get(i));
            if (results[i] == correctAnswer) {
                hits++;
            }
        }
        percentage = (int) (long) (hits * 100 / results.length);
        mark = " " + Integer.toString(hits) + " out of " + results.length + " - " + Integer.toString(percentage) + "%";
        return mark;
    }

    //randomly select the questions

    /**
     * @return an array of randomly arranged integers
     */
    public static int[] randomize() {
        Random random = new Random();
        int index, temp;
        int[] randomOrder = new int[QUERY_LENGTH];

        //populating the integer array with values from 1 to QUERY_LENGTH
        for (int i = 0; i < QUERY_LENGTH; i++) {
            randomOrder[i] = i + 1;
        }

        for (int i = randomOrder.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = randomOrder[index];
            randomOrder[index] = randomOrder[i];
            randomOrder[i] = temp;
        }

        //debug info
        System.out.println("random order created: ");
        for (int i = 0; i < randomOrder.length; i++) {
            System.out.print(randomOrder[i] + " ");
        }

        return randomOrder;
    }

    public static ArrayList<WebContent> updateJumpButtons(int[] allAnswers, ArrayList<WebContent> jumpButtons) {
        String status = "";
        String style="";
        String bootstrapClass;
        //parse string, if answer is equal to 0,
        for (int i = 0; i < allAnswers.length; i++) {
            if (allAnswers[i] == 0) {
                status = "not answered";
                //style = "display:inline";
                bootstrapClass="btn btn-primary";
            } else {
                status = "answered";
                //style = "display:none;";
                bootstrapClass="btn btn-light";
            }
            jumpButtons.get(i).setAnswerStatus(status);
            jumpButtons.get(i).setRowCss(style);
            jumpButtons.get(i).setBootstrapClass(bootstrapClass);
        }
        return jumpButtons;
    }

    public static int unansweredCount(int[] allAnswers) {
        int unanswered = 0;
        for (int i = 0; i < allAnswers.length; i++) {
            if (allAnswers[i] == 0)
                unanswered++;
        }
        return unanswered;
    }
}
