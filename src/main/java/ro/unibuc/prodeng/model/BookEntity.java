package ro.unibuc.prodeng.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
public record BookEntity(
    @Id String id,
    String title,
    boolean borrowed,
    String borrowerUserId
) {}