package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class WebController {

    //instantiez candidate repository prin autowired
    @Autowired
    private CandidateRepository repository;
    Query query=new Query();
    Date startTimeObj= new Date();

    //mapping pentru prima lansarea quizzului.
    @RequestMapping("welcome")
    public String greetingForm(Model model) {

        //construiesc un obiect formdata
        Formdata formdata= new Formdata();
        formdata.setQuestionNR(1);

        //pun form data in Model
        model.addAttribute("formdata", formdata);
        System.out.println("********************* MODEL:"+model.toString());
        return "welcome";
    }

    //mapping pentru stergere baza de date
    @RequestMapping("dbdelete")
    public String dbdelete(Model model) {
        repository.deleteAll();
        return "dbdelete";
    }

    //cand vine un raspuns prin POST
    @PostMapping("/query")
    public String greetingSubmit(@ModelAttribute("formdata") Formdata formdata, Model model) {

        //numele default al paginii afisate
        String page="query";

        String allAnswers, unupdatedAnswers;

        int unupdatedAnswersLength=0;
        long startTime=startTimeObj.getTime();

        System.out.println("startTime primit cu Formdata in Query:"+formdata.getStartTime());
        System.out.println("nume primit cu Formdata in Query:"+formdata.getFirstname());
        System.out.println("prenume primit cu Formdata in Query:"+formdata.getLastname());
        Candidate unupdatedCandidate = repository.findCandidateByFirstNameAndAndLastName(formdata.getFirstname(), formdata.getLastname());
        //daca exista deja candidatul
        if (unupdatedCandidate!=null) {
            unupdatedAnswers = unupdatedCandidate.answers;
            unupdatedAnswersLength=unupdatedAnswers.length();
            //si daca a dat toate raspunsurile
            if (unupdatedAnswersLength+1 == Config.QUERY_LENGTH ) {
                //pregatesc afisarea paginii de rezultate.
                page = "results";
                System.out.println("Unupdated answers length: "+unupdatedAnswersLength);
            }
        }
        //incrementez numarul intrebarii
        formdata.incrementQuestionNr();

        System.out.println("Fac update la raspunsuri si salvez in DB. ");
        updateAnswers(formdata.getFirstname(), formdata.getLastname(), formdata.getAnswer(), startTime);

         //reconstruiesc obietul candidat, dupa update, ca sa vad daca exista intrebari fara raspuns
        Candidate updatedCandidate = repository.findCandidateByFirstNameAndAndLastName(formdata.getFirstname(),formdata.getLastname());
        allAnswers=updatedCandidate.answers;
        startTime=updatedCandidate.getStartTime();
        System.out.println("All answers pentru candidatul actual: "+allAnswers+" Lungime: "+allAnswers.length());

        formdata.setStartTime(startTime);
        //salvez noul formdata in obiectul Model
        model.addAttribute("formdata", formdata);

        //golesc setAnswer ca sa nu se bifeze radiobuttonul intrebarii anterioare
        formdata.setAnswer(null);

        int positionOfZero;
        //daca am trecut prin toate intrebarile, si este vreuna cu raspuns 0 nu voi afisa rezultatele,
        // ci voi reafisa intrebarile fara raspuns
        if (allAnswers.length()==Config.TOTAL_QUESTIONS && allAnswers.contains("0")){
            positionOfZero=allAnswers.indexOf("0")+1;
            formdata.setQuestionNR(positionOfZero);
            System.out.println("Question nr set to "+positionOfZero);
            //salvez noul formdata in obiectul Model
            model.addAttribute("formdata", formdata);
            page="rerun";
        }
        //de pus aici o conditie pt cazul cand nu mai e niiucn zero
        if (allAnswers.length()==Config.TOTAL_QUESTIONS && !allAnswers.contains("0")){
            page="results";
        }

        model.addAttribute("allAnswers", allAnswers);
        return page;
    }

    //afisare content baza de date
    @RequestMapping("/dbcontent")
    public String reports(Model model) {
        String page = "dbcontent";
        Logic tools=new Logic();

        String allAnswers="";

        for (Candidate candidate : repository.findAll()) {
            allAnswers+=candidate.getFirstName()+" "+candidate.getLastName()+":\n"
            + tools.evaluate(candidate.answers)+".\n\n";
        }

        model.addAttribute("dbContent", allAnswers);
        return page;
    }

    //adauga raspunsul actual la cele anterioare si salveaza in baza de date, la candidatul din formularul actual
    public void updateAnswers(String FirstName, String Lastname, String answer, long startTime){
        //caut persoana dupa nume
        Candidate person = repository.findCandidateByFirstNameAndAndLastName(FirstName, Lastname);
        String updatedAnswers;

        //daca prenumele nu exista in Mongo, creez un nou candidat cu datele primite de la quizz
        if (person==null){
            repository.save(new Candidate(FirstName, Lastname, answer, startTime));
            System.out.println(FirstName+Lastname+" created.");
        }

        //daca persoana exista in DB
           else         {
                //fac update la raspunsuri
                //daca sunt mai putine raspunsuri decat numarul maxim de intrebari adaug raspunsul in coada
                if (person.getAnswers().length()<Config.TOTAL_QUESTIONS){
                    person.addAnswer(answer);
                    System.out.println("Answer added to person.");
                }
                else
                    //daca s-a raspuns la toate intrebarile, facem inlocuiri in stringul cu raspunsuri
                    if (person.getAnswers().length()==Config.TOTAL_QUESTIONS){
                    if (answer==null){
                        answer="0";
                    }

                    //daca si a doua oara raspunde cu 0, alocam un non raspuns - altul decat null
                    if (answer=="0"){
                        answer="N";
                    }

                    Logic tools=new Logic();
                    updatedAnswers=tools.replaceCharAt(person.getAnswers(), person.getAnswers().indexOf("0"), answer );
                    person.setAnswers(updatedAnswers);
                    System.out.println("Answers updated by replacement: "+updatedAnswers);
                    }

                //salvez datele persoanei
                repository.save(person);
                System.out.println("Answers saved to mongo.");
            }
        }
}
