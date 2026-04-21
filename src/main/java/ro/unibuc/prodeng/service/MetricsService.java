package ro.unibuc.prodeng.service;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsService {
    
    // 1. Business: Contorizăm câți useri noi au fost creați
    private final Counter userCreatedCounter;
    
    // 2. Domain-specific: Contorizăm acțiunile pe cărți (împrumut, rezervare etc.)
    private final Counter bookActionCounter;
    
    // 3. Error: Contorizăm erorile apărute în aplicație
    private final Counter errorCounter;
    
    // 4. Resource: Câți useri activi avem momentan (crește la creare, scade la ștergere)
    private final AtomicInteger activeUsers;
    
    // 5. Performance: Cât durează să căutăm un user
    private final Timer userLookupTimer;

    public MetricsService(MeterRegistry registry) {
        this.userCreatedCounter = Counter.builder("app_users_created_total")
                .description("Numărul total de useri creati")
                .tag("type", "business").register(registry);

        this.bookActionCounter = Counter.builder("app_book_actions_total")
                .description("Numărul total de operațiuni pe cărți")
                .tag("type", "domain").register(registry);

        this.errorCounter = Counter.builder("app_errors_total")
                .description("Numărul total de erori ale aplicației")
                .tag("type", "error").register(registry);

        this.activeUsers = new AtomicInteger(0);
        Gauge.builder("app_active_users", activeUsers, AtomicInteger::get)
                .description("Numărul curent de useri în sistem")
                .tag("type", "resource").register(registry);

        this.userLookupTimer = Timer.builder("app_user_lookup_duration_seconds")
                .description("Timpul de răspuns pentru aducerea unui user")
                .tag("type", "performance").register(registry);
    }

    // Metode pe care le vom apela din controllere
    public void recordUserCreated() {
        userCreatedCounter.increment();
        activeUsers.incrementAndGet();
    }

    public void recordUserDeleted() { activeUsers.decrementAndGet(); }
    public void recordBookAction() { bookActionCounter.increment(); }
    public void recordError() { errorCounter.increment(); }
    public Timer getUserLookupTimer() { return userLookupTimer; }
}