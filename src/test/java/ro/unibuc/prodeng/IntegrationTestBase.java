package ro.unibuc.prodeng;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Tag("IntegrationTest")
public abstract class IntegrationTestBase {

   private static final MongoDBContainer mongoDBContainer =
           new MongoDBContainer("mongo:latest")
                   .withExposedPorts(27017);

   static {
      mongoDBContainer.start();
   }

   @DynamicPropertySource
   static void setProperties(DynamicPropertyRegistry registry) {
      registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
   }
}