package hello;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class Application /*implements CommandLineRunner */{

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
// TODO Faza 1
// TODO: de pus intrebarile in mongod: colectie de date cu intrebarile
//      -fiecare obiect contine
//          - id unic
//          - fisierul foto(poate fi null)
//          - sau textul(poate fi null)
//          - numarul de variante de raspuns
//          - categoria intrebarii
//          - raspunsul corect (poate fi null)
// TODO: -afisare rezultate pe categorii
// TODO: -java script cu timer - si cu call http cand expira timpul
// TODO: -metoda de randomizare poate fi imbunatatita

// TODO Faza 2
// TODO: - session management cu login initial
// TODO: - adaugare drepturi la user / admin
// TODO: - salvare intrebari in baza de date (excel si imagini)
// TODO: - interfata admin pentru modificare intrebari
// TODO: - editare categorii (CRUD)