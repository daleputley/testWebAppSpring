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
    public String answers = "";
    int[] order = new int[QUERY_LENGTH];


    Query query = new Query();
    long startTime;

    //constructor simplu
    public Candidate() {
    }

    public Candidate(String firstName, String lastName, String answers, long startTime, int[] order) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.answers = answers;
        this.startTime = startTime;
        this.order = order;
    }

    //adaug un raspuns la ce s-a adaugat pana acum
    public void addAnswer(String newAnswer) {
        if ((newAnswer == null) || (newAnswer == "")) {
            newAnswer = "0";
        }

        //daca stringul nu e gol
        System.out.println("Daca stringul nu e gol il incrementez ");
        if (this.answers != "") {
            this.answers += newAnswer;
        }
        //daca e gol
        else {
            this.answers = newAnswer;
        }
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

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
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
