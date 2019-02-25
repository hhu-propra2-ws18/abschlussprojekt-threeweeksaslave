package propra2.database;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;

public class ProductTest {

    private Product product;

    @Before
    public void setup(){
        product = new Product();
        product.setDailyFee(5);
        product.setDeposit(10);
    }

    @Test
    public void testGetTotalDailyFee(){
        java.sql.Date from = new java.sql.Date(System.currentTimeMillis());
        double diff = 5.0;
        double response = product.getTotalDailyFee(from);
        Assertions.assertThat(diff).isEqualTo(response);
    }


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
        product.setBorrowedUntil(new java.util.Date());
        product.setTitle("Putzfrau");
        product.setDescription("Putzt alles blitzblank");

        Assert.assertEquals(true, product.allValuesSet());
    }

}
