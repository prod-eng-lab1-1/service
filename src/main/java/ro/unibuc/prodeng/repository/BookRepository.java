package ro.unibuc.prodeng.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ro.unibuc.prodeng.model.BookEntity;

@Repository
public interface BookRepository extends MongoRepository<BookEntity, String> {

    List<BookEntity> findByBorrowerUserId(String borrowerUserId);
}