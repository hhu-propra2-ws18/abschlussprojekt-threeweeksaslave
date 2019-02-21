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
    public void testGetTotalAmount(){
        java.sql.Date from = new java.sql.Date(System.currentTimeMillis());
        double response = product.getTotalAmount(from);
        Assertions.assertThat(15.0).isEqualTo(response);
    }

}
