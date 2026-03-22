package ro.unibuc.prodeng.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "books")
public record BookEntity(
        @Id String id,
        String title,
        int totalCopies,
        int availableCopies,
        List<String> borrowerIds,
        List<String> reservationQueue
) {}