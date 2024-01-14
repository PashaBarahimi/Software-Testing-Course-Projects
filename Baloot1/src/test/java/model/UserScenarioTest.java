package model;

import application.BalootApplication;
import exceptions.CommodityIsNotInBuyList;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = BalootApplication.class)
public class UserScenarioTest {
    private User user;
    private Commodity commodity;
    private Exception exception = null;
    private final int manyItemsCount = 4;

    @Given("A sample user")
    public void aSampleUser() {
        user = new User("username", "password", "email@mail.com", "2023-01-01", "address");
    }

    @Given("A sample user with {float} credit")
    public void aSampleUserWithInitialCredit(float initialCredit) {
        aSampleUser();
        user.setCredit(initialCredit);
    }

    @Given("One item in the buy list")
    public void oneItemInTheBuyList() {
        commodity = new Commodity();
        commodity.setId("1");
        user.getBuyList().put("1", 1);
    }

    @Given("Many of one item in the buy list")
    public void manyOfOneItemInTheBuyList() {
        commodity = new Commodity();
        commodity.setId("1");
        user.getBuyList().put("1", manyItemsCount);
    }

    @When("The user adds {float} to their account")
    public void theUserAddsCreditToTheirAccount(float credit) {
        try {
            user.addCredit(credit);
        } catch (InvalidCreditRange ex) {
            exception = ex;
        }
    }

    @When("The user withdraws {float} from their account")
    public void theUserWithdrawsCreditFromTheirAccount(float credit) {
        try {
            user.withdrawCredit(credit);
        } catch (InsufficientCredit | IllegalArgumentException ex) {
            exception = ex;
        }
    }

    @When("The user removes the item from their buy list")
    public void theUserRemovesTheItemFromTheirBuyList() {
        try {
            user.removeItemFromBuyList(commodity);
        } catch (CommodityIsNotInBuyList ex) {
            exception = ex;
        }
    }

    @When("The user removes an item not in their buy list")
    public void theUserRemovesAnItemNotInTheirBuyList() {
        commodity = new Commodity();
        commodity.setId("NotInList");
        try {
            user.removeItemFromBuyList(commodity);
        } catch (CommodityIsNotInBuyList ex) {
            exception = ex;
        }
    }

    @Then("The user credit should be {float}")
    public void theUserCreditShouldBe(float expectedCredit) {
        Assertions.assertEquals(expectedCredit, user.getCredit());
    }

    @Then("InvalidCreditRange is thrown")
    public void InvalidCreditRangeIsThrown() {
        Assertions.assertInstanceOf(InvalidCreditRange.class, exception);
    }

    @Then("InsufficientCredit is thrown")
    public void insufficientCreditIsThrown() {
        Assertions.assertInstanceOf(InsufficientCredit.class, exception);
    }

    @Then("IllegalArgumentException is thrown")
    public void illegalArgumentExceptionIsThrown() {
        Assertions.assertInstanceOf(IllegalArgumentException.class, exception);
    }

    @Then("The item should be removed from the buy list")
    public void theItemShouldBeRemovedFromTheBuyList() {
        Assertions.assertFalse(user.getBuyList().containsKey(commodity.getId()));
    }

    @Then("The item quantity should be lowered by one")
    public void theItemQuantityShouldBeLoweredByOne() {
        Assertions.assertEquals(manyItemsCount - 1, user.getBuyList().get(commodity.getId()));
    }

    @Then("CommodityIsNotInBuyList is thrown")
    public void commodityIsNotInBuyListIsThrown() {
        Assertions.assertInstanceOf(CommodityIsNotInBuyList.class, exception);
    }
}
