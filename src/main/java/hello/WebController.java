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

import static hello.Tools.unansweredCount;
import static hello.Tools.updateJumpButtons;

@Controller
@SessionAttributes({"formdata", "jumpButtons"})
public class WebController {
    Tools tools = new Tools();

    //instantiez candidate repository prin autowired
    @Autowired
    private CandidateRepository repository;

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

        //build formdata object which stores and handles all form data
        Formdata formdata = new Formdata();

        //build jumpButtons object and populate with initial webcontent objects;
        List<WebContent> jumpButtons = tools.buildJumbbuttonList();

        //initialize questions index
        formdata.setQuestionIndex(0);

        //randomize order of questions and store it in formdata
        formdata.setQuizOrder(tools.randomize());

        //place formdata in framework Model
        model.addAttribute("formdata", formdata);
        model.addAttribute("jumpButtons", jumpButtons);

        //return welcome page
        return "welcome";
    }

    @PostMapping("/query")
    public String queryController(@ModelAttribute("formdata") Formdata formdata,
                                  @ModelAttribute("jumpButtons") ArrayList<WebContent> jumpButtons,
                                  Model model) {

        //default html template to be displayed
        String page = "query";

        //creates candidate if first iteration
        //update answers array based on formdata, and store it in Mongo DB.
        updateAnswers(formdata);

        //setting current question as the "n"-th question in the pre-established order,
        //where "n" is formdata.getQuestionIndex()
        int[] orderOfQuestions = formdata.getQuizOrder();
        int currentQuestion = orderOfQuestions[(formdata.getQuestionIndex())];
        formdata.setCurrentQuestion(currentQuestion);
        System.out.println("+++++++++++++++++++++++++question index is at  " + formdata.getQuestionIndex());

        //updates question index depending on user request: answer, skip, previous or jump.
        formdata.updateQuestionIndex();

        //query updated Candidate object in DB
        Candidate updatedCandidate = repository.findCandidateByFirstNameAndAndLastName(formdata.getFirstname(), formdata.getLastname());

        //set setAnswer equal to current question answer so its radio button is checked
        int[] allAnswers = updatedCandidate.answers;
        formdata.setAnswer(Integer.toString(allAnswers[formdata.getQuestionIndex()]));

        //update jumpButtons, based on answers so far
        jumpButtons = updateJumpButtons(allAnswers, formdata.getQuestionIndex(), jumpButtons);
        int unansweredQuestions = unansweredCount(allAnswers);

        //time counter management
        int remaining = formdata.getRemainingTime(updatedCandidate.getStartTime());
        //if time runs out the thankyou page will be displayed
        if (remaining < 0) page = "thankyou";

        model.addAttribute("formdata", formdata);
        model.addAttribute("allAnswers", allAnswers);
        model.addAttribute("jumpButtons", jumpButtons);
        model.addAttribute("unansweredQuestions", unansweredQuestions);
        return page;
    }

    //afisare content baza de date
    @RequestMapping("/dbcontent")
    public String reports(Model model) {
        String page = "dbcontent";
        Tools tools = new Tools();
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
        }
        //daca persoana exista
        else {
            //citesc raspunsurile anterioare
            int[] allAnswers = person.answers;
            //if the answer is not "skip" nor "jump" nor "previous"
            if (!formdata.getAnswer().equals("skip")
                    && !formdata.getAnswer().equals("jump")
                    && !formdata.getAnswer().equals("previous"))
                allAnswers[formdata.getQuestionIndex()] = Integer.parseInt(formdata.getAnswer());
            person.setAnswers(allAnswers);
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
