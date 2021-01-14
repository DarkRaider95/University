
package taass.repository;

import org.springframework.data.repository.CrudRepository;
import taass.model.Category;
import taass.model.Product;
import taass.model.User;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findByName(String name);
    Optional<Product> findById(Long id);
    List<Product> findByCategory(Category category);
    List<Product> findByOwner(User Owner);

    List<Product> findByCategoryIn(List<Category> cats);

}

