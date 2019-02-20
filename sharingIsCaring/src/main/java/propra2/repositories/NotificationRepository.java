package propra2.repositories;



import org.springframework.data.repository.CrudRepository;
import propra2.database.Notification;


public interface NotificationRepository extends CrudRepository<Notification, Long>{

}
