package com.litecart.pages;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.*;

public class ItemPage extends Page {
    private WebDriver driver;

    @FindBy(tagName = "select")
    private WebElement selectElement;

    @FindBy(css = "div.buy_now > form > div.price-wrapper > strong")
    private WebElement campaignPriceElement;

    @FindBy(css = "div.buy_now > form > div.price-wrapper > span")
    private WebElement priceElement;

    @FindBy(xpath = "//input[@name=\"quantity\"]")
    private WebElement quantityElement;

    @FindBy(xpath = "//button[@name=\"add_cart_product\"]")
    private WebElement addToCartButton;

    private By quantityInCartElement = By.xpath("//div[@class=\"badge quantity\"]");

    public ItemPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * Select field 'Size' for item if it possible.
     *
     * @param sizeName name of size (e.g. 'Small').
     */
    public void selectSize(String sizeName) {
        try {
            Select select = new Select(selectElement);
            select.selectByValue(sizeName);
        } catch (NoSuchElementException e) {
            System.err.println("-- ERROR: size '" + sizeName + "' can not be selected.");
        }
    }

    /**
     * Get item final price (with discount) on the item details page.
     *
     * @return value of price.
     */
    public float getFinalPrice() {
        String priceStr = null;

        try {
            priceStr = campaignPriceElement.getText();
        } catch (NoSuchElementException e1) {
            try {
                priceStr = priceElement.getText();
            } catch (NoSuchElementException e2) {
                System.err.println("-- ERROR: price not found.");
            }
        }
        Assert.assertNotNull("Field 'price' not found for the item.", priceStr);

        return convertPriceFromStringToFloat(priceStr);
    }

    /**
     * Set quantity field.
     *
     * @param quantity amount of items.
     */
    public void setQuantity(String quantity) {
        quantityElement.clear();
        quantityElement.sendKeys(quantity);
    }

    /**
     * Add item to the cart.
     */
    public void addToCart() {
        String quantityAfter;
        String quantityBefore = driver.findElement(quantityInCartElement).getText();
        addToCartButton.click();

        // Wait when cart items quantity will be updated. Not more 3 seconds.
        long startTime = System.currentTimeMillis();
        do {
            quantityAfter = driver.findElement(quantityInCartElement).getText();
        }
        while (quantityBefore.equals(quantityAfter) && System.currentTimeMillis() - startTime < 3000);

    }
}
