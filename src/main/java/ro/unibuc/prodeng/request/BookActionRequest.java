package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.NotBlank;

public record BookActionRequest(
        @NotBlank(message = "Emailul utilizatorului este obligatoriu") 
        String userEmail
) {}