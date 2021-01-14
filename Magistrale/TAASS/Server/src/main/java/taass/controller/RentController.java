package taass.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taass.model.Product;
import taass.model.Rent;
import taass.model.User;
import taass.payload.RentDates;
import taass.payload.Views;
import taass.repository.ProductRepository;
import taass.repository.RentRepository;
import taass.repository.UserRepository;
import taass.security.CurrentUser;
import taass.security.UserPrincipal;
import taass.util.NotificationService;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/rent")
public class RentController {

    @Autowired
    RentRepository rentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    NotificationService notif;


    /*
     * Mostra tutte le richieste inviate
     */
    @GetMapping(value = "/sent")
    @JsonView(Views.Rent.class)
    public List<Rent> sentRent(@CurrentUser UserPrincipal userPrincipal) {
        //User user = userRepository.findByUserName(userDetails.getUsername()).get();
        User user = userRepository.findById(userPrincipal.getId()).get();
        return rentRepository.findBySender(user);
    }


    /*
     * Mostra tutte le richieste ricevute per i propri prodotti
     */
    @JsonView(Views.Rent.class)
    @GetMapping(value = "/received")
    public List<Rent> receivedRent(@CurrentUser UserPrincipal userPrincipal) {
        //User user = userRepository.findByUserName(userDetails.getUsername()).get();
        User user = userRepository.findById(userPrincipal.getId()).get();

        // List<Rent> rents = rentRepository.findByOwner(user);
        List<Product> products = productRepository.findByOwner(user);
        List<Rent> rents =  new ArrayList<>();
        for (Product prod : products) {
            rents.addAll(rentRepository.findByProduct(prod));
        }
        //products.parallelStream().forEach((product) ->  rents.addAll(rentRepository.findByProduct(product)));

        /*for (Rent rent : rents) {
            System.out.println(rent);
        }*/

        return rents;
    }


    /*
     * Mostra le date di prenotazione di un prodotto
     */
    @GetMapping(value = "/product/{productId}")
    public ResponseEntity<Object> showRentProd(@PathVariable Long productId) {
        Optional<Product> prod = productRepository.findById(productId);
        if (prod.isPresent()) {
            Product product = prod.get();

            Date today = Calendar.getInstance().getTime();

            List<Rent> rents = rentRepository.findByProductAndEndDateAfter(product, today);
            //if (!rents.isEmpty()) {
            List<RentDates> rd = new ArrayList<>();
            for(Rent rent: rents){
                rd.add(new RentDates(rent));
            }

            return new ResponseEntity<>(rd, HttpStatus.OK);


        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
        }
    }

    /*
    * Affitta un prodotto per un periodo di tempo
    */
    @PostMapping(value = "/product/{productId}")
    public ResponseEntity<Object> rent(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long productId, @RequestBody Map<String, Date> period) {
        //User user = userRepository.findByUserName(userDetails.getUsername()).get();
        User user = userRepository.findById(userPrincipal.getId()).get();

        Date startDate = period.get("startDate");
        Date endDate = period.get("endDate");
        Date today = Calendar.getInstance().getTime();

        if (startDate.before(endDate) && startDate.after(today)) {

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
                            Rent addedRent = rentRepository.save(newRent);

                            /// NOTIFICA
                            String title = product.getName()
                                            .concat(" noleggiato.");

                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                            String message = "Il prodotto "
                                                .concat(product.getName())
                                                .concat(" e' stato noleggiato dal ")
                                                .concat(formatter.format(startDate))
                                                .concat(" al ")
                                                .concat(formatter.format(endDate));

                            notif.sendNews(product.getOwner(), addedRent.getId(), title, message);
                            /// FINE NOTIFICA

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
            return ResponseEntity.badRequest().body("Cannot make request, startDate must precedes endDate and follow today!");
        }
    }

    /*
     * Rimuove una prenotazione
     */
    @DeleteMapping(value = "/{rentId}")
    public ResponseEntity<Object> remove(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long rentId) {
        //User user = userRepository.findByUserName(userDetails.getUsername()).get();
        User user = userRepository.findById(userPrincipal.getId()).get();
        Optional<Rent> _rent =  rentRepository.findById(rentId);

        if (_rent.isPresent()) {
            Rent rent = _rent.get();
            if (user.getUserName().equals(rent.getSender().getUserName())){

                rentRepository.deleteById(rentId);

                notif.delNews(rentId);

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
    @GetMapping(value = "/{rentId}")
    public ResponseEntity<Object> one(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long rentId) {
        //User user = userRepository.findByUserName(userDetails.getUsername()).get();
        User user = userRepository.findById(userPrincipal.getId()).get();
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


}
