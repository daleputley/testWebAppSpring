package hello;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class Application /*implements CommandLineRunner */{


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

/*    @Override
    public void run(String... args) throws Exception {
        repository.deleteAll();
        //save a couple of candidates
        repository.save(new Candidate("Ion", "Popescu"));
        repository.save(new Candidate("Andrea","Mihailescu"));

        //fetch candidates:
            System.out.println("Customers found with findAll:");
            System.out.println("-----------------------------");
        for (Candidate candidate : repository.findAll()) {
            System.out.println(candidate);
        }
        System.out.println("Customer found with findByFirstName('Andrea'):");
        System.out.println("--------------------------------");
        System.out.println(repository.findByFirstName("Andrea"));
    }*/
}
