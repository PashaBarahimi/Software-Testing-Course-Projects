package model;

import exceptions.CommodityIsNotInBuyList;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;
import exceptions.NotInStock;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class UserTest {
    private User user;
    private final float initialCredit = 10.0f;
    private final float delta = 1e-3f;

    @BeforeEach
    public void setUp() {
        user = new User("username", "password", "email@mail.com", "2023-01-01", "address");
        user.setCredit(initialCredit);
    }

    @Test
    @DisplayName("Test user constructor")
    public void testUserConstructor() {
        Assertions.assertEquals("username", user.getUsername());
        Assertions.assertEquals("password", user.getPassword());
        Assertions.assertEquals("email@mail.com", user.getEmail());
        Assertions.assertEquals("2023-01-01", user.getBirthDate());
        Assertions.assertEquals("address", user.getAddress());
    }

    @ParameterizedTest
    @ValueSource(floats = {0f, 0.2f, 2.2f})
    @DisplayName("Test adding credit to user")
    public void testAddCredit(float credit) throws InvalidCreditRange {
        user.addCredit(credit);
        Assertions.assertEquals(user.getCredit(), initialCredit + credit, delta);
    }

    @Test
    @DisplayName("Test adding negative credit to user")
    public void testAddNegativeCredit() {
        Assertions.assertThrows(InvalidCreditRange.class, () -> user.addCredit(-2.2f));
    }

    @ParameterizedTest
    @ValueSource(floats = {initialCredit, 0f, 1f})
    @DisplayName("Test withdrawing credit from user")
    public void testWithdrawCredit(float credit) throws InsufficientCredit, IllegalArgumentException {
        user.withdrawCredit(credit);
        Assertions.assertEquals(user.getCredit(), initialCredit - credit, delta);
    }

    @Test
    @DisplayName("Test withdrawing more credit than user's")
    public void testWithdrawMoreCredit() {
        Assertions.assertThrows(InsufficientCredit.class, () -> user.withdrawCredit(2 * initialCredit));
    }

    @Test
    @DisplayName("Test withdrawing negative credit")
    public void testWithdrawNegativeCredit() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> user.withdrawCredit(-2.2f));
    }

    @Test
    @DisplayName("Test adding to buy items when no stock")
    public void testAddBuyItemNotInStock() {
        Commodity commodity = new Commodity();
        commodity.setInStock(0);
        Assertions.assertThrows(NotInStock.class, () -> user.addBuyItem(commodity));
    }

    @Test
    @DisplayName("Test adding to buy items for the first time")
    public void testAddBuyItemFirstTime() throws NotInStock {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        commodity.setInStock(2);
        user.addBuyItem(commodity);
        Assertions.assertEquals(1, user.getBuyList().get("1"));
    }

    @Test
    @DisplayName("Test adding more quantity to buy item")
    public void testAddBuyItemMoreQuantity() throws NotInStock {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        commodity.setInStock(4);
        user.getBuyList().put("1", 1);
        user.addBuyItem(commodity);
        Assertions.assertEquals(2, user.getBuyList().get("1"));
    }

    @Test
    @DisplayName("Test purchasing negative quantity")
    public void testAddPurchasedNegativeQuantity() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> user.addPurchasedItem("1", -1));
    }

    @Test
    @DisplayName("Test adding purchased item")
    public void testAddPurchased() throws IllegalArgumentException {
        user.addPurchasedItem("1", 2);
        Assertions.assertEquals(2, user.getPurchasedList().get("1"));
    }

    @Test
    @DisplayName("Test increasing purchased item quantity")
    public void testAddPurchasedQuantity() throws IllegalArgumentException {
        user.getPurchasedList().put("1", 2);
        user.addPurchasedItem("1", 3);
        Assertions.assertEquals(5, user.getPurchasedList().get("1"));
    }

    @Test
    @DisplayName("Test removing an item not in buy list")
    public void testRemoveItemNotInBuyList() {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        Assertions.assertThrows(CommodityIsNotInBuyList.class, () -> user.removeItemFromBuyList(commodity));
    }

    @Test
    @DisplayName("Test removing an item from buy list")
    public void testRemoveItemInBuyList() throws CommodityIsNotInBuyList {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        user.getBuyList().put("1", 1);
        user.removeItemFromBuyList(commodity);
        Assertions.assertFalse(user.getBuyList().containsKey("1"));
    }

    @Test
    @DisplayName("Test removing one quantity from a buy list")
    public void testRemoveItemQuantityInBuyList() throws CommodityIsNotInBuyList {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        user.getBuyList().put("1", 4);
        user.removeItemFromBuyList(commodity);
        Assertions.assertEquals(3, user.getBuyList().get("1"));
    }
}
