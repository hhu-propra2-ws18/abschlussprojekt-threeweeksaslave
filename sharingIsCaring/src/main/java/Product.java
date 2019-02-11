import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class Product {
	@GeneratedValue
	@Id
	Long id;

	String title;
	String description;

	boolean available;
	Date borrowedUntil;

	int deposit;
	int dailyFee;

	Long ownerId;
}
