package hello;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;

import static hello.Config.QUERY_LENGTH;

//clasa candidata care contine datele de contact si un string cu raspunsurile lui
public class Candidate {

    @Id
    public String id;
    public String firstName;
    public String lastName;
    public String password;
    public String[] answers = new String[QUERY_LENGTH];
    int[] order = new int[QUERY_LENGTH];
    Query query = new Query();
    Date startTime;
    int remainingtime;

    public int getRemainingtime() {
        return remainingtime;
    }

    public void setRemainingtime(int remainingtime) {
        this.remainingtime = remainingtime;
    }

    //constructor simplu
    public Candidate() {
    }

    public Candidate(Formdata formdata) {
        this.firstName = formdata.getFirstname();
        this.lastName = formdata.getLastname();
        this.password=formdata.getPassword();
        this.startTime = formdata.getStartTime();
        this.order = formdata.getQuizOrder();
        //initializez variabila answers
        for (int i = 0; i < QUERY_LENGTH; i++) {
            answers[i] = "0";
        }
    }

    //fac update la answers
    public void addAnswer(int index, String newAnswer) {
        if ((newAnswer == null)) {
            newAnswer = "0";
        }
        this.answers[index] = newAnswer;
    }


    // SETTERS and GETTERS

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int[] getOrder() {
        return order;
    }

    public void setOrder(int[] order) {
        this.order = order;
    }

}
