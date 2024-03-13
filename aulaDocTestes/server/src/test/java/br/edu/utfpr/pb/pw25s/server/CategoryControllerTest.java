package br.edu.utfpr.pb.pw25s.server;

import br.edu.utfpr.pb.pw25s.server.model.Category;
import br.edu.utfpr.pb.pw25s.server.model.User;
import br.edu.utfpr.pb.pw25s.server.repository.CategoryRepository;
import br.edu.utfpr.pb.pw25s.server.repository.UserRepository;
import br.edu.utfpr.pb.pw25s.server.security.AuthenticationResponse;
import br.edu.utfpr.pb.pw25s.server.security.SecurityConstants;
import br.edu.utfpr.pb.pw25s.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Objects;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CategoryControllerTest {

    private static final String URL_CATEGORY = "/categories";
    private static final String URL_LOGIN = "/login";


    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @BeforeEach
    public void cleanup() {
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void postCategory_whenCategoryIsValid_receiveCreated() {
        Category category = createValidCategory();
        ResponseEntity<Object> response = postEntity(category, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    private Category createValidCategory() {
        return Category.builder().name("Category One").build();
    }

    private User createValidUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setDisplayName("test-display");
        user.setPassword("P4ssword");
        return user;
    }

    public <T> ResponseEntity<T> postEntity(Object category, Class<T> responseType) {
        userService.save(createValidUser());
        ResponseEntity<AuthenticationResponse> responseToken = login(createValidUser(), AuthenticationResponse.class);
        System.out.println(Objects.requireNonNull(responseToken.getBody()).getToken());
        HttpEntity<Object> entity = new HttpEntity<>(category , createHttpHeaders(Objects.requireNonNull(responseToken.getBody()).getToken()));

        return testRestTemplate.postForEntity(URL_CATEGORY, entity, responseType);
    }

    public HttpHeaders createHttpHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(SecurityConstants.HEADER_STRING,SecurityConstants.TOKEN_PREFIX + accessToken);
        return headers;
    }

    public <T> ResponseEntity<T> login(Object request, Class<T> responseType) {
        return testRestTemplate.postForEntity(URL_LOGIN, request, responseType);
    }


}
