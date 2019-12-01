package com.litecart.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.*;

import java.util.regex.*;

import static java.util.regex.Pattern.*;

public class Page {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String cartEmptyMessage = "There are no items in your cart.";

    @FindBy(id = "cart")
    private WebElement cartElement;

    @FindBy(id = "box-category-tree")
    private WebElement categoriesTree;

    @FindBy(tagName = "em")
    private WebElement cartEmptyMessageElement;

    @FindBy(xpath = "//button[text()=\"Confirm Order\"]")
    private WebElement cartConfirmOrderButton;

    private By productsList = By.xpath("//section[@class=\"listing products\"]");

    public Page(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 5);
        PageFactory.initElements(driver, this);
    }

    /**
     * Open the cart.
     */
    public void openCart() {
        cartElement.click();
        // Wait page to be loaded
        wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOf(cartConfirmOrderButton),
                ExpectedConditions.textToBePresentInElement(cartEmptyMessageElement, cartEmptyMessage)));
    }

    /**
     * Verify cart is empty.
     *
     * @return true if cart is empty, false - otherwise.
     */
    public boolean isCartEmpty() {
        return cartEmptyMessageElement.getText().equals(cartEmptyMessage);
    }

    /**
     * Open page by URL.
     *
     * @param URL link to the page.
     */
    public void openURL(String URL) {
        driver.get(URL);
    }

    /**
     * Open category page by name.
     *
     * @param categoryName category name (for example 'Rubber Ducks')
     */
    public void openCategories(String categoryName) {
        // Scroll page to 'section' web element
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", categoriesTree);
        categoriesTree.findElement(By.partialLinkText(categoryName)).click();
        // Wait for all products list page to be loaded
        wait.until(ExpectedConditions.visibilityOfElementLocated(productsList));
    }

    /**
     * Convert price value from String to float.
     *
     * @param priceStr price as String value.
     * @return price as float value.
     */
    float convertPriceFromStringToFloat(String priceStr) {
        Matcher matcher = compile("\\d+\\.*\\d+").matcher(priceStr);
        return Float.parseFloat(matcher.find() ? matcher.group() : "");
    }
}
