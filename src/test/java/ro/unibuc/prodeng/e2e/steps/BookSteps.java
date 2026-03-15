package ro.unibuc.prodeng.e2e.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ro.unibuc.prodeng.model.UserEntity;
import ro.unibuc.prodeng.request.AssignBookRequest;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.response.BookResponse;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BookSteps {

    private static final String BASE_URL = "http://localhost:8080";

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResponseEntity<String> latestResponse;
    private final List<String> createdUserIds = new ArrayList<>();
    private final List<String> createdBookIds = new ArrayList<>();
    private String lastCreatedBookId;

    @After
    public void cleanup() {
        for (String bookId : createdBookIds) {
            try {
                restTemplate.delete(BASE_URL + "/api/books/" + bookId);
            } catch (Exception e) {}
        }
        createdBookIds.clear();

        for (String userId : createdUserIds) {
            try {
                restTemplate.delete(BASE_URL + "/api/users/" + userId);
            } catch (Exception e) {}
        }
        createdUserIds.clear();
    }

    @Given("a user named {word} with email {word}")
    public void createUser(String name, String email) throws Exception {
        CreateUserRequest request = new CreateUserRequest(name, email);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateUserRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL + "/api/users", entity, String.class);
        UserEntity user = objectMapper.readValue(response.getBody(), UserEntity.class);
        createdUserIds.add(user.id());
    }

    @When("the client creates a book {string} for {word}")
    public void createBook(String title, String email) throws Exception {
        CreateBookRequest request = new CreateBookRequest(title, email);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateBookRequest> entity = new HttpEntity<>(request, headers);

        latestResponse = restTemplate.postForEntity(BASE_URL + "/api/books", entity, String.class);
        BookResponse book = objectMapper.readValue(latestResponse.getBody(), BookResponse.class);
        createdBookIds.add(book.id());
        lastCreatedBookId = book.id();
    }

    @Then("the client receives status code of {int}")
    public void verifyStatusCode(int statusCode) {
        assertThat("status code is incorrect", latestResponse.getStatusCode().value(), is(statusCode));
    }

    @Then("the client can retrieve {int} book(s) for {word}")
    public void verifyBookCount(int count, String email) throws Exception {
        String url = BASE_URL + "/api/books?borrowerEmail=" + email;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        List<BookResponse> books = objectMapper.readValue(response.getBody(), new TypeReference<List<BookResponse>>() {});
        assertThat("book count is incorrect", books.size(), is(count));
    }

    @When("the client reassigns the book to {word}")
    public void reassignBook(String newBorrowerEmail) {
        AssignBookRequest request = new AssignBookRequest(newBorrowerEmail);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AssignBookRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.exchange(BASE_URL + "/api/books/" + lastCreatedBookId + "/borrower",
                HttpMethod.PATCH, entity, String.class);
    }

    @When("the client marks the book as borrowed")
    public void markBookAsBorrowed() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Boolean> entity = new HttpEntity<>(true, headers);

        restTemplate.patchForObject(BASE_URL + "/api/books/" + lastCreatedBookId + "/borrowed", entity, String.class);
    }

    @Then("the book {string} for {word} is marked as borrowed")
    public void verifyBookIsBorrowed(String title, String email) throws Exception {
        String url = BASE_URL + "/api/books?borrowerEmail=" + email;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        List<BookResponse> books = objectMapper.readValue(response.getBody(), new TypeReference<List<BookResponse>>() {});
        BookResponse book = books.stream()
                .filter(b -> b.title().equals(title))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Book not found: " + title));

        assertThat("book should be marked as borrowed", book.borrowed(), is(true));
    }
}