package ro.unibuc.prodeng.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ro.unibuc.prodeng.model.BookEntity;

public interface BookRepository extends MongoRepository<BookEntity, String> {
}