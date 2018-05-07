package hello;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.query.Query;

import static hello.Config.QUERY_LENGTH;

//clasa candidata care contine datele de contact si un string cu raspunsurile lui
public class Candidate {

    @Id
    public String id;

    public String firstName;
    public String lastName;
    public int[] answers;
    int[] order = new int[QUERY_LENGTH];

    Query query = new Query();
    long startTime;

    //constructor simplu
    public Candidate() {
    }

    public Candidate(String firstName, String lastName, int[] answers, long startTime, int[] order) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.answers = answers;
        this.startTime = startTime;
        this.order = order;
    }

    //fac update la answers
    public void addAnswer(int index, Integer newAnswer) {

        if ((newAnswer == null)) {
            newAnswer = 0;
        }
        this.answers[index] = newAnswer;
    }

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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int[] getOrder() {
        return order;
    }

    public void setOrder(int[] order) {
        this.order = order;
    }

}
