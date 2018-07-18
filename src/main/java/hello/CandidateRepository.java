package hello;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CandidateRepository extends MongoRepository<Candidate, String>{

    Candidate findCandidateByFirstNameAndAndLastName(String firstName, String lastName, String password);

}



