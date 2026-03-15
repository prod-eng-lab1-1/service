package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AssignBookRequest(
    @Email(message = "Invalid email format")
    @NotBlank(message = "New borrower email is required")
    String newBorrowerEmail
) {}