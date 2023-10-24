package model;

import exceptions.NotInStock;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CommodityTest {
    private Commodity commodity;

    @BeforeEach
    public void setUp() {
        commodity = new Commodity();
    }

    @Test
    public void testConstructor() {
        Assertions.assertEquals(0, commodity.getInStock());
        Assertions.assertEquals(0, commodity.getInitRate());
    }

    @Test
    public void testUpdateInStockWithNegativeAmount() {
        Assertions.assertThrows(NotInStock.class, () -> commodity.updateInStock(-1));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void testUpdateInStockWithPositiveAmount(int amount) throws NotInStock {
        commodity.updateInStock(amount);
        Assertions.assertEquals(amount, commodity.getInStock());
    }

    @Test
    public void testInitialRateShouldBeZero() {
        Assertions.assertEquals(0, commodity.getRating());
    }

    @Test
    public void testAddRateWithInitRate() {
        commodity.setInitRate(3);
        commodity.addRate("username", 5);
        Assertions.assertEquals(4, commodity.getRating());
    }

    @Test
    public void testAddRateWithoutInitRate() {
        commodity.addRate("username", 5);
        Assertions.assertEquals(2.5, commodity.getRating());
    }

    @Test
    public void testAddRateWithMultipleUsers() {
        commodity.addRate("username1", 5);
        commodity.addRate("username2", 4);
        commodity.addRate("username3", 0);
        Assertions.assertEquals(2.25, commodity.getRating());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 11})
    public void testAddRateWithInvalidRange(int rate) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> commodity.addRate("username", rate));
    }
}
