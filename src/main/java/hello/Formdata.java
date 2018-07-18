package hello;

import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static hello.Config.QUERY_LENGTH;

public class Formdata {

    //form data
    private int questionIndex;
    private int currentQuestion;
    private String answer;
    private String firstname, lastname, password;
    private String currentQuestionType, currentQuestionText, currentQuestionFileName;

    private Date startTime;
    private String startTimeString;
    private List<String> answerOptions;

    private int[] quizOrder = new int[QUERY_LENGTH];
    boolean rerun = false;
    private String tableCss = "display:;";
    DateFormat formatter = new SimpleDateFormat("HH:mm");
    private String startDateFormatted;
    private int remainingMinutes;
    public String timerCss = "";
    int unansweredCount;


    public void updateQuestionIndex() {
        //increment index only if this is not the first question...
        if (getAnswer() != null) {
            // ...AND if user did not press "jump" nor "previous"
            // ...AND if query end has not been reached
            //... increment question index
            if (!getAnswer().equals("jump")
                    && !getAnswer().equals("previous")
                    && getQuestionIndex() < (QUERY_LENGTH - 1)) {
                incrementQuestionIndex();
                System.out.println("Question index incremented to: " + getQuestionIndex());
            }
            //but if user has pressed "previous", decrement question index
            if (getAnswer().equals("previous") && getQuestionIndex() > 0) {
                decrementQuestionIndex();
                System.out.println("Question index decremented to: " + getQuestionIndex());
            }
        }
    }

    public int getRemainingTime(Date startTime) {
        setStartTime(startTime);
        Date currenttime = new Date();
        int duration = (int) ((currenttime.getTime() - startTime.getTime()) / 1000);
        int remaining = (60 * Config.TIME_MINUTES) - duration;
        setRemainingMinutes((int)Math.round(remaining / 60.00));
        return remaining;
    }


    //------------------------------------------- Getters and Setters ---------------------------------


    public List <String> getAnswerOptions() {return answerOptions;}

    public void setAnswerOptions(List<String> answerOptions) {this.answerOptions = answerOptions;}

    public String getCurrentQuestionFileName() {return currentQuestionFileName;    }

    public void setCurrentQuestionFileName(String currentQuestionFileName) {this.currentQuestionFileName = currentQuestionFileName;}

    public String getCurrentQuestionType() {
        return currentQuestionType;
    }

    public void setCurrentQuestionType(String currentQuestionType) {
        this.currentQuestionType = currentQuestionType;
    }

    public String getCurrentQuestionText() {
        return currentQuestionText;
    }

    public void setCurrentQuestionText(String currentQuestionText) {
        this.currentQuestionText = currentQuestionText;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRemainingMinutes() {
        return remainingMinutes;
    }

    public void setRemainingMinutes(int remainingMinutes) {
        this.remainingMinutes = remainingMinutes;
        if (remainingMinutes < 5) this.timerCss = "redText";
    }

    public String getStartDateFormatted() {
        return startDateFormatted;
    }

    public int getUnansweredCount() {
        return unansweredCount;
    }

    public void setUnansweredCount(int unansweredCount) {
        this.unansweredCount = unansweredCount;
    }

    public String getTableCss() {
        return tableCss;
    }

    public void setTableCss(String tableCss) {
        this.tableCss = tableCss;
    }

    public boolean isRerun() {
        return rerun;
    }

    public void setRerun(boolean rerun) {
        this.rerun = rerun;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public void setStartTimeString(String startTimeString) {
        this.startTimeString = startTimeString;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionNR) {
        this.questionIndex = questionNR;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
        this.startDateFormatted = new SimpleDateFormat("HH:mm 'on' d MMM yyyyy").format(startTime);
    }

    public void incrementQuestionIndex() {
        this.questionIndex++;
    }

    public void decrementQuestionIndex() {
        this.questionIndex--;
    }

    public void setQuizOrder(int[] quizOrder) {
        this.quizOrder = quizOrder;
    }

    public int[] getQuizOrder() {
        return quizOrder;
    }

    public DateFormat getFormatter() {
        return formatter;
    }

    public void setFormatter(DateFormat formatter) {
        this.formatter = formatter;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

}

