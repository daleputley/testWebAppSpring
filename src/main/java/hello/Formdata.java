package hello;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Formdata {

    //form data
    private int questionNR;
    private String answer;
    private String firstname, lastname;
    private long startTime;
    private String startTimeString;

    DateFormat formatter = new SimpleDateFormat("HH:mm");
    private String dateFormatted;

    //getters and setters for the data
    public String getFirstname() {return firstname;}
    public void setFirstname(String firstname) {this.firstname = firstname;}

    public String getStartTimeString() {return startTimeString;}
    public void setStartTimeString(String startTimeString) {this.startTimeString = startTimeString;}

    public String getLastname() {return lastname;}
    public void setLastname(String lastname){this.lastname = lastname;}

    public int getQuestionNR() {
        return questionNR;
    }
    public void setQuestionNR(int questionNR) {
        this.questionNR = questionNR;
    }

    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {this.answer = answer;}

    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {this.startTime = startTime;
        dateFormatted=formatter.format(startTime);
        System.out.println(startTime);
    }

    public void incrementQuestionNr(){
        this.questionNR++;
    }

    public String getDateFormatted() {return dateFormatted;}

    public void setDateFormatted(String dateFormatted) {this.dateFormatted = dateFormatted;
    }
}
