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
    public int[] answers = new int[QUERY_LENGTH];
    int[] order = new int[QUERY_LENGTH];
    Query query = new Query();
    Date startTime;

    //constructor simplu
    public Candidate() {
    }

    public Candidate(String firstName, String lastName, Date startTime, int[] order) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.startTime = startTime;
        this.order = order;

        //initializez variabila answers
        for (int i = 0; i < QUERY_LENGTH; i++) {
            answers[i] = 0;
        }
    }

    //fac update la answers
    public void addAnswer(int index, Integer newAnswer) {
        if ((newAnswer == null)) {
            newAnswer = 0;
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

    public int[] getAnswers() {
        return answers;
    }

    public void setAnswers(int[] answers) {
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
