package com.simbirsoft.pages.components;

import com.simbirsoft.pages.BasePage;
import com.simbirsoft.pages.CartPage;
import com.simbirsoft.pages.SearchResultPage;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HeaderComponent extends BasePage {

    @FindBy(id = "filter_keyword")
    private WebElement searchInput;

    @FindBy(css = "div.button-in-search")
    private WebElement searchButton;

    @FindBy(css = "ul.nav.topcart span.cart_total")
    private WebElement cartTotal;

    public HeaderComponent(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
    }

    public HeaderComponent searchFor(String query) {
        waiter.until(ExpectedConditions.elementToBeClickable(searchInput));
        searchInput.clear();
        searchInput.sendKeys(query);
        return this;
    }

    public SearchResultPage clickSearchButton() {
        waiter.until(ExpectedConditions.elementToBeClickable(searchButton));
        searchButton.click();
        return new SearchResultPage(driver, waiter);
    }

    public SearchResultPage searchForAndSubmit(String query) {
        waiter.until(ExpectedConditions.elementToBeClickable(searchInput));
        searchInput.clear();
        searchInput.sendKeys(query);
        searchInput.sendKeys(Keys.ENTER);
        return new SearchResultPage(driver, waiter);
    }

    public CartPage goToCart() {
        driver.get("https://automationteststore.com/index.php?rt=checkout/cart");
        return new CartPage(driver, waiter);
    }

    public String getCartTotal() {
        waiter.until(ExpectedConditions.visibilityOf(cartTotal));
        return cartTotal.getText();
    }
}
