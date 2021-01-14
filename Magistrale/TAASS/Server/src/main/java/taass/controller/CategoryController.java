package taass.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taass.model.Category;
import taass.repository.CategoryRepository;
import taass.repository.UserRepository;
import taass.security.CurrentUser;
import taass.security.UserPrincipal;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;


    /*
     * Ritorna le info della categoria passata
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<Object> info(@PathVariable Long categoryId){
        Optional<Category> cate = categoryRepository.findById(categoryId);

        if (cate.isPresent()) {
            Category category = cate.get();

            return  ResponseEntity.ok(category);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found!");
        }
    }

    /*
    * Mostra tutte le categorie esistenti
    */
    @GetMapping("")
    public Iterable<Category> getAll(){
        Iterable<Category> cats = categoryRepository.findAll();
        return cats;
    }

    /*
    * Aggiunge una categoria
    */
    @PostMapping("")
    public ResponseEntity<Object> add(@CurrentUser UserPrincipal userPrincipal, @RequestBody Category newCate){
        //User user = userRepository.findByUserName(userDetails.getUsername()).get();

        Optional<Category> cate = categoryRepository.findByNameIgnoreCase(newCate.getName());

        if (cate.isPresent()) {
            return  ResponseEntity.ok(cate.get());
        } else {
            if (newCate.getDefaultImage() == null || newCate.getDefaultImage().isEmpty()){
                    System.out.println("IMMAGINE MANCANTE --> METTO DEFAULT");
                   newCate.setDefaultImage("https://static.wixstatic.com/media/04839a_b4d0c16c316d49b68c12dee6f0920ce4.jpg");
            }
            Category addedCate = categoryRepository.save(newCate);
            return  ResponseEntity.ok(addedCate);
        }
    }

    /*
     * Modifica una categoria
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<Object> edit(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long categoryId, @RequestBody Category cateDetails){
        //User user = userRepository.findByUserName(userDetails.getUsername()).get();

        Optional<Category> cate = categoryRepository.findById(categoryId);

        if (cate.isPresent()) {
            Category category = cate.get();

            if (cateDetails.getDefaultImage() == null || cateDetails.getDefaultImage().isEmpty()){
                    cateDetails.setDefaultImage("https://static.wixstatic.com/media/04839a_b4d0c16c316d49b68c12dee6f0920ce4.jpg");
            }

            category.setDescr(cateDetails.getDescr());
            category.setDefaultImage(cateDetails.getDefaultImage());

            Category editCate = categoryRepository.save(category);


            return  ResponseEntity.ok(editCate);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found!");
        }
    }
}
