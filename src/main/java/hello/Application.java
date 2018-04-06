package hello;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import static hello.Config.QUERY_LENGTH;

@SpringBootApplication
class Application /*implements CommandLineRunner */{


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    //TODO: - afisare buton ANSWER LATER (intrebarea ba primi answer 0)
    //TODO: - afisarea linkurilor catre intrebarile la care nu s-a raspuns
    //TODO: - repara timerul
    //TODO: - trimite mail cu rezultatele unui quiz
    //TODO: - interfata admin (vizualizeaza raspunsuri, sterge candidat, seteaza admini care sa primeasca mail)

}