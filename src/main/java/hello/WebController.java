package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static hello.Config.QUERY_LENGTH;

@Controller
@SessionAttributes("formdata")
public class WebController {

    //instantiez candidate repository prin autowired
    @Autowired
    private CandidateRepository repository;
    Query query=new Query();
    Date startTimeObj= new Date();

    //mapping pentru prima lansarea quizzului.
    @RequestMapping("welcome")
    public String greetingForm(Model model) {

        //construiesc un obiect formdata - contine datele transmise prin formularul de intrebari
        Formdata formdata= new Formdata();
        //initializez contorul intrebarilor
        formdata.setQuestionNR(1);
        //randomizez ordinea intrebarilor si o memorez in formdata
        formdata.setQuizOrder(Logic.randomize());

        int[] order=formdata.getQuizOrder();
        //stabilesc prima intrebare care va fi afisata: prima din array-ul order[]
        formdata.setCurrentQuestion(order[0]);

        //debug log
        System.out.print("Ordinea intrebarilor, salvata in formdata: ");
        for (int i=0; i<QUERY_LENGTH; i++){
            System.out.print(order[i]);
        }
        System.out.println(" ");
        //debug log
        System.out.println("Cand Welcome e afisat, currentQuestion= "+formdata.getCurrentQuestion());
        System.out.println("Cand Welcome e afisat, questionNR= "+formdata.getQuestionNR());

        //pun formdata in Model
        model.addAttribute("formdata", formdata);
        return "welcome";
    }

    //cand vine un raspuns prin POST
    @PostMapping("/query")
    public String greetingSubmit(@ModelAttribute("formdata") Formdata formdata, Model model) {

        //numele default al paginii afisate
        String page="query";

        //string cu toate raspunsurile de pana la momentul actual
        String allAnswers;

        //string cu raspunsurile inainte de salvare in DB
        String unupdatedAnswers;

        int unupdatedAnswersLength=0;
        long startTime=startTimeObj.getTime();

        //debug logs
        //System.out.println("startTime primit cu Formdata in Query:"+formdata.getStartTime());
        //System.out.println("nume primit cu Formdata in Query:"+formdata.getFirstname());
        //System.out.println("prenume primit cu Formdata in Query:"+formdata.getLastname());
        System.out.println("Answer primit cu formdata:"+formdata.getAnswer());

        int[] orderOfQuestions = formdata.getQuizOrder();

        //debug log
        System.out.print("Ordinea intrebarilor, venita cu formdata:");
        for (int i=0; i<orderOfQuestions.length; i++){
            System.out.print(orderOfQuestions[i]);
        }
        System.out.println("");
        System.out.print("Indexul intrebarilor, venit cu formdata:"+formdata.getQuestionNR());
        System.out.println("");
        //PRELUCREZ DATELE PRIMITE prin POST **********************

        //caut candidatul in DB, cu toate datele lui de pana acum
        Candidate unupdatedCandidate = repository.findCandidateByFirstNameAndAndLastName(formdata.getFirstname(), formdata.getLastname());
        //daca exista deja candidatul
        if (unupdatedCandidate!=null) {
            //caut raspunsurile lui anterioare
            System.out.println("Am gasit candidatul in baza de date.");
            unupdatedAnswers = unupdatedCandidate.answers;
            System.out.println("Unupdated answers:"+unupdatedAnswers);
            //aflu cate raspunsuri a dat
            unupdatedAnswersLength=unupdatedAnswers.length();
            //iar daca a dat toate raspunsurile
            if (unupdatedAnswersLength+1 == QUERY_LENGTH ) {
                //pregatesc afisarea paginii de rezultate, in locul celei de query.
                page = "results";
                System.out.println("Unupdated answers length: "+unupdatedAnswersLength);
            }
        }
        //atata vreme cat indexul intrebarilor nu a ajuns la limita stabilita prin QUERY_LENGTH...
        if (formdata.getQuestionNR()<=QUERY_LENGTH) {
            //stabilesc intrebarea care va fi afisata - pe baza questionNr, care e indexul array-ului Order[]
            formdata.setCurrentQuestion(orderOfQuestions[(formdata.getQuestionNR() - 1)]);
            System.out.println("La afisare pagina, currentQuestion= " + formdata.getCurrentQuestion());

        //incrementez numarul intrebarii, pentru afisarea urmatoare
        formdata.incrementQuestionNr();
        System.out.println("Dupa incrementare, inainte de afisare pagina, questionNr= "+formdata.getQuestionNR());
        }

        //Fac update la raspunsurile primite acum si le salvez in DB.
        //System.out.println("Fac update la raspunsuri si salvez in DB sirul de raspunsuri. ");
        updateAnswers(formdata.getFirstname(), formdata.getLastname(), formdata.getAnswer(), startTime, orderOfQuestions);

        //dupa update reconstruiesc obiectul Candidate ca sa vad daca exista intrebari fara raspuns
        Candidate updatedCandidate = repository.findCandidateByFirstNameAndAndLastName(formdata.getFirstname(),formdata.getLastname());
        allAnswers=updatedCandidate.answers;
        startTime=updatedCandidate.getStartTime();
        System.out.println("All answers pentru candidatul actual: "+allAnswers+". Lungime: "+allAnswers.length());

        formdata.setStartTime(startTime);

        //salvez noul formdata in obiectul Model
        model.addAttribute("formdata", formdata);

        //golesc setAnswer ca sa nu se bifeze radiobuttonul intrebarii anterioare
        formdata.setAnswer(null);

        int positionOfZero;
        //daca am trecut prin toate intrebarile, si este vreuna cu raspuns 0 nu voi afisa rezultatele,
        // ci voi reafisa intrebarile fara raspuns
        if (allAnswers.length()==Config.QUERY_LENGTH && allAnswers.contains("0")){
            positionOfZero=allAnswers.indexOf("0")+1;
            formdata.setQuestionNR(positionOfZero);
            //System.out.println("Question nr set to "+positionOfZero);
            //salvez noul formdata in obiectul Model
            model.addAttribute("formdata", formdata);
            page="rerun";
        }
        //daca nutoate intrebarile au primit raspuns pregatesc pagina de rezultate
        if (allAnswers.length()==Config.QUERY_LENGTH && !allAnswers.contains("0")){
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

    //mapping pentru stergere baza de date
    @RequestMapping("dbdelete")
    public String dbdelete(Model model) {
        repository.deleteAll();
        return "dbdelete";
    }

    //adauga raspunsul actual la cele anterioare si salveaza in baza de date, la candidatul din formularul actual
    public void updateAnswers(String FirstName, String Lastname, String answer, long startTime, int[] order){
        //caut persoana dupa nume
        Candidate person = repository.findCandidateByFirstNameAndAndLastName(FirstName, Lastname);
        String updatedAnswers;

        //daca persoana nu exista in DB, creez un nou candidat cu datele primite de la quizz
        if (person==null){
            repository.save(new Candidate(FirstName, Lastname, answer, startTime, order));
            System.out.println(FirstName+Lastname+" created.");
        }

        //daca persoana exista in DB
           else         {
                //fac update la raspunsuri
                //daca sunt mai putine raspunsuri decat numarul maxim de intrebari adaug raspunsul in coada
                if (person.getAnswers().length()<Config.QUERY_LENGTH){

                    System.out.println("Answer and questions-order added to person.");
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
