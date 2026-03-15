package ro.unibuc.prodeng.response;

public record BookResponse(
    String id,
    String title,
    boolean borrowed,
    String borrowerName,
    String borrowerEmail
) {}