package hello;

import org.springframework.data.mongodb.repository.MongoRepository;
public interface CandidateRepository extends MongoRepository<Candidate, String>{
    Candidate findCandidateByFirstNameAndAndLastName(String firstName, String lastName, String password);
}



