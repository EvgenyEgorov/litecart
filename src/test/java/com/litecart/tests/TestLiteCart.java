package com.litecart.tests;

import com.litecart.pages.*;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;

/**
 * Test class verifies LiteCart web application and uses Selenium WebDriver.
 *
 * @author Evgeny Egorov
 */
public class TestLiteCart {
    // Constant _BROWSER allow to choose browser. Set:
    // Browser.CHROME - for Google Chrome
    // Browser.FIREFOX - for Mozilla Firefox
    private final Browser _BROWSER = Browser.CHROME;

    private static WebDriver driver;

    @Before
    public void startTest() {
        if (_BROWSER == Browser.CHROME) {
            System.setProperty("webdriver.chrome.driver", "src/main/resources/webdriver/chromedriver.exe");
            driver = new ChromeDriver();
        } else {
            System.setProperty("webdriver.gecko.driver", "src/main/resources/webdriver/geckodriver.exe");
            driver = new FirefoxDriver();
        }
    }

    @Test
    public void testCreateOrder() {
        Page homePage = new Page(driver);
        homePage.openURL("http://localhost/litecart/");

        // Open category "Rubber Ducks"
        homePage.openCategories("Rubber Ducks");
        CategoryPage categoryPage = new CategoryPage(driver);

        // Add two small yellow ducks to the cart
        categoryPage.openItem("Yellow Duck");
        ItemPage itemPage = new ItemPage(driver);
        itemPage.selectSize("Small");

        // Verify price is correct
        Assert.assertEquals(18, itemPage.getFinalPrice(), 0);

        itemPage.setQuantity("2");
        itemPage.addToCart();

        homePage.openCategories("Rubber Ducks");

        // Add three purple ducks to the cart
        categoryPage.openItem("Purple Duck");

        // Verify price is correct
        Assert.assertEquals(20, itemPage.getFinalPrice(), 0);

        itemPage.setQuantity("3");
        itemPage.addToCart();

        // Open the cart and verify items
        itemPage.openCart();
        OrderPage orderPage = new OrderPage(driver);

        // Verify we have two kinds of items in the cart (two rows in the table)
        Assert.assertEquals(2, orderPage.getItemsNumber());

        // We don't know how sorted items in the cart. It can be randomly.
        // We should get item position (zero-based) for item with known name.
        // If we get '-1' it means item with required name is not found in the cart and test will be failed.
        int itemPos1 = orderPage.getItemPositionByName("Yellow Duck");
        Assert.assertNotEquals(-1, itemPos1);

        int itemPos2 = orderPage.getItemPositionByName("Purple Duck");
        Assert.assertNotEquals(-1, itemPos2);

        // Verify all values to first item: "Yellow Duck"
        Assert.assertEquals("Yellow Duck", orderPage.getItemName(itemPos1));
        Assert.assertEquals("Size: Small", orderPage.getItemNameDetails(itemPos1));

        Assert.assertEquals(2, orderPage.getItemQuantity(itemPos1));
        Assert.assertEquals(18, orderPage.getItemPrice(itemPos1), 0);
        Assert.assertEquals(36, orderPage.getItemPriceSum(itemPos1), 0);

        // Verify all values to second item: "Purple Duck"
        Assert.assertEquals("Purple Duck", orderPage.getItemName(itemPos2));
        Assert.assertEquals(3, orderPage.getItemQuantity(itemPos2));
        Assert.assertEquals(20, orderPage.getItemPrice(itemPos2), 0);
        Assert.assertEquals(60, orderPage.getItemPriceSum(itemPos2), 0);

        // Verify subtotal price
        Assert.assertEquals(96, orderPage.getSubtotalPrice(), 0);

        // Fill Customer Details Form
        orderPage.fillField("company", "X1 Company");
        orderPage.fillField("tax_id", "12345");
        orderPage.fillField("firstname", "Ivan");
        orderPage.fillField("lastname", "Petrov");
        orderPage.fillField("address1", "address 1");
        orderPage.fillField("address2", "address 2");
        orderPage.fillField("postcode", "123456");
        orderPage.fillField("city", "Moscow");
        orderPage.fillField("email", "ivanov@mx.ru");
        orderPage.fillField("phone", "+79991234567");
        orderPage.fillField("country_code", "RU");

        // Verify Zone Based Shipping price
        Assert.assertEquals(8.95, orderPage.getZoneBasedShippingPrice(), 0.001);

        // Verify Cash on Delivery price
        Assert.assertEquals(5, orderPage.getCashOnDeliveryPrice(), 0);

        // Verify Payment Due price
        Assert.assertEquals(109.95, orderPage.getPaymentDuePrice(), 0.001);

        orderPage.confirmOrder();

        // Verify the order was completed successfully
        Assert.assertTrue(orderPage.getConfirmationMessage().contains("was completed successfully!"));

        orderPage.openCart();

        // Verify cart is empty
        Assert.assertTrue(orderPage.isCartEmpty());
    }

    @After
    public void finishTest() {
        driver.quit();
    }

    enum Browser {CHROME, FIREFOX}
}
