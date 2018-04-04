package hello;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CandidateRepository extends MongoRepository<Candidate, String>{

    public Candidate findCandidateByFirstNameAndAndLastName(String firstName, String lastName);
    public List<Candidate>  findByLastName(String lastName);

}



