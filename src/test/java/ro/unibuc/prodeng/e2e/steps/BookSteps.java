package ro.unibuc.prodeng.e2e.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import ro.unibuc.prodeng.request.BookActionRequest;
import ro.unibuc.prodeng.request.CreateBookRequest;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.response.BookResponse;
import ro.unibuc.prodeng.response.UserResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BookSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private String currentBookId;

    @Given("the database is empty")
    public void theDatabaseIsEmpty() {
        // Asumat ca am curatat DB-ul
    }

    @Given("there is a user registered with email {string} and name {string}")
    public void createUser(String email, String name) {
        CreateUserRequest request = new CreateUserRequest(name, email);
        restTemplate.postForEntity("/api/users", request, UserResponse.class);
    }

    @Given("there is a book with title {string} and {int} copies in the system")
    public void createBook(String title, int copies) {
        CreateBookRequest request = new CreateBookRequest(title, copies);
        ResponseEntity<BookResponse> response = restTemplate.postForEntity("/api/books", request, BookResponse.class);
        currentBookId = response.getBody().id();
    }

    @When("the user with email {string} borrows the book")
    public void borrowBook(String email) {
        BookActionRequest request = new BookActionRequest(email);
        restTemplate.postForEntity("/api/books/" + currentBookId + "/borrow", request, BookResponse.class);
    }

    @Then("the book should have {int} available copies")
    public void verifyAvailableCopies(int copies) {
        // AICI ERA PROBLEMA: Trebuie sa fie ResponseEntity<BookResponse[]> in loc de <BookResponse>
        ResponseEntity<BookResponse[]> response = restTemplate.getForEntity("/api/books", BookResponse[].class);
        
        BookResponse[] books = response.getBody();
        boolean found = false;
        
        if (books != null) {
            for (BookResponse b : books) {
                if (b.id().equals(currentBookId) && b.availableCopies() == copies) {
                    found = true;
                }
            }
        }
        assertThat("Book should have expected available copies", found, is(true));
    }
}