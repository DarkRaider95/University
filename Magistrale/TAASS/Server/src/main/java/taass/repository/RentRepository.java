package taass.repository;

import org.springframework.data.repository.CrudRepository;
import taass.model.Product;
import taass.model.Rent;
import taass.model.User;

import java.util.Date;
import java.util.List;

public interface RentRepository extends CrudRepository<Rent, Long> {
    List<Rent> findBySender(User user);
    List<Rent> findByProduct(Product product);
    List<Rent> findByProductAndEndDateAfter(Product product, Date todayDate);

    boolean existsByProduct(Product prod);

    // NOTA: da modificare in futuro quando avranno le idee chiare
    boolean existsByProductAndEndDateAfterAndStartDateBefore(Product product, Date startDate, Date endDate);

    // per controllare se NON ci sono gia prenotazioni che vanno in colflitto con quella richiesta ora
    List<Rent> findByProductAndEndDateAfterAndStartDateBefore(Product product, Date startDate, Date endDate);
                                                                // LE DATE SONO GIUSTE COSI!! NON INVERTIRLE!!!
}
