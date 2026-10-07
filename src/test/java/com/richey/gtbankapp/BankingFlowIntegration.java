package com.richey.gtbankapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richey.gtbankapp.repo.TransactionRepo;
import com.richey.gtbankapp.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test: boots the full Spring context (controllers, security filter chain,
 * JWT, services, JPA) against an in-memory H2 database. Only the outside world
 * (the mail sender) is mocked.
 *
 * Each test is @Transactional, so the database is rolled back after every test.
 */
@SpringBootTest
@Disabled("Integration tests paused for now")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BankingFlowIntegrationTest {
//
//    // ---- Adjust these to your real controller paths ----
//    private static final String REGISTER_URL = "/api/auth";        // @PostMapping("") on /api/auth, admin only
//    private static final String LOGIN_URL = "/api/auth/login";
//    private static final String DEPOSIT_URL = "/api/transactions/deposit";
//    private static final String MY_TRANSACTIONS_URL = "/api/transactions/me";
//
//    @Autowired private MockMvc mockMvc;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    @Autowired private UserRepo userRepo;
//    @Autowired private TransactionRepo transactionRepo;
//
//    // Stop the app from sending real emails when events fire
//    @MockitoBean private JavaMailSender mailSender;
//
//    private static final String EMAIL = "johndoe@gmail.com";
//    private static final String PASSWORD = "Password123!";
//
//    @BeforeEach
//    void cleanUp() {
//        transactionRepo.deleteAll();
//        userRepo.deleteAll();
//    }
//
//    // ---------------------- helpers ----------------------
//
//    private void register(String email) throws Exception {
//        Map<String, Object> body = Map.of(
//                "firstName", "John",
//                "lastName", "Doe",
//                "email", email,
//                "password", PASSWORD
//        );
//
//        mockMvc.perform(post(REGISTER_URL)
//                        .with(admin())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andExpect(status().is2xxSuccessful());
//    }
//
//    /** Simulates an authenticated admin (the register endpoint requires ROLE_ADMIN). */
//    private static RequestPostProcessor admin() {
//        return user("admin@test.com").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
//    }
//
//    private String loginAndGetToken(String email, String password) throws Exception {
//        Map<String, Object> body = Map.of("email", email, "password", password);
//
//        MvcResult result = mockMvc.perform(post(LOGIN_URL)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        Map<?, ?> json = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
//        // AuthTokenResponse field name: change "token" if yours differs
//        return (String) json.get("token");
//    }
//
//    // ---------------------- registration ----------------------
//
//    @Test
//    @DisplayName("register: persists the user with an encoded password and ROLE_USER")
//    void register_persistsUser() throws Exception {
//        register(EMAIL);
//
//        var saved = userRepo.findByEmail(EMAIL).orElseThrow();
//        assertEquals("John", saved.getFirstName());
//        assertEquals("ROLE_USER", saved.getRole().name());
//        assertNotEquals(PASSWORD, saved.getPassword());     // never stored in plain text
//        assertTrue(saved.getPassword().startsWith("$2"));   // BCrypt hash, remove if you use another encoder
//    }
//
//    @Test
//    @DisplayName("register: returns 409 when the email is already taken")
//    void register_duplicateEmail() throws Exception {
//        register(EMAIL);
//
//        Map<String, Object> body = Map.of(
//                "firstName", "Jane",
//                "lastName", "Roe",
//                "email", EMAIL,
//                "password", PASSWORD
//        );
//
//        mockMvc.perform(post(REGISTER_URL)
//                        .with(admin())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andExpect(status().isConflict());
//    }
//
//    @Test
//    @DisplayName("register: rejects callers who are not admin")
//    void register_requiresAdmin() throws Exception {
//        Map<String, Object> body = Map.of(
//                "firstName", "John",
//                "lastName", "Doe",
//                "email", EMAIL,
//                "password", PASSWORD
//        );
//
//        // Anonymous
//        int anonymous = mockMvc.perform(post(REGISTER_URL)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andReturn().getResponse().getStatus();
//        assertTrue(anonymous == 401 || anonymous == 403, "Expected 401/403 but was " + anonymous);
//
//        // Logged in, but only ROLE_USER
//        mockMvc.perform(post(REGISTER_URL)
//                        .with(user("regular@test.com").authorities(new SimpleGrantedAuthority("ROLE_USER")))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andExpect(status().isForbidden());
//
//        assertTrue(userRepo.findByEmail(EMAIL).isEmpty());
//    }
//
//    // ---------------------- login ----------------------
//
//    @Test
//    @DisplayName("login: returns a JWT for valid credentials")
//
//    void login_success() throws Exception {
//        register(EMAIL);
//
//        String token = loginAndGetToken(EMAIL, PASSWORD);
//
//        assertNotNull(token);
//        assertEquals(3, token.split("\\.").length); // header.payload.signature
//    }
//
//    @Test
//    @DisplayName("login: returns 401 for a wrong password")
//    void login_wrongPassword() throws Exception {
//        register(EMAIL);
//
//        Map<String, Object> body = Map.of("email", EMAIL, "password", "WrongPass!");
//
//        mockMvc.perform(post(LOGIN_URL)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andExpect(status().isUnauthorized());
//    }
//
//    // ---------------------- security ----------------------
//
//    @Test
//    @DisplayName("security: protected endpoint rejects requests without a token")
//    void protectedEndpoint_withoutToken() throws Exception {
//        // 401 or 403 depending on how your security entry point is configured
//        int statusCode = mockMvc.perform(get(MY_TRANSACTIONS_URL))
//                .andReturn().getResponse().getStatus();
//
//        assertTrue(statusCode == 401 || statusCode == 403,
//                "Expected 401 or 403 but was " + statusCode);
//    }
//
//    @Test
//    @DisplayName("security: protected endpoint rejects a garbage token")
//    void protectedEndpoint_invalidToken() throws Exception {
//        int statusCode = mockMvc.perform(get(MY_TRANSACTIONS_URL)
//                        .header("Authorization", "Bearer not.a.realtoken"))
//                .andReturn().getResponse().getStatus();
//
//        assertTrue(statusCode == 401 || statusCode == 403,
//                "Expected 401 or 403 but was " + statusCode);
//    }
//
//    // ---------------------- end-to-end flow ----------------------
//
//    @Test
//    @DisplayName("flow: register -> login -> deposit -> transaction appears in history")
//    @Disabled
//    void fullFlow_depositShowsInHistory() throws Exception {
//        register(EMAIL);
//        String token = loginAndGetToken(EMAIL, PASSWORD);
//
//        Map<String, Object> deposit = Map.of(
//                "amount", 500.00,
//                "description", "Salary"
//        );
//
//        mockMvc.perform(post(DEPOSIT_URL)
//                        .header("Authorization", "Bearer " + token)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(deposit)))
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(jsonPath("$.description").value("Salary"))
//                .andExpect(jsonPath("$.type").value("DEPOSIT"))
//                .andExpect(jsonPath("$.status").value("COMPLETED"));
//
//        mockMvc.perform(get(MY_TRANSACTIONS_URL)
//                        .header("Authorization", "Bearer " + token))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(1))
//                .andExpect(jsonPath("$[0].description").value("Salary"));
//
//        // And it really hit the database
//        assertEquals(1, transactionRepo.count());
//    }
//
//    @Test
//    @DisplayName("flow: a user only sees their own transactions")
//    @Disabled
//    void transactions_areIsolatedPerUser() throws Exception {
//        register(EMAIL);
//        register("jane@gmail.com");
//
//        String johnToken = loginAndGetToken(EMAIL, PASSWORD);
//        String janeToken = loginAndGetToken("jane@gmail.com", PASSWORD);
//
//        mockMvc.perform(post(DEPOSIT_URL)
//                        .header("Authorization", "Bearer " + johnToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(
//                                Map.of("amount", 100.00, "description", "John only"))))
//                .andExpect(status().is2xxSuccessful());
//
//        mockMvc.perform(get(MY_TRANSACTIONS_URL)
//                        .header("Authorization", "Bearer " + janeToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(0));
//    }
}
