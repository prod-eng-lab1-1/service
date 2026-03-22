package ro.unibuc.prodeng.response;

public record BookResponse(
        String id,
        String title,
        int totalCopies,
        int availableCopies,
        int queueSize
) {}