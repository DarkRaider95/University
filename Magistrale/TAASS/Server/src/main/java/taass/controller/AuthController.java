package taass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taass.exception.BadRequestException;
import taass.model.AuthProvider;
import taass.model.User;
import taass.payload.AuthResponse;
import taass.payload.LoginRequest;
import taass.payload.SignUpRequest;
import taass.repository.UserRepository;
import taass.security.TokenProvider;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println(loginRequest);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserName(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByUserName(signUpRequest.getUserName())) {
            throw new BadRequestException("Username address already in use.");
        }

        // Creating user's account
        User user = new User();
        user.setUserName(signUpRequest.getUserName());
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setProvider(AuthProvider.local);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //System.out.println(passwordEncoder.encode(user.getPassword()));
        //System.out.println(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);

        /*URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/user/profile")
                .buildAndExpand(result.getId()).toUri();*/

        //return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully@"));
        LoginRequest login = new LoginRequest();
        login.setUserName(signUpRequest.getUserName());
        login.setPassword(signUpRequest.getPassword());
        return authenticateUser(login);
    }

}
