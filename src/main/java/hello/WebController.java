package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static hello.Config.TIME_MINUTES;
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

        String page = "welcome";
        //build formdata object which stores and handles all form data
        Formdata formdata = new Formdata();

        //build jumpButtons object and populate with initial webcontent objects;
        List<JumpButtons> jumpButtons = tools.buildJumbbuttonList();

        //initialize questions index
        formdata.setQuestionIndex(0);

        //randomize order of questions and store it in formdata
        formdata.setQuizOrder(tools.randomize());

        //place formdata in framework Model
        model.addAttribute("formdata", formdata);
        model.addAttribute("jumpButtons", jumpButtons);

        //return welcome page
        return page;
    }

    @PostMapping("/query")
    public String queryController(@ModelAttribute("formdata") Formdata formdata,
                                  @ModelAttribute("jumpButtons") ArrayList<JumpButtons> jumpButtons,
                                  Model model) throws IOException {

        String page = "query";
        //this creates candidate if first iteration
        saveAnswersToDb(formdata);

        //updates question index depending on user request: answer, skip, previous or jump.
        //it also updates related values in formdata object
        formdata.updateQuestionIndex();

        //query updated Candidate object in DB
        Candidate updatedCandidate = repository.findCandidateByFirstNameAndAndLastName(formdata.getFirstname(), formdata.getLastname(), formdata.getPassword());

        //set setAnswer equal to current question answer so its radio button is checked
        String[] allAnswers = updatedCandidate.answers;
        formdata.setAnswer(allAnswers[formdata.getQuestionIndex()]);

        //update the CSS style of the jumpButtons, based on answers received so far
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
        List<String[]> allAnswers = new ArrayList<>();
        for (Candidate candidate : repository.findAll()) {
            String[] candidateAnswers = new String[3];
            candidateAnswers[0] = candidate.getFirstName();
            candidateAnswers[1] = candidate.getLastName();
            candidateAnswers[2] = tools.evaluate(candidate.answers, candidate.order);
            allAnswers.add(candidateAnswers);
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
    public void saveAnswersToDb(Formdata formdata) {

        //search for person by Name, Surname, Password
        Candidate person = repository.findCandidateByFirstNameAndAndLastName(formdata.getFirstname(), formdata.getLastname(), formdata.getPassword());
        //daca persoana nu exista in DB, creez un nou candidat cu datele primite de la quizz
        if (person == null) {
            Date startTime = new Date();
            Candidate newPerson = new Candidate(formdata);
            newPerson.addAnswer(0, null);
            newPerson.setOrder(formdata.getQuizOrder());
            newPerson.setRemainingtime(TIME_MINUTES);
            newPerson.setStartTime(startTime);
            repository.save(newPerson);
        }

        //daca persoana exista si nu a dat raspunsuri
        else if (null != formdata.getAnswer()) {
            //citesc raspunsurile anterioare
            String[] allAnswers = person.answers;
            //if the answer is not "skip" nor "jump" nor "previous"
            if (
                    !formdata.getAnswer().equals("skip")
                            && !formdata.getAnswer().equals("jump")
                            && !formdata.getAnswer().equals("previous"))
                allAnswers[formdata.getQuestionIndex()] = formdata.getAnswer();
            person.setAnswers(allAnswers);
            person.setRemainingtime(formdata.getRemainingTime(formdata.getStartTime()));
            repository.save(person);
//            System.out.print("----------------------All answers now updated to: ");
//            printArrayValues(person.answers);
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
