package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static hello.Config.QUERY_LENGTH;
import static hello.Logic.updateRows;

@Controller

@SessionAttributes({"formdata", "rows"})
public class WebController {

    //instantiez candidate repository prin autowired
    @Autowired
    private CandidateRepository repository;

    //Query query = new Query();
    Date startTimeObj = new Date();

    //mapping pentru prima lansarea quizzului.
    @RequestMapping("welcome")
    public String greetingForm(Model model) {
        System.out.println("****************************WELCOME. FIRST ITERATION***************************");

        //construiesc un obiect formdata - contine datele transmise prin formularul de intrebari
        Formdata formdata = new Formdata();

        //construiesc obiect rows si il populez cu obiecte webcontent initiale;
        List<WebContent> rows = new ArrayList<>();
        for (int i = 0; i < QUERY_LENGTH; i++) {
            WebContent webContent = new WebContent();
            webContent.setIndex(i);
            webContent.setAnswerStatus("not answered");
            rows.add(webContent);
        }

        //initializez contorul intrebarilor
        formdata.setQuestionIndex(1);

        //randomizez ordinea intrebarilor si o memorez in formdata
        formdata.setQuizOrder(Logic.randomize());

        int[] order = formdata.getQuizOrder();
        //stabilesc prima intrebare care va fi afisata: prima din array-ul order[]
        formdata.setCurrentQuestion(order[0]);

        //debug log
        System.out.print("Ordinea intrebarilor, salvata in formdata: ");
        for (int i = 0; i < QUERY_LENGTH; i++) {
            System.out.print(order[i]);
        }
        System.out.println(" ");

        //debug log
        //System.out.println("Cand Welcome e afisat, currentQuestion= " + formdata.getCurrentQuestion());
        //System.out.println("Cand Welcome e afisat, questionNR= " + formdata.getQuestionIndex());

        //pun formdata in Model
        model.addAttribute("formdata", formdata);
        model.addAttribute("rows", rows);
        return "welcome";
    }

    //**************************************POST mapping - QUERY page**************************

