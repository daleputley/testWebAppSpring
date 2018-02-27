package hello;

public class Formdata {

    private int questionNR;
    private String answer;
    private String firstname, lastname;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getQuestionNR() {
        return questionNR;
    }

    public void setQuestionNR(int questionNR) {
        this.questionNR = questionNR;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {

        this.answer = answer;
    }

    public void incrementQuestionNr(){
        this.questionNR++;
    }
}
