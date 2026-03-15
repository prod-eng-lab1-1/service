package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateBookRequest(
    @NotBlank(message = "Title is required")
    String title,

    @Email(message = "Invalid email format")
    @NotBlank(message = "Borrower email is required")
    String borrowerEmail
) {}