package hello;

import com.sun.xml.internal.fastinfoset.util.StringArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static hello.Config.TIME_MINUTES;
import static hello.Tools.unansweredCount;
import static hello.Tools.updateJumpButtons;
import static java.nio.charset.StandardCharsets.UTF_8;

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
        List<WebContent> jumpButtons = tools.buildJumbbuttonList();

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
                                  @ModelAttribute("jumpButtons") ArrayList<WebContent> jumpButtons,
                                  Model model) throws IOException {

        //default html template to be displayed
        String page = "query";

        //creates candidate if first iteration
        //update answers array based on formdata, and store it in Mongo DB.
        updateAnswers(formdata);

        //setting current question as the "n"-th question of the pre-established order,
        //where "n" is formdata.getQuestionIndex()
        int[] orderOfQuestions = formdata.getQuizOrder();
        int currentQuestion = orderOfQuestions[(formdata.getQuestionIndex())];

        //TODO improve selection of matching filename
        // get filename based on question index
        File dir = new ClassPathResource("/static/questions").getFile();
        File[] matchingFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(Integer.toString(currentQuestion));
            }
        });
        File currentQuestionFile=matchingFiles[0];
        formdata.setCurrentQuestionFileName(currentQuestionFile.getName());
        //TODO add text questions
        //TODO add question type interpreter
        if (!formdata.getCurrentQuestionFileName().endsWith(".txt"))
            formdata.setCurrentQuestionType("image");
        else {
            formdata.setCurrentQuestionType("text");
            byte[] encoded = Files.readAllBytes(Paths.get(currentQuestionFile.getAbsolutePath()));
            String fileText=new String (encoded, UTF_8);
            formdata.setCurrentQuestionText(fileText);
        }

        //-----------------------------get number of possible answers for current question
        int numberOfOptions=Integer.parseInt(formdata.getCurrentQuestionFileName().substring(2,3));
        System.out.println("---------------------- This question has options: "+numberOfOptions);
        List<String> listOfOptions= new ArrayList<>();
        for (int i=0; i<numberOfOptions; i++){
            listOfOptions.add(Integer.toString(i+1));
        }
        formdata.setAnswerOptions(listOfOptions);

        System.out.println("+++++++++++++++++++++++++ question index is at  " + formdata.getQuestionIndex());

        //updates question index depending on user request: answer, skip, previous or jump.
        formdata.updateQuestionIndex();

        //query updated Candidate object in DB
        Candidate updatedCandidate = repository.findCandidateByFirstNameAndAndLastName(formdata.getFirstname(), formdata.getLastname(), formdata.getPassword());

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
            int[] allAnswers = person.answers;
            //if the answer is not "skip" nor "jump" nor "previous"
            if (
                    !formdata.getAnswer().equals("skip")
                            && !formdata.getAnswer().equals("jump")
                            && !formdata.getAnswer().equals("previous"))
                allAnswers[formdata.getQuestionIndex()] = Integer.parseInt(formdata.getAnswer());
            person.setAnswers(allAnswers);
            //person.setStartTime(formdata.getStartTime());
            person.setRemainingtime(formdata.getRemainingTime(formdata.getStartTime()));
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
