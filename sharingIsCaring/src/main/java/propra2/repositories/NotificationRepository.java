package propra2.repositories;

import org.springframework.data.repository.CrudRepository;
import propra2.database.Notification;


        import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long>{
    List<Notification> findAllByBorrowerId(Long id);
}
