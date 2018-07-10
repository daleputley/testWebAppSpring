package hello;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static hello.Config.QUERY_LENGTH;

public class Formdata {

    //form data
    private int questionIndex;
    private int currentQuestion;
    private String answer;
    private String firstname, lastname;
    private long startTime;
    private String startTimeString;
    private int[] quizOrder = new int[QUERY_LENGTH];
    boolean rerun = false;
    public String tableCss ="display:;";
    public String buttonChecked="checked";
    DateFormat formatter = new SimpleDateFormat("HH:mm");
    private String dateFormatted;
    int unansweredCount;


    //------------------------------------------- Getters and Setters ---------------------------------


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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        dateFormatted = formatter.format(startTime);
        System.out.println(startTime);
    }

    public void incrementQuestionIndex() {
        this.questionIndex++;
    }

    public void decrementQuestionIndex() {
        this.questionIndex--;
    }

    public String getDateFormatted() {
        return dateFormatted;
    }

    public void setDateFormatted(String dateFormatted) {
        this.dateFormatted = dateFormatted;
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

