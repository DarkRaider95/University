package taass.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import taass.exception.ResourceNotFoundException;
import taass.model.AuthProvider;
import taass.model.User;
import taass.payload.UpdateUser;
import taass.payload.Views;
import taass.repository.RentRepository;
import taass.repository.UserRepository;
import taass.security.CurrentUser;
import taass.security.UserPrincipal;

import java.util.Map;
import java.util.Optional;

//import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RentRepository rentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /*@GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }*/

    //////////////


    /*
     * Info pubbliche utente qualsiasi
     */
    @GetMapping("/{username}")
    @JsonView(Views.User.class)
    public ResponseEntity<Object> publicInfo(@PathVariable String username) {
        Optional<User> _user = userRepository.findByUserName(username);
        if (_user.isPresent()){
            //PublicUser puser = new PublicUser(_user.get());
            return new ResponseEntity<>(_user.get(), HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
    }

    /*
     * Info private utente loggato
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    @JsonView(Views.PrivateUser.class)
    public User profile(@CurrentUser UserPrincipal userPrincipal){
        //User user = userRepository.findByUserName(userDetails.getUsername()).get();
        //return user;
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

    /*
     * Cancella l'utente loggato
     */
    @DeleteMapping("/profile")
    public ResponseEntity delete(@CurrentUser UserPrincipal userPrincipal, @RequestBody Map<String, String> passInfo) {
        //User user = userRepository.findByUserName(userDetails.getUsername()).get();
        User user = userRepository.findById(userPrincipal.getId()).get();

        String password = passInfo.get("password");

        if (passwordEncoder.matches(password, user.getPassword())) {
            userRepository.delete(user);
            return ResponseEntity.ok("User deleted");
        } else {
            return new ResponseEntity<>("Error! Wrong password.", HttpStatus.BAD_REQUEST);
        }
    }

    /*
     * Modifica l'utente loggato
     */
    @PutMapping("/profile")
    public ResponseEntity edit(@CurrentUser UserPrincipal userPrincipal, @RequestBody UpdateUser newInfoUser) {
        //User user = userRepository.findByUserName(userDetails.getUsername()).get();
        User user = userRepository.findById(userPrincipal.getId()).get();

        if (user.getProvider() != AuthProvider.local || passwordEncoder.matches(newInfoUser.getConfirmPassword(), user.getPassword())) {
            //user.setFullName(newInfoUser.getFullName());
            //user.setUserName(newInfoUser.getUserName());
            user.setName(newInfoUser.getName());
            user.setEmail(newInfoUser.getEmail());
            //user.setCards(newInfoUser.getCards());
            user.setDescript(newInfoUser.getDescript());
            if (newInfoUser.getImageUrl() != null && newInfoUser.getImageUrl().length() < 256){
                user.setImageUrl(newInfoUser.getImageUrl());
            }
            if (newInfoUser.getPassword() != null && !newInfoUser.getPassword().isEmpty()) { // cambia la password solo se NON e' nulla nel json passato
                user.setPassword(passwordEncoder.encode(newInfoUser.getPassword()));
            }
            userRepository.save(user);
            return ResponseEntity.ok("User updated!");
        } else {
            return new ResponseEntity<>("Error! Wrong password.", HttpStatus.BAD_REQUEST);
        }
    }
}
