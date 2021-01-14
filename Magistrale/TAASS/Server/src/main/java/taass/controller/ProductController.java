
package taass.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taass.model.Category;
import taass.model.Product;
import taass.model.User;
import taass.payload.Views;
import taass.repository.CategoryRepository;
import taass.repository.ProductRepository;
import taass.repository.RentRepository;
import taass.repository.UserRepository;
import taass.security.CurrentUser;
import taass.security.UserPrincipal;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/products")

public class ProductController {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RentRepository rentRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("")
    public Iterable<Product> all(){
        return productRepository.findAll();
    }

    /*
     * Mostra le info del prodotto passato
     */
    @GetMapping(value = "/{productId}")
    public ResponseEntity<Object> one(@PathVariable Long productId) {
        Optional<Product> prod =  productRepository.findById(productId);

        if (prod.isPresent()) {
            Product product = prod.get();
            return  ResponseEntity.ok(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
        }
    }

    /*
     * Ricerca in tutti i prodotti
     */
    @JsonView(Views.Product.class)
    @GetMapping(value = "/search/{str}")
    public List<Product> searchByString(@PathVariable String str) {
        List<String> strsLower = Arrays.stream(str.trim().toLowerCase().split("\\s+")).collect(Collectors.toList());
        return StreamSupport.stream(productRepository.findAll().spliterator(), true).filter(product -> {
            return strsLower.stream().anyMatch(s -> {
                return  product.getName().toLowerCase().contains(s) ||
                        product.getCategory().getName().toLowerCase().contains(s) ||
                        product.getDescr().toLowerCase().contains(s);
            });
        }).collect(Collectors.toList());
    }

    /*
     * Ricerca in tutti i prodotti anche per categoria
     */
    @JsonView(Views.Product.class)
    @PostMapping(value = "/search/{str}")
    public List<Product> searchByStringAndCategory(@PathVariable String str, @RequestBody List<Category> cats) {
        List<String> strsLower = Arrays.stream(str.trim().toLowerCase().split("\\s+")).collect(Collectors.toList());

        Iterable<Product> products;
        if (cats != null && cats.size()>0){
             products = productRepository.findByCategoryIn(cats);
        } else {
            products = productRepository.findAll();
        }

        return StreamSupport.stream(products.spliterator(), true).filter(product -> {
            return strsLower.stream().anyMatch(s -> {
                return  product.getName().toLowerCase().contains(s) ||
                        product.getDescr().toLowerCase().contains(s);
            });
        }).collect(Collectors.toList());
    }


    /*
    * Aggiunge un prodotto
    */

    @PostMapping("")
    public ResponseEntity<Object> add(@CurrentUser UserPrincipal userPrincipal, @RequestBody Product prodDetails){
        User user = userRepository.findById(userPrincipal.getId()).get();
        Optional<Category> cate = categoryRepository.findById(prodDetails.getCategory().getId());

        if (cate.isPresent()) {
            Category category = cate.get();
            if (prodDetails.getImage() == null || prodDetails.getImage().isEmpty()){
                prodDetails.setImage(category.getDefaultImage());
            }
            Product newProduct = new Product(prodDetails.getName(), prodDetails.getDescr(), prodDetails.getImage(), prodDetails.getPrice(), category, user);
            productRepository.save(newProduct);
            return  ResponseEntity.ok("Product added!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found!");
        }
    }

    /*
     * Modifica un prodotto (solo se e' del proprietario)
     */
    @PutMapping("/{productId}")
    public ResponseEntity<Object> edit(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long productId, @RequestBody Product prodDetails){
        User user = userRepository.findById(userPrincipal.getId()).get();
        Optional<Product> _prod =  productRepository.findById(productId);

        // se il prodotto esiste
        if (_prod.isPresent()) {
            Product product = _prod.get();

            // se e' di questo user
            if (product.getOwner().getUserName().equals(user.getUserName())){
                Optional<Category> _cate = categoryRepository.findById(prodDetails.getCategory().getId());

                // se la categoria esiste
                if (_cate.isPresent()) {
                    Category category = _cate.get();

                    product.setCategory(category);
                    product.setPrice(prodDetails.getPrice()); // setta il nuovo prezzo
                    product.setDescr(prodDetails.getDescr());
                    product.setName(prodDetails.getName());
                    product.setImage(prodDetails.getImage());

                    productRepository.save(product);
                    return new ResponseEntity<>("Product added", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Error! Category not found!", HttpStatus.NOT_FOUND);
                }

            } else {
                return new ResponseEntity<>("Error! This product is owned by another user.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Error! Product not found!", HttpStatus.NOT_FOUND);
        }
    }

    /*
     * Rimuove un prodotto (solo se e' del proprietario)
     */
    @DeleteMapping(value = "/{productId}")
    public ResponseEntity<Object> remove(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long productId) {
        User user = userRepository.findById(userPrincipal.getId()).get();
        Optional<Product> _prod =  productRepository.findById(productId);

        if (_prod.isPresent()) {
            Product product = _prod.get();
            if (product.getOwner().getUserName().equals(user.getUserName())){
                if (!rentRepository.existsByProduct(product)) {

                    productRepository.deleteById(productId);
                    return new ResponseEntity<>("Product deleted", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Error! This product is rented!", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Error! This product is owned by another user.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Error! Product not found!", HttpStatus.NOT_FOUND);
        }
    }

    /*
     * Mostra tutti i prodotti di un utente
     */
    @GetMapping(value = "/user/{username}")
    public ResponseEntity<Object> userProducts(@PathVariable String username) {
        Optional<User> _user = userRepository.findByUserName(username);

        if (_user.isPresent()){
            List<Product> products =  productRepository.findByOwner(_user.get());
            return  ResponseEntity.ok(products);

        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }

    }

    /*
     * Mostra tutti i prodotti dell'utente loggato
     */
    @GetMapping(value = "/my")
    public ResponseEntity<Object> myProducts(@CurrentUser UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).get();
        return userProducts(user.getUserName());
    }

    /*
     * Mostra tutti i prodotti che appartengono alla categoria passata
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Object> list(@PathVariable Long categoryId){
        Optional<Category> _cate = categoryRepository.findById(categoryId);

        if (_cate.isPresent()) {
            List<Product> products = productRepository.findByCategory(_cate.get());
            return  ResponseEntity.ok(products);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found!");
        }
    }

}

