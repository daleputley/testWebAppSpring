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
import static hello.Logic.unansweredCount;
import static hello.Logic.updateJumpButtons;

@Controller

@SessionAttributes({"formdata", "jumpButtons"})
public class WebController {

    //instantiez candidate repository prin autowired
    @Autowired
    private CandidateRepository repository;

    //Query query = new Query();
    Date startTimeObj = new Date();

    @RequestMapping("admin")
    public String adminPage(Model model) {
        return "admin";
    }

    @RequestMapping("thankyou")
    public String thankyou(Model model) {
        return "thankyou";
    }

    //mapping pentru prima lansarea quizzului.
    @RequestMapping("welcome")
    public String greetingForm(Model model) {

        System.out.println("****************************WELCOME. FIRST ITERATION***************************");

        //construiesc un obiect formdata - va contine datele transmise prin formularul de intrebari
        Formdata formdata = new Formdata();

        //construiesc obiect jumpButtons si il populez cu obiecte webcontent initiale;
        List<WebContent> jumpButtons = new ArrayList<>();
        for (int i = 0; i < QUERY_LENGTH; i++) {
            WebContent webContent = new WebContent();
            webContent.setIndex(i);
            webContent.setAnswerStatus("not answered");
            jumpButtons.add(webContent);
        }

        //initializez contorul intrebarilor
        formdata.setQuestionIndex(0);

        //randomizez ordinea intrebarilor si o memorez in formdata
        formdata.setQuizOrder(Logic.randomize());

        //debug log
        System.out.print("Ordinea intrebarilor, salvata in formdata: ");
        for (int i = 0; i < QUERY_LENGTH; i++) {
            System.out.print(formdata.getQuizOrder()[i]);
        }
        System.out.println(" ");

        //pun formdata in Model
        model.addAttribute("formdata", formdata);
        model.addAttribute("jumpButtons", jumpButtons);
        return "welcome";
    }

    //**************************************POST mapping - QUERY page**************************

    @PostMapping("/query")
    public String queryController(@ModelAttribute("formdata") Formdata formdata,
                                  @ModelAttribute("jumpButtons") ArrayList<WebContent> jumpButtons,
                                  Model model) {

        //numele default al paginii afisate
        String page = "query";

        int[] orderOfQuestions = formdata.getQuizOrder();

        System.out.println("********************** POST Query triggered ********************");
        System.out.print("----------------------Preset order of questions: ");
        printArrayValues(orderOfQuestions);
        System.out.println("+++++++++++++++++++++++formdata.isRerun=" + formdata.isRerun());
        System.out.println("----------------------formdata.getQuestionIndex=" + formdata.getQuestionIndex());
        System.out.println("+++++++++++++++++++++++formdata.getAnswer=" + formdata.getAnswer());
        System.out.println("+++++++++++++++++++++++jumpbuttons array size: =" + jumpButtons.size());

        //Fac update la raspunsurile primite acum cu formdata si le salvez in DB.
        //daca nu s-a apasat skip sau jump
        updateAnswers(formdata);

        //if user did not press "jump"
        if (formdata.getAnswer() != "-2"){
            formdata.incrementQuestionIndex();
            System.out.println("+++++++++++++++++++++++++question index incremented to: " + formdata.getQuestionIndex());
        }
        else {
            formdata.setCurrentQuestion(orderOfQuestions[(formdata.getQuestionIndex())]);
        }


//        //daca indexul intrebarilor nu a ajuns la limita stabilita prin QUERY_LENGTH
//        if (formdata.getQuestionIndex() < QUERY_LENGTH && !formdata.isRerun()) {
//            //stabilesc intrebarea care va fi afisata
//            formdata.setCurrentQuestion(orderOfQuestions[(formdata.getQuestionIndex())]);
//        }

        //cand ajung prima data la lungimea formularului, setez rerun=true
        if (formdata.getQuestionIndex() == QUERY_LENGTH) {
            formdata.setRerun(true);
        }

        //salvez noul formdata in obiectul Model
        model.addAttribute("formdata", formdata);

        //golesc setAnswer ca sa nu se bifeze radiobuttonul intrebarii anterioare
        formdata.setAnswer(null);

        //dupa update reconstruiesc obiectul Candidate ca sa vad daca exista intrebari fara raspuns
        Candidate updatedCandidate = repository.findCandidateByFirstNameAndAndLastName(formdata.getFirstname(), formdata.getLastname());
        int[] allAnswers = updatedCandidate.answers;

        //daca am trecut prin toate intrebarile, si este vreuna cu raspuns 0 nu voi afisa rezultatele,
        // ci voi reafisa intrebarile fara raspuns
        int positionOfFirstZero = getIndexOfFirstZero(allAnswers);
        if (formdata.isRerun() && positionOfFirstZero != -1) {
            //stabilesc numarul intrebarii
            formdata.setQuestionIndex(positionOfFirstZero);
            formdata.setCurrentQuestion(orderOfQuestions[positionOfFirstZero]);
            //salvez noul formdata in obiectul Model
            model.addAttribute("formdata", formdata);
        }

        //fac update la jumpButtons, pe baza raspunsurilor date pana acum
        System.out.println("allAnswers array: ");
        printArrayValues(allAnswers);
        jumpButtons = updateJumpButtons(allAnswers, jumpButtons);
        int unansweredQuestions = unansweredCount(allAnswers);

        model.addAttribute("allAnswers", allAnswers);
        model.addAttribute("jumpButtons", jumpButtons);
        model.addAttribute("unansweredQuestions", unansweredQuestions);
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
    public void updateAnswers(Formdata formdata) {

        //caut persoana dupa nume
        Candidate person = repository.findCandidateByFirstNameAndAndLastName(formdata.getFirstname(), formdata.getLastname());

        //daca persoana nu exista in DB, creez un nou candidat cu datele primite de la quizz
        if (person == null) {
            Date startTime = new Date();
            Candidate newPerson = new Candidate(formdata.getFirstname(), formdata.getLastname(), startTime, formdata.getQuizOrder());
            newPerson.addAnswer(0, null);
            newPerson.setOrder(formdata.getQuizOrder());
            repository.save(newPerson);
            System.out.println(newPerson.getFirstName() + " has been created.");
            System.out.print("His answers so far:");
            printArrayValues(newPerson.answers);
        //daca persoana exista
        } else {
            //citesc raspunsurile anterioare
            int[] allAnswers = person.answers;
            //fac update la raspunsuri
            //daca raspunsul nu este egal cu -1 (skip) sau -2 (jump)
            if (!formdata.getAnswer().equals("-1") && !formdata.getAnswer().equals("-2") )
                allAnswers[formdata.getQuestionIndex()] = Integer.parseInt(formdata.getAnswer());
            person.setAnswers(allAnswers);
            printArrayValues(allAnswers);
            repository.save(person);
            System.out.print("----------------------All answers now updated to: ");
            printArrayValues(person.answers);
        }
    }

    public int getIndexOfFirstZero(int[] allAnswers) {
        int result = -1;

        for (int i = 0; i < allAnswers.length; i++) {
            if (allAnswers[i] == 0) {
                result = i;
                break;
            }
        }
        return result;
    }

    public void printArrayValues(int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + ". ");
        }
        System.out.println();
    }
}
