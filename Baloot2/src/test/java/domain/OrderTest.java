package domain;

import org.junit.jupiter.api.*;

public class OrderTest {
    private Order order;

    @BeforeEach
    public void setUp() {
        order = new Order() {{
            setId(0);
        }};
    }

    @Test
    @DisplayName("Test order.equals should only compare order ids")
    public void testEqualsComparesIds() {
        Order order = new Order() {{
            setId(0);
        }};
        Order anotherOrder = new Order() {{
            setId(0);
        }};
        Assertions.assertTrue(order.equals(anotherOrder));
    }

    @Test
    @DisplayName("Test order.equals with different order ids")
    public void testEqualsDifferentIds() {
        Order newOrder = new Order() {{
            setId(1);
        }};
        Assertions.assertFalse(order.equals(newOrder));
    }

    @Test
    @DisplayName("Test order.equals with object of another type")
    public void testEqualsWrongObject() {
        Object object = new Object();
        Assertions.assertFalse(order.equals(object));
    }

    @Test
    @DisplayName("Test order getter and setters")
    public void testOrderGetterSetters() {
        order.setId(1);
        order.setCustomer(2);
        order.setPrice(3);
        order.setQuantity(4);
        Assertions.assertEquals(1, order.getId());
        Assertions.assertEquals(2, order.getCustomer());
        Assertions.assertEquals(3, order.getPrice());
        Assertions.assertEquals(4, order.getQuantity());
    }
}
