package hello;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static hello.Config.QUERY_LENGTH;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Formdata {

    private int questionIndex;                      //the index of the question
    private int currentQuestion;                    //the current question
    private String answer;                          //answer sent via form
    private String firstname, lastname, password;   //user details
    private String currentQuestionType;             //it can be image or txt
    private String currentQuestionText;             //content of text questions
    private String currentQuestionFileName;         //the name of the file containing current question
    private String startTimeString;                 // time when the query started
    private Date startTime;                         // time when query started
    private List<String> answerOptions;             // how many options each question has
    private int[] quizOrder = new int[QUERY_LENGTH];// the order of the questions in the query
//    private String tableCss = "display:;";          //css for
    DateFormat formatter = new SimpleDateFormat("HH:mm");// date format
    private String startDateFormatted;              //the string containing the starting time
    private int remainingMinutes;                   //remaining time for user to complete quizz
    public String timerCss = "";                    //CSS for string showing time
    int unansweredCount;                            // how many questions unanswered


    public void updateQuestionIndex() throws IOException {
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
        //set various values depending on current question index
        updateQuestionFileDetails();
    }


    private void updateQuestionFileDetails() throws IOException {
        //setting current question as the "n"-th question of the pre-established order,
        //where "n" is formdata.getQuestionIndex()
        int[] orderOfQuestions = getQuizOrder();
        int currentQuestion = orderOfQuestions[(getQuestionIndex())];
        File currentQuestionFile = Tools.getCurrentQuestionFile(currentQuestion);

        //identify the question file in the resource folder, and set its name in the formdata object
        setCurrentQuestionFileName(currentQuestionFile.getName());

        //identify the type of the question (text or image) and store the type in the formdata object
        setCurrentQuestionType(currentQuestionFile);

        //-----------------------------get number of possible answers for current question
        int numberOfOptions = Integer.parseInt(getCurrentQuestionFileName().substring(2, 3));
        List<String> listOfOptions = new ArrayList<>();
        for (int i = 0; i < numberOfOptions; i++) {
            listOfOptions.add(Integer.toString(i + 1));
        }
        setAnswerOptions(listOfOptions);
    }

    public int getRemainingTime(Date startTime) {
        setStartTime(startTime);
        Date currenttime = new Date();
        int duration = (int) ((currenttime.getTime() - startTime.getTime()) / 1000);
        int remaining = (60 * Config.TIME_MINUTES) - duration;
        setRemainingMinutes((int) Math.round(remaining / 60.00));
        return remaining;
    }

    //------------------------------------------- Getters and Setters ---------------------------------


    public List<String> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<String> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public String getCurrentQuestionFileName() {
        return currentQuestionFileName;
    }

    public void setCurrentQuestionFileName(String currentQuestionFileName) {
        this.currentQuestionFileName = currentQuestionFileName;
    }

    public String getCurrentQuestionType() {
        return currentQuestionType;
    }

    public void setCurrentQuestionType(String currentQuestionType) {
        this.currentQuestionType = currentQuestionType;
    }

    public void setCurrentQuestionType(File currentQuestionFile) throws IOException {
        if (!currentQuestionFileName.endsWith(".txt"))
            setCurrentQuestionType("image");
        else {
            setCurrentQuestionType("text");
            byte[] encoded = Files.readAllBytes(Paths.get(currentQuestionFile.getAbsolutePath()));
            String fileText = new String(encoded, UTF_8);
            setCurrentQuestionText(fileText);
        }

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

//    public String getTableCss() {
//        return tableCss;
//    }

//    public void setTableCss(String tableCss) {
//        this.tableCss = tableCss;
//    }

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

