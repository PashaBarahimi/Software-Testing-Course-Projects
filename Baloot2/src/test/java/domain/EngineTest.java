package domain;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

public class EngineTest {
    private Engine engine;

    @BeforeEach
    public void setUp() {
        engine = new Engine();
    }

    @NotNull
    public static Order constructOrder(int id, int customer, int price, int quantity) {
        Order order = new Order();
        order.setId(id);
        order.setCustomer(customer);
        order.setPrice(price);
        order.setQuantity(quantity);
        return order;
    }

    // getAverageOrderQuantityByCustomer

    @Test
    @DisplayName("Test average quantity with no orders")
    public void testAverageQuantityNoOrders() {
        Assertions.assertEquals(0, engine.getAverageOrderQuantityByCustomer(1));
    }

    @Test
    @DisplayName("Test average quantity with one order")
    public void testAverageQuantityOneOrder() {
        Order order = constructOrder(0, 1, 4, 2);
        engine.orderHistory.add(order);
        Assertions.assertEquals(2, engine.getAverageOrderQuantityByCustomer(1));
    }

    @Test
    @DisplayName("Test average quantity with multiple orders")
    public void testAverageQuantityMultipleOrders() {
        Order order = constructOrder(0, 1, 4, 2);
        Order order2 = constructOrder(1, 1, 8, 6);
        engine.orderHistory.add(order);
        engine.orderHistory.add(order2);
        Assertions.assertEquals(4, engine.getAverageOrderQuantityByCustomer(1));
    }

    @Test
    @DisplayName("Test average quantity with unknown customer")
    public void testAverageQuantityUnknownCustomer() {
        Order order = constructOrder(0, 1, 4, 2);
        engine.orderHistory.add(order);
        Assertions.assertThrows(Exception.class, () -> engine.getAverageOrderQuantityByCustomer(2));
    }

    // getQuantityPatternByPrice

    @Test
    @DisplayName("Test quantity pattern with no orders")
    public void testQuantityPatternNoOrders() {
        Assertions.assertEquals(0, engine.getQuantityPatternByPrice(1));
    }

    @Test
    @DisplayName("Test quantity pattern with linear increasing quantity for a price")
    public void testQuantityPatternWithPattern() {
        Order order = constructOrder(0, 1, 4, 2);
        Order order2 = constructOrder(1, 2, 4, 8);
        Order order3 = constructOrder(2, 2, 4, 14);
        engine.orderHistory.add(order);
        engine.orderHistory.add(order2);
        engine.orderHistory.add(order3);
        Assertions.assertEquals(6, engine.getQuantityPatternByPrice(4));
    }

    @Test
    @DisplayName("Test quantity pattern without pattern")
    public void testQuantityPatternWithoutPattern() {
        Order order = constructOrder(0, 1, 4, 2);
        Order order2 = constructOrder(1, 2, 4, 8);
        Order order3 = constructOrder(2, 2, 4, 13);
        engine.orderHistory.add(order);
        engine.orderHistory.add(order2);
        engine.orderHistory.add(order3);
        Assertions.assertEquals(0, engine.getQuantityPatternByPrice(4));
    }

    @Test
    @DisplayName("Test quantity pattern with pattern and different prices")
    public void testQuantityPatternWithPatternAndDifferentPrices() {
        Order order = constructOrder(0, 1, 4, 2);
        Order order2 = constructOrder(1, 2, 4, 8);
        Order order3 = constructOrder(2, 2, 5, 13);
        Order order4 = constructOrder(2, 2, 4, 14);
        engine.orderHistory.add(order);
        engine.orderHistory.add(order2);
        engine.orderHistory.add(order3);
        engine.orderHistory.add(order4);
        Assertions.assertEquals(6, engine.getQuantityPatternByPrice(4));
    }

    // getCustomerFraudulentQuantity

    @Test
    @DisplayName("Test fraudulent quantity with order quantity more than the average")
    public void testFraudulentQuantityMoreThanAvg() {
        Order order = constructOrder(0, 1, 4, 2);
        Order order2 = constructOrder(1, 1, 8, 6);
        engine.orderHistory.add(order);
        Assertions.assertEquals(4, engine.getCustomerFraudulentQuantity(order2));
    }

    @Test
    @DisplayName("Test fraudulent quantity with order quantity equal to the average")
    public void testFraudulentQuantityEqualToAvg() {
        Order order = constructOrder(0, 1, 4, 2);
        engine.orderHistory.add(order);
        Assertions.assertEquals(0, engine.getCustomerFraudulentQuantity(order));
    }

    @Test
    @DisplayName("Test fraudulent quantity with order quantity less than the average")
    public void testFraudulentQuantityLessThanAvg() {
        Order order = constructOrder(0, 1, 4, 2);
        Order order2 = constructOrder(1, 1, 8, 6);
        engine.orderHistory.add(order2);
        Assertions.assertEquals(0, engine.getCustomerFraudulentQuantity(order));
    }

    // addOrderAndGetFraudulentQuantity

    @Test
    @DisplayName("Test adding already existing order")
    public void testAddingAlreadyExistingOrder() {
        Order order = constructOrder(0, 1, 4, 2);
        engine.orderHistory.add(order);
        Assertions.assertEquals(0, engine.addOrderAndGetFraudulentQuantity(order));
    }

    @Test
    @DisplayName("Test adding order and getting fraudulent quantity (if not 0)")
    public void testAddOrderAndGetFraudulent() {
        Order order = constructOrder(0, 1, 4, 2);
        Order order2 = constructOrder(1, 1, 8, 6);
        engine.orderHistory.add(order);
        Assertions.assertEquals(4, engine.addOrderAndGetFraudulentQuantity(order2));
        Assertions.assertTrue(engine.orderHistory.contains(order2));
    }

    @Test
    @DisplayName("Test adding order and getting fraudulent quantity of 0 (should return pattern)")
    public void testAddOrderAndGetFraudulentBelowAvg() {
        Order order = constructOrder(0, 1, 4, 8);
        Order order2 = constructOrder(1, 1, 4, 14);
        Order order3 = constructOrder(2, 1, 4, 2);
        engine.orderHistory.add(order);
        engine.orderHistory.add(order2);
        Assertions.assertEquals(6, engine.addOrderAndGetFraudulentQuantity(order3));
        Assertions.assertTrue(engine.orderHistory.contains(order3));
    }
}
