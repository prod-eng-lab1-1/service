package ro.unibuc.prodeng.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

public record CreateBookRequest(
        @NotBlank String title,
        @Min(1) int copies
) {}