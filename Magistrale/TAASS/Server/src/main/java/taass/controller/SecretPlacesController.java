package taass.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taass.model.AuthProvider;
import taass.model.Product;
import taass.model.Rent;
import taass.model.User;
import taass.payload.Views;
import taass.repository.ProductRepository;
import taass.repository.RentRepository;
import taass.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@CrossOrigin(origins = "*") // mettere server Secret Places
@RequestMapping("/sp")
public class SecretPlacesController {

    @Autowired
    RentRepository rentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    final String SECRET_PLACES_USER = "SecretPlaceService";

    private User getSecretPlacesUser(){
        if (userRepository.existsByUserName(SECRET_PLACES_USER)){
            return userRepository.findByUserName(SECRET_PLACES_USER).get();
        } else {
            User user = new User();
            user.setUserName(SECRET_PLACES_USER);
            user.setName("Secret Places");
            user.setEmail("info@example.com");
            user.setPassword("FakePWD");
            user.setProvider(AuthProvider.local);

            return userRepository.save(user);
        }
    }

    /*
     * Mostra tutte le richieste inviate da Secret Palces
     */
    @GetMapping(value = "/rent/sent")
    @JsonView(Views.Rent.class)
    public List<Rent> sentRent() {
        User user = getSecretPlacesUser();
        return rentRepository.findBySender(user);
    }

    /*
     * Affitta un prodotto per un periodo di tempo
     */
    @PostMapping(value = "/rent/product/{productId}")
    public ResponseEntity<Object> rent(@PathVariable Long productId, @RequestBody Map<String, Date> period) {
        User user = getSecretPlacesUser();

        Date startDate = period.get("startDate");
        Date endDate = period.get("endDate");

        if (startDate.before(endDate)) {

            Optional<Product> prod = productRepository.findById(productId);
            if (prod.isPresent()) {
                Product product = prod.get();
                if (user.equals(product.getOwner())) {
                    return ResponseEntity.badRequest().body("You own this product!");
                } else {
                    //synchronized (product) { // FIXARE
                    List<Rent> rents = rentRepository.findByProductAndEndDateAfterAndStartDateBefore(product, startDate, endDate);
                    if (rents.isEmpty()) {
                        Rent newRent = new Rent(user, product, startDate, endDate);
                        rentRepository.save(newRent);

                        return ResponseEntity.ok("Request accepted!");
                    } else {
                        return ResponseEntity.badRequest().body("Cannot make request, product already booked in this period!");
                    }
                    //}
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
            }
        } else {
            return ResponseEntity.badRequest().body("Cannot make request, startDate must precedes endDate!");
        }
    }

    /*
     * Rimuove una prenotazione
     */
    @DeleteMapping(value = "/rent/{rentId}")
    public ResponseEntity<Object> remove(@PathVariable Long rentId) {
        User user = getSecretPlacesUser();
        Optional<Rent> _rent =  rentRepository.findById(rentId);

        if (_rent.isPresent()) {
            Rent rent = _rent.get();
            if (user.getUserName().equals(rent.getSender().getUserName())){

                rentRepository.deleteById(rentId);

                return new ResponseEntity<>("Rent deleted", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Error! This rent is sent by another user.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Error! Rent not found!", HttpStatus.NOT_FOUND);
        }
    }

    /*
     * Mostra la prenotazione passata (solo se l'utente e' il sender o l'owner dell'oggetto)
     */
    @GetMapping(value = "/rent/{rentId}")
    public ResponseEntity<Object> one(@PathVariable Long rentId) {
        User user = getSecretPlacesUser();
        Optional<Rent> _rent =  rentRepository.findById(rentId);

        if (_rent.isPresent()) {
            Rent rent = _rent.get();
            if (user.getUserName().equals(rent.getSender().getUserName()) || user.getUserName().equals(rent.getProduct().getOwner().getUserName())){

                return new ResponseEntity<>(rent, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Error! Only sender and owner can see this rent.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Error! Rent not found!", HttpStatus.NOT_FOUND);
        }
    }

    /*
     * Ricerca in tutti i prodotti anche per intervallo date
     */
    @JsonView(Views.Product.class)
    @PostMapping(value = "/search/{str}")
    public ResponseEntity<Object> searchByStringDates(@PathVariable String str, @RequestBody Map<String, Date> period) {

        Date startDate = period.get("startDate");
        Date endDate = period.get("endDate");

        if (startDate.before(endDate)) {
            List<Product> res = new ArrayList<Product>();
            List<Product> prods = search(str);
            for(Product prod : prods){
                if (!rentRepository.existsByProductAndEndDateAfterAndStartDateBefore(prod, startDate, endDate)){
                    res.add(prod);
                }
            }
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return ResponseEntity.badRequest().body("Cannot make request, startDate must precedes endDate!");
        }
    }

    private List<Product> search(String str){
        List<String> strsLower = Arrays.stream(str.trim().toLowerCase().split("\\s+")).collect(Collectors.toList());
        return StreamSupport.stream(productRepository.findAll().spliterator(), true).filter(product -> {
            return strsLower.stream().anyMatch(s -> {
                return  product.getName().toLowerCase().contains(s) ||
                        product.getCategory().getName().toLowerCase().contains(s) ||
                        product.getDescr().toLowerCase().contains(s);
            });
        }).collect(Collectors.toList());
    }

}
