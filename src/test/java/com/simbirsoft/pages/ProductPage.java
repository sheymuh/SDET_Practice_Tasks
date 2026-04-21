package com.simbirsoft.pages;

import com.simbirsoft.helpers.PriceHelper;
import com.simbirsoft.pages.components.HeaderComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductPage extends BasePage {

    private final HeaderComponent header;

    @FindBy(id = "product_quantity")
    private WebElement quantityInput;

    @FindBy(css = "a.cart")
    private WebElement addToCartButton;

    @FindBy(css = "h1.productname")
    private WebElement productName;

    @FindBy(css = "div.productfilneprice")
    private WebElement productPrice;

    public ProductPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        this.header = new HeaderComponent(driver, waiter);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
    }

    public HeaderComponent getHeader() {
        return header;
    }

    public ProductPage setQuantity(int quantity) {
        waiter.until(ExpectedConditions.visibilityOf(quantityInput));
        quantityInput.clear();
        quantityInput.sendKeys(String.valueOf(quantity));
        return this;
    }

    public ProductPage addToCart() {
        waiter.until(ExpectedConditions.elementToBeClickable(addToCartButton));
        addToCartButton.click();
        sleep(1500);
        return this;
    }

    public HomePage continueShoppingToHome() {
        driver.get("https://automationteststore.com/");
        return new HomePage(driver, waiter);
    }

    public SearchResultPage continueShoppingToSearch() {
        driver.get("https://automationteststore.com/index.php?rt=product/search&keyword=shirt&sort=pd.name-ASC");
        return new SearchResultPage(driver, waiter);
    }

    public String getProductName() {
        waiter.until(ExpectedConditions.visibilityOf(productName));
        return productName.getText().trim();
    }

    public double getProductPrice() {
        waiter.until(ExpectedConditions.visibilityOf(productPrice));
        return PriceHelper.parsePrice(productPrice.getText());
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
