package model;

import exceptions.NotInStock;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CommodityTest {
    private Commodity commodity;
    private final float delta = 1e-3f;

    @BeforeEach
    public void setUp() {
        commodity = new Commodity();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    @DisplayName("Test updating stock count with positive value")
    public void testUpdateInStockWithPositiveAmount(int amount) throws NotInStock {
        commodity.updateInStock(amount);
        Assertions.assertEquals(amount, commodity.getInStock());
    }

    @Test
    @DisplayName("Test updating stock count with negative value")
    public void testUpdateInStockWithNegativeAmount() {
        Assertions.assertThrows(NotInStock.class, () -> commodity.updateInStock(-1));
    }

    @Test
    @DisplayName("Test initial rating")
    public void testInitialRateShouldBeZero() {
        Assertions.assertEquals(0f, commodity.getRating());
    }

    @Test
    @DisplayName("Test adding the first rating")
    public void testAddRateWithoutInitRate() throws IllegalArgumentException {
        commodity.addRate("username", 5);
        Assertions.assertEquals(2.5f, commodity.getRating(), delta);
    }

    @Test
    @DisplayName("Test adding rating with different init rate")
    public void testAddRateWithInitRate() throws IllegalArgumentException {
        commodity.setInitRate(3);
        commodity.addRate("username", 5);
        Assertions.assertEquals(4f, commodity.getRating(), delta);
    }

    @Test
    @DisplayName("Test adding multiple ratings")
    public void testAddRateWithMultipleUsers() throws IllegalArgumentException {
        commodity.addRate("username1", 5);
        commodity.addRate("username2", 4);
        commodity.addRate("username3", 2);
        Assertions.assertEquals(2.75f, commodity.getRating(), delta);
    }

    @Test
    @DisplayName("Test adding multiples ratings as one user")
    public void testAddMultipleRatesOneUser() {
        commodity.addRate("username", 1);
        commodity.addRate("username", 2);
        commodity.addRate("username", 5);
        Assertions.assertEquals(1, commodity.getUserRate().size());
        Assertions.assertEquals(2.5f, commodity.getRating());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    @DisplayName("Test adding rate with corner values")
    public void testAddRateWithCornerValues(int rate) throws IllegalArgumentException {
        Assertions.assertDoesNotThrow(() -> commodity.addRate("username", rate));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 11})
    @DisplayName("Test adding rate in invalid range")
    public void testAddRateWithInvalidRange(int rate) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> commodity.addRate("username", rate));
    }
}
