package hello;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CandidateRepository extends MongoRepository<Candidate, String>{

    //livreaza candidatul dupa firstname
    public Candidate findByFirstName(String firstName);

    //livreaza candidatul dupa lastname
    public List<Candidate>  findByLastName(String lastName);

}



