package com.litecart.pages;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.*;

import java.util.*;

public class CategoryPage extends Page {
    private WebDriver driver;

    @FindBy(xpath = "//div[@class='name']")
    private List<WebElement> items = null;

    public CategoryPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * Click item on the page.
     * If item is not found an error message is printed to console.
     *
     * @param name item name (e.g. 'Green Duck').
     */
    public void openItem(String name) {
        boolean isItemFound = false;
        for (WebElement item : items) {
            if (name.equals(item.getText())) {
                isItemFound = true;
                item.click();
                break;
            }
        }
        Assert.assertTrue("Item '" + name + "' not found on the page.", isItemFound);
    }
}
