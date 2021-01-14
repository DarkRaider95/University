package taass.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import taass.model.AuthProvider;
import taass.model.Category;
import taass.model.Product;
import taass.model.User;
import taass.repository.CategoryRepository;
import taass.repository.ProductRepository;
import taass.repository.UserRepository;
import taass.util.NotificationService;

@Configuration
public class InitiDbObjects {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    NotificationService notif;

    @Bean
    CommandLineRunner initDB(){

        try{
            populate();
            notif.delAllNews();
        } catch (Exception e){
            System.out.println("DB gia popolato");
        }

        return args -> {};
    }

    private void populate(){
        User gianni = createUser("gianni", "Gianni Primo", "gianni@example.com", "moltosicura");
        User paolo = createUser("paolo", "Paolo Ciampi", "iu1ngs@example.com", "nessunolasa");
        User max = createUser("max", "Max Baratono", "iw1fru@example.com", "cosicosi");

        Category auto = createCategory("Auto", "Automobili in buone condizioni", "https://www.gelestatic.it/thimg/R2gZiK0bmh_rkZgw5uPrCpRD6No=/fit-in/960x540/https%3A//www.lastampa.it/image/contentid/policy%3A1.37733896%3A1570836424/mirai-front-395534.jpg%3Ff%3Ddetail_558%26h%3D720%26w%3D1280%26%24p%24f%24h%24w%3Dbeda61c");
        Category moto = createCategory("Moto", "Motocicliette per tutti i centauri", "https://motomorini.eu/wp-content/themes/yootheme/cache/Moto-Morini-X-Cape-laterale-sx-9e9d5b67.jpeg");
        Category bici = createCategory("Bici", "Mountain bike e city bike", "https://contents.mediadecathlon.com/p1638899/kdb31565070d7796ce88b8e895fee2af3/1638899_default.jpg?format=auto&quality=60&f=800x0");
        Category tecnologia = createCategory("Tecnologia", "Telefoni e PC", "https://blog.pcloud.com/wp-content/uploads/2018/06/1.jpg");

        createProduct("BMW Z4", "BMW nera con motore appena rifatto", "", 100.00f, auto, max);
        createProduct("KTM fr900", "Mountain bike arancione", "", 30.00f, bici, max);
        createProduct("TMax", "Scooterone portami via", "", 60.00f, moto, max);
        createProduct("Panda Cross", "Pandino fuori strada da massacrare", "", 50.00f, auto, max);
        createProduct("iPhone 6s", "Smartphone per le emergenze", "", 25.00f, tecnologia, max);


        createProduct("iCom 7300", "Radio SDR per radioamatori", "", 30.00f, tecnologia, gianni);
        createProduct("Scott Electric", "Bici elettrica", "", 40.00f, bici, gianni);
        createProduct("Gilera 500", "Moto d'epoca per i raduni", "", 60.00f, moto, gianni);

        createProduct("Porsche Targa 4s", "Fuoriserie per matrimoni", "", 200.00f, auto, paolo);
        createProduct("Aprlilia RS", "Moto per ragazzi, 50ino", "", 40.00f, moto, paolo);

        System.out.println("Popolato");
    }

    private User createUser(String username, String name, String email, String password){
        User user = new User();
        user.setUserName(username);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setProvider(AuthProvider.local);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    private Category createCategory(String name, String descr, String defaultImage){
        Category category = new Category(name, descr, defaultImage);
        return categoryRepository.save(category);
    }

    private Product createProduct(String name, String descr, String image, float price, Category category, User owner){
        Product prod = new Product(name, descr, image, price, category, owner);
        if (image.length() == 0){
            prod.setImage(prod.getCategory().getDefaultImage());
        }
        return productRepository.save(prod);
    }
}