    @PostMapping("/query")
    public String greetingSubmit(@ModelAttribute("formdata") Formdata formdata,
                                 @ModelAttribute("rows") ArrayList<WebContent> rows,
                                 Model model) {

        //numele default al paginii afisate
        String page = "query";
        //string cu toate raspunsurile de pana la momentul actual
        String allAnswers;
        //string cu raspunsurile inainte de salvare in DB
        int[] unupdatedAnswers;
        //daca nu s-a dat niciun raspuns pana acum
        int unupdatedAnswersLength = 0;
        long startTime = startTimeObj.getTime();
        int[] orderOfQuestions = formdata.getQuizOrder();

        //caut candidatul in DB, cu toate datele lui de pana acum
        Candidate unupdatedCandidate =
                repository.findCandidateByFirstNameAndAndLastName(
                        formdata.getFirstname(),
                        formdata.getLastname());

        //daca exista deja candidatul
        if (unupdatedCandidate != null) {
            //citesc raspunsurile lui anterioare
            unupdatedAnswers = unupdatedCandidate.answers;
        }

        //daca indexul intrebarilor nu a ajuns la limita stabilita prin QUERY_LENGTH
        if (formdata.getQuestionIndex() <= QUERY_LENGTH) {
            //stabilesc intrebarea care va fi afisata
            // questionNr - a cata intrebare din seria prestabilita - este si indexul array-ului Order[]
            formdata.setCurrentQuestion(orderOfQuestions[(formdata.getQuestionIndex() - 1)]);
            //System.out.println("*******La afisare pagina, currentQuestion= " + formdata.getCurrentQuestion());
            //incrementez numarul intrebarii, pentru afisarea urmatoare
            formdata.incrementQuestionIndex();
            //System.out.println("Dupa incrementare, inainte de afisare pagina, questionNr= " + formdata.getQuestionIndex());
        }

        //Fac update la raspunsurile primite acum si le salvez in DB.
        updateAnswers(formdata.getFirstname(), formdata.getLastname(), formdata.getAnswer(), startTime, orderOfQuestions);

        //dupa update reconstruiesc obiectul Candidate ca sa vad daca exista intrebari fara raspuns
        Candidate updatedCandidate = repository.findCandidateByFirstNameAndAndLastName(formdata.getFirstname(), formdata.getLastname());
        allAnswers = updatedCandidate.answers;
        startTime = updatedCandidate.getStartTime();
        System.out.println("***********All answers, as in DB, pentru candidatul actual: "
                + allAnswers + ". Lungime: " + allAnswers.length());

        formdata.setStartTime(startTime);

        //salvez noul formdata in obiectul Model
        model.addAttribute("formdata", formdata);

        //golesc setAnswer ca sa nu se bifeze radiobuttonul intrebarii anterioare
        formdata.setAnswer(null);

        //*****************************AM TRECUT PRIN TOATE INTREBARILE*******************

        int positionOfZero;
        //daca am trecut prin toate intrebarile, si este vreuna cu raspuns 0 nu voi afisa rezultatele,
        // ci voi reafisa intrebarile fara raspuns
        if (allAnswers.length() == Config.QUERY_LENGTH && allAnswers.contains("0")) {
            positionOfZero = allAnswers.indexOf("0") + 1;

            //stabilesc numarul intrebarii
            formdata.setQuestionIndex(positionOfZero);
            formdata.setCurrentQuestion(orderOfQuestions[positionOfZero - 1]);
            System.out.println("----------------------Question nr set to " + positionOfZero);
            System.out.println("----------------------Current Question set to " + orderOfQuestions[positionOfZero - 1]);
            //salvez noul formdata in obiectul Model
            model.addAttribute("formdata", formdata);
            page = "rerun";
        }

        //daca toate intrebarile au primit raspuns pregatesc pagina de rezultate
        if (allAnswers.length() == Config.QUERY_LENGTH && !allAnswers.contains("0")) {
            page = "results";
        }

        //aici lansez metoda care face update la ROWS, pe baza raspunsurilor date
        rows = updateRows(allAnswers, rows);

        model.addAttribute("allAnswers", allAnswers);
        model.addAttribute("rows", rows);
        return page;
    }

    //afisare content baza de date
    @RequestMapping("/dbcontent")
    public String reports(Model model) {
        String page = "dbcontent";
        Logic tools = new Logic();

        String allAnswers = "";

        for (Candidate candidate : repository.findAll()) {
            allAnswers += candidate.getFirstName() + " " + candidate.getLastName() + ":\n"
                    + tools.evaluate(candidate.answers) + ".\n\n";
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
    public void updateAnswers(String FirstName, String Lastname, String answer, long startTime, int[] order) {
        //caut persoana dupa nume
        Candidate person = repository.findCandidateByFirstNameAndAndLastName(FirstName, Lastname);
        String updatedAnswers;

        //daca persoana nu exista in DB, creez un nou candidat cu datele primite de la quizz
        if (person == null) {
            repository.save(new Candidate(FirstName, Lastname, answer, startTime, order));
            System.out.println(FirstName + Lastname + " created.");
        }

        //daca persoana exista in DB
        else {
            //fac update la raspunsuri
            //daca sunt mai putine raspunsuri decat numarul maxim de intrebari adaug raspunsul in coada
            if (person.getAnswers().length() < Config.QUERY_LENGTH) {
                person.addAnswer(answer);
                //System.out.println("Answer and questions-order added to person.");
            } else
                //daca s-a raspuns la toate intrebarile, facem inlocuiri in stringul cu raspunsuri
                if (person.getAnswers().length() == Config.QUERY_LENGTH) {
                    if ((answer == null) || (answer == "")) {
                        answer = "0";
                    }
                    Logic tools = new Logic();
                    updatedAnswers = tools.replaceCharAt(person.getAnswers(), person.getAnswers().indexOf("0"), answer);
                    person.setAnswers(updatedAnswers);
                    System.out.println("*******Answers updated by replacement: " + updatedAnswers);
                }

            //salvez datele persoanei
            repository.save(person);
            System.out.println("Answers saved to mongo.");
        }
    }
}
