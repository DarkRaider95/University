package taass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taass.model.User;
import taass.util.NotificationService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/test")
public class TestController {

    @Autowired
    NotificationService notif;

    @GetMapping("/news")
    public ResponseEntity<Object> getAll(){
        User asd = new User();
        asd.setUserName("lorenzo");
        notif.sendNews(asd, 123L, "Titolo", "messaggio");
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
