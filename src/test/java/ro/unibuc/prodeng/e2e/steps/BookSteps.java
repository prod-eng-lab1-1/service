package ro.unibuc.prodeng.e2e.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.response.BookResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookSteps {

    private static final String BASE_URL = "http://localhost:8080";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @When("the librarian adds a book named {string} with {int} copies")
    public void add_book(String title, int copies) {
        CreateBookRequest request = new CreateBookRequest(title, copies);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateBookRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(BASE_URL + "/api/books", entity, String.class);
    }

    @Then("the catalog should contain {string} with {int} available copies")
    public void verify_catalog(String expectedTitle, int expectedCopies) throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/api/books", String.class);
        
        List<BookResponse> books = objectMapper.readValue(response.getBody(), new TypeReference<List<BookResponse>>() {});

        boolean found = books.stream()
                .anyMatch(b -> b.title().equals(expectedTitle) && b.availableCopies() == expectedCopies);

        assertTrue(found, "Cartea " + expectedTitle + " cu " + expectedCopies + " copii nu a fost gasita in catalog!");
    }
}