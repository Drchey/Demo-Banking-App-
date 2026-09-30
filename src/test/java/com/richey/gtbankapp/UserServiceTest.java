package com.richey.gtbankapp;

import com.richey.gtbankapp.dto.AuthTokenResponse;
import com.richey.gtbankapp.dto.LoginRequest;
import com.richey.gtbankapp.dto.RegistrationRequest;
import com.richey.gtbankapp.dto.UserResponse;
import com.richey.gtbankapp.dto.mail.UserRegisteredEvent;
import com.richey.gtbankapp.model.User;
import com.richey.gtbankapp.model.UserRole;
import com.richey.gtbankapp.repo.UserRepo;
import com.richey.gtbankapp.security.JwtUtil;
import com.richey.gtbankapp.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ApplicationEventPublisher publisher;


    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private User manager;
    private User admin;

    @BeforeEach
    void setUp(){
        user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@gmail.com")
                .password("password")
                .role(UserRole.ROLE_USER)
                .build();

        manager = User.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .email("alicejohnson@gmail.com")
                .password("password")
                .role(UserRole.ROLE_MANAGER)
                .build();

        admin = User.builder()
                .firstName("Joey")
                .lastName("Dias")
                .email("joeydias@gmail.com")
                .password("password")
                .role(UserRole.ROLE_ADMIN)
                .build();

    }



    // Create User
    @Test
    @DisplayName("Create User : saves user with encoded password")
    void createUserSuccess(){
        RegistrationRequest request =
                new RegistrationRequest("April", "Oneil",
                        "apriloneil@gmail.com", "passwordPlain");

        when(userRepo.existsByEmail("apriloneil@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("passwordPlain")).thenReturn("encoded-pass");
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));


        //Act
        UserResponse userResponse = userService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(captor.capture());
        User saved = captor.getValue();


        assertEquals("encoded-pass", saved.getPassword());
        assertNotEquals("passwordPlain", saved.getPassword());
        assertEquals(UserRole.ROLE_USER, saved.getRole());
        assertEquals("April", saved.getFirstName());
        assertEquals("Oneil", saved.getLastName());
        assertEquals("apriloneil@gmail.com", saved.getEmail());


    }


    @Test
    @DisplayName("User Conflict: email already exists")
    void createUser_emailExists(){
        RegistrationRequest request =  new RegistrationRequest("John", "Doe", "john@example.com", "plainPass");
        when(userRepo.existsByEmail("john@example.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, ()-> userService.createUser(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(userRepo, never()).save(any());
        verifyNoInteractions(passwordEncoder);

    }


    @Test
    @DisplayName("Login User: returns token and publishes event")
    void loginUserSuccess(){
        LoginRequest request = new LoginRequest("johndoe@gmail.com","password");
        when(userRepo.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        AuthTokenResponse response = userService.LoginUser(request);

        assertEquals("jwt-token", response.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Verify that Email is Sent
        ArgumentCaptor<UserRegisteredEvent> captor = ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(publisher).publishEvent(captor.capture());
        assertNotNull(captor.getValue());

    }


    @Test
    @DisplayName("Login User: 401 error for non user")
    void LoginUser_badCreds(){
        LoginRequest request = new LoginRequest("johndoefalse@gmail.com","password");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad Cred"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.LoginUser(request));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        verifyNoInteractions(userRepo, jwtUtil, publisher);

    }


    @Test
    @DisplayName("findById: returns user when found")
    void findById_admin() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(admin));

        UserResponse response = userService.findById(1L);

        assertEquals("joeydias@gmail.com", response.email());
        assertEquals("ROLE_ADMIN", response.role());
    }

    @Test
    @DisplayName("findById: throws 404 for non-admin user")
    void findById_nonAdmin() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.findById(1L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("findById: throws 404 ")
    void findById_Notfound(){
        when(userRepo.findById(5L)).thenReturn(Optional.of(user));
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class,
                        ()-> userService.findById(5L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("Get All Users")
    void getAllUsers(){
        when(userRepo.findAll()).thenReturn(List.of(user, admin, manager));
        List<UserResponse> res = userService.findAllUsers();
        assertEquals(3, res.size());
        assertEquals("johndoe@gmail.com", res.get(0).email());
    }


    @Test
    @DisplayName("Get Empty List")
    void getAllUsers_Empty(){
        when(userRepo.findAll()).thenReturn(List.of());
        assertTrue(userService.findAllUsers().isEmpty());
    }


}
