package propra2.repositories;

import org.springframework.data.repository.CrudRepository;
import propra2.database.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends CrudRepository<Notification, Long>{
    List<Notification> findAllByBorrowerId(Long id);
    Optional<Notification> findByProcessId(Long processId);
}
