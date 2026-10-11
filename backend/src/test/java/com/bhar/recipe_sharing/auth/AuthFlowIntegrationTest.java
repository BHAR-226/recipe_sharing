package com.bhar.recipe_sharing.auth;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.bhar.recipe_sharing.user.UserRepository;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

    private static final String REGISTER_JSON = """
        {
          "username": "chef",
          "email": "chef@example.com",
          "password": "secret123"
        }
        """;

    private static final String LOGIN_JSON = """
        {
          "email": "chef@example.com",
          "password": "secret123"
        }
        """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void registerLoginMeLogoutFlow() throws Exception {
        MockHttpSession session = new MockHttpSession();
        String sessionIdBeforeLogin = session.getId();

        Csrf csrf = fetchCsrf();

        mockMvc.perform(post("/api/auth/register")
                .session(session)
                .cookie(csrf.cookie())
                .header("X-XSRF-TOKEN", csrf.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REGISTER_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("chef@example.com"));

        mockMvc.perform(post("/api/auth/login")
                .session(session)
                .cookie(csrf.cookie())
                .header("X-XSRF-TOKEN", csrf.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(LOGIN_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("chef@example.com"));

        assertNotEquals(
            sessionIdBeforeLogin,
            session.getId(),
            "Session id should rotate on successful login"
        );

        mockMvc.perform(get("/api/auth/me").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("chef@example.com"));

        mockMvc.perform(post("/api/auth/logout")
                .session(session)
                .cookie(csrf.cookie())
                .header("X-XSRF-TOKEN", csrf.token()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void loginWithoutCsrfTokenIsForbidden() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(LOGIN_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() throws Exception {
        Csrf csrf = fetchCsrf();

        mockMvc.perform(post("/api/auth/register")
                .cookie(csrf.cookie())
                .header("X-XSRF-TOKEN", csrf.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(REGISTER_JSON))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                .cookie(csrf.cookie())
                .header("X-XSRF-TOKEN", csrf.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "chef@example.com",
                      "password": "wrong-password"
                    }
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedMeReturns401Json() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401));
    }

    private Csrf fetchCsrf() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/csrf"))
            .andExpect(status().isOk())
            .andReturn();

        String token = result.getResponse().getContentAsString();
        Cookie cookie = result.getResponse().getCookie("XSRF-TOKEN");

        assertNotNull(cookie, "CSRF cookie should be issued");

        return new Csrf(token, cookie);
    }

    private record Csrf(String token, Cookie cookie) {
    }
}
