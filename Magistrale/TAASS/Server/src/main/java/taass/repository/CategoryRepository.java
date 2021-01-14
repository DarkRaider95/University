package taass.repository;

import org.springframework.data.repository.CrudRepository;
import taass.model.Category;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<Category> findByName(String name);
    Optional<Category> findByNameIgnoreCase(String name);

}
