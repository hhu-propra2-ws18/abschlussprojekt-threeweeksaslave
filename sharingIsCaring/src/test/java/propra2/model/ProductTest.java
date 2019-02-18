package propra2.model;

import org.junit.Assert;
import org.junit.Test;
import propra2.database.Customer;
import propra2.database.Product;

import java.util.Date;

public class ProductTest {

    @Test
    public void allValuesSetFalse(){
    	Customer customer = new Customer();
        Product product = new Product();
        product.setId(10L);
        product.setOwner(customer);
        product.setAvailable(true);

        Assert.assertEquals(false, product.allValuesSet());
    }

    @Test
    public void allValuesSetTrue(){
    	Customer customer = new Customer();
        Product product = new Product();
        product.setId(10L);
        product.setOwner(customer);
        product.setAvailable(false);
        product.setDeposit(4);
        product.setDailyFee(123);
        product.setBorrowedUntil(new Date());
        product.setTitle("Putzfrau");
        product.setDescription("Putzt alles blitzblank");

        Assert.assertEquals(true, product.allValuesSet());
    }
}
