package hello;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import static hello.Config.QUERY_LENGTH;

@SpringBootApplication
class Application /*implements CommandLineRunner */{

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
// TODO Faza 1
// TODO: -excel/csv pentru intrebarile tip text
// TODO: -afisare rezultate pe categorii
// TODO: -java script cu timer - si cu call http cand expira timpul
// TODO: -metoda de randomizare poate fi imbunatatita

// TODO Faza 2
// TODO: - session management cu login initial
// TODO: - adaugare drepturi la user / admin
// TODO: - salvare intrebari in baza de date (excel si imagini)
// TODO: - interfata admin pentru modificare intrebari
// TODO: - editare categorii (CRUD)