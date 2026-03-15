package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.NotBlank;

public record EditBookRequest(
    @NotBlank(message = "Title is required")
    String title
) {}