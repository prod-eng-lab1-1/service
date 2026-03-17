package ro.unibuc.prodeng.e2e.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ro.unibuc.prodeng.model.UserEntity;
import ro.unibuc.prodeng.request.BookActionRequest;
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
    private BookResponse lastBookResponse;

    @After
    public void cleanup() {
        for (String bookId : createdBookIds) {
            try { restTemplate.delete(BASE_URL + "/api/books/" + bookId); } catch (Exception e) {}
        }
        createdBookIds.clear();

        for (String userId : createdUserIds) {
            try { restTemplate.delete(BASE_URL + "/api/users/" + userId); } catch (Exception e) {}
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

    @When("the client creates a book {string} with {int} copies")
    public void createBook(String title, int copies) throws Exception {
        CreateBookRequest request = new CreateBookRequest(title, copies);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateBookRequest> entity = new HttpEntity<>(request, headers);

        latestResponse = restTemplate.postForEntity(BASE_URL + "/api/books", entity, String.class);
        lastBookResponse = objectMapper.readValue(latestResponse.getBody(), BookResponse.class);
        createdBookIds.add(lastBookResponse.id());
        lastCreatedBookId = lastBookResponse.id();
    }

    @When("the client borrows the book for {word}")
    public void borrowBook(String email) throws Exception {
        BookActionRequest request = new BookActionRequest(email);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookActionRequest> entity = new HttpEntity<>(request, headers);

        latestResponse = restTemplate.postForEntity(BASE_URL + "/api/books/" + lastCreatedBookId + "/borrow", entity, String.class);
        lastBookResponse = objectMapper.readValue(latestResponse.getBody(), BookResponse.class);
    }

    @When("the client reserves the book for {word}")
    public void reserveBook(String email) throws Exception {
        BookActionRequest request = new BookActionRequest(email);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookActionRequest> entity = new HttpEntity<>(request, headers);

        latestResponse = restTemplate.postForEntity(BASE_URL + "/api/books/" + lastCreatedBookId + "/reserve", entity, String.class);
        lastBookResponse = objectMapper.readValue(latestResponse.getBody(), BookResponse.class);
    }

    @Then("the book has {int} available copies")
    public void verifyAvailableCopies(int expectedCopies) {
        assertThat("available copies is incorrect", lastBookResponse.availableCopies(), is(expectedCopies));
    }

    @Then("the book has {int} user in queue")
    public void verifyQueueSize(int expectedQueue) {
        assertThat("queue size is incorrect", lastBookResponse.queueSize(), is(expectedQueue));
    }
}