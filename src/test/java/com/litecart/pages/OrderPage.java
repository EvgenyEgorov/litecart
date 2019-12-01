package com.litecart.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.*;

import java.util.*;

public class OrderPage extends Page {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//div[@class=\"table-responsive\"]//tr[@class=\"item\"]")
    private List<WebElement> itemsList = null;

    @FindBy(className = "formatted-value")
    private WebElement subtotalPriceElement;

    @FindBy(name = "save_customer_details")
    private WebElement saveChangesButton;

    @FindBy(name = "terms_agreed")
    private WebElement termsAgreedCheckBox;

    @FindBy(name = "confirm_order")
    private WebElement confirmOrderButton;

    @FindBy(xpath = "//*[@id=\"box-checkout-summary\"]//strong[text()=\"Cash on Delivery (Russian Federation):\"]/../../td[2]")
    private WebElement cashOnDeliveryElement;

    @FindBy(xpath = "//*[@id=\"box-checkout-summary\"]//strong[text()=\"Zone Based Shipping (Russian Federation):\"]/../../td[2]")
    private WebElement zoneBasedShippingElement;

    @FindBy(xpath = "//*[@id=\"box-checkout-summary\"]//strong[text()=\"Payment Due:\"]/../../td[2]")
    private WebElement paymentDueElement;

    @FindBy(xpath = "//h1[@class=\"title\"]")
    private WebElement orderConfirmElement;

    public OrderPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        PageFactory.initElements(driver, this);
    }

    /**
     * Get total items number in the cart.
     *
     * @return items number.
     */
    public int getItemsNumber() {
        return itemsList.size();
    }

    /**
     * Get index (zero-based) of item in the cart table.
     *
     * @param name item name in the cart.
     * @return index value >=0 if item is in the cart, -1 if item is not found.
     */
    public int getItemPositionByName(String name) {
        int index;
        for (index = 0; index < itemsList.size(); index++) {
            if (getItemName(index).equals(name)) {
                break;
            }
        }
        return index == itemsList.size() ? -1 : index;
    }

    /**
     * Get item name by index.
     *
     * @param index item index in the cart list (zero-based).
     * @return item name.
     */
    public String getItemName(int index) {
        return itemsList.get(index).getAttribute("data-name");
    }

    /**
     * Get additional item description from cart list, for example: "Size: small", etc.
     *
     * @param index item index in the cart list (zero-based).
     * @return description.
     */
    public String getItemNameDetails(int index) {
        return itemsList.get(index).findElement(By.xpath("td[2]/div[2]")).getText();
    }

    /**
     * Get item quantity.
     *
     * @param index item index in the cart list (zero-based).
     * @return quantity of item.
     */
    public int getItemQuantity(int index) {
        // This value saved as float value in the attribute. It should be converted to integer.
        return (int) Float.parseFloat(itemsList.get(index).getAttribute("data-quantity"));
    }

    /**
     * Get item price.
     *
     * @param index item index in the cart list (zero-based).
     * @return item price.
     */
    public float getItemPrice(int index) {
        return convertPriceFromStringToFloat(itemsList.get(index).getAttribute("data-price"));
    }

    /**
     * Get item summary price.
     *
     * @param index item index in the cart list (zero-based).
     * @return item summary price.
     */
    public float getItemPriceSum(int index) {
        return convertPriceFromStringToFloat(itemsList.get(index).findElement(By.xpath("td[5]")).getText());
    }

    public float getSubtotalPrice() {
        return convertPriceFromStringToFloat(subtotalPriceElement.getText());
    }

    public float getCashOnDeliveryPrice() {
        return convertPriceFromStringToFloat(cashOnDeliveryElement.getText());
    }

    public float getZoneBasedShippingPrice() {
        return convertPriceFromStringToFloat(zoneBasedShippingElement.getText());
    }

    public float getPaymentDuePrice() {
        return convertPriceFromStringToFloat(paymentDueElement.getText());
    }

    /**
     * Get message order was completed.
     * @return message text
     */
    public String getConfirmationMessage() {
        return orderConfirmElement.getText();
    }

    /**
     * Fill field on the Customer Details Form.
     *
     * @param fieldName name of field (attribute 'name').
     * @param value     field value.
     */
    public void fillField(String fieldName, String value) {
        WebElement field = driver.findElement(By.name(fieldName));
        if (fieldName.equals("country_code")) {
            // Combo box (select)
            Select select = new Select(field);
            select.selectByValue(value);
        } else {
            // Text box
            field.clear();
            field.sendKeys(value);
        }
    }

    /**
     * Confirm new order.
     */
    public void confirmOrder() {
        // Press 'Save Changes'
        saveChangesButton.click();

        // Wait for confirmation checkbox will be displayed
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("terms_agreed")));

        // Set checkbox 'I have read the Privacy Policy and Terms of Purchase and I consent.'
        termsAgreedCheckBox.click();

        // Press 'Confirm Order'
        confirmOrderButton.click();

        // Wait for page with new order will be displayed
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("box-order-success")));
    }

}
