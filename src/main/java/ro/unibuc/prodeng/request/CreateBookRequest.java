package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateBookRequest(
        @NotBlank(message = "Titlul nu poate fi gol") String title,
        @Min(value = 1, message = "Trebuie sa adaugi cel putin o copie") int copies
) {}