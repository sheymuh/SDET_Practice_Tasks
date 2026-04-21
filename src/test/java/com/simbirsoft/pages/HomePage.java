package com.simbirsoft.pages;

import com.simbirsoft.helpers.ParameterProvider;
import com.simbirsoft.pages.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class HomePage extends BasePage {

    private final HeaderComponent header;

    @FindBy(css = "a[href*='path=49']")
    private WebElement fragranceMenu;

    @FindBy(css = "a[href*='path=49_51']")
    private WebElement menSubcategory;

    @FindBy(css = "section#featured div.thumbnail")
    private List<WebElement> featuredProducts;

    public HomePage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        this.header = new HeaderComponent(driver, waiter);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 15), this);
    }

    public HomePage open() {
        driver.get(ParameterProvider.get("base.url"));
        return this;
    }

    public HeaderComponent getHeader() {
        return header;
    }

    public CategoryPage navigateToFragranceMenCategory() {
        driver.get("https://automationteststore.com/index.php?rt=product/category&path=49_51");
        return new CategoryPage(driver, waiter);
    }

    public List<WebElement> getFeaturedProducts() {
        waiter.until(ExpectedConditions.visibilityOfAllElements(featuredProducts));
        return featuredProducts;
    }

    public ProductPage clickProductByIndex(int index) {
        List<WebElement> products = getFeaturedProducts();

        if (products.isEmpty()) {
            throw new IllegalStateException("No featured products found");
        }
        if (index >= products.size()) {
            throw new IllegalArgumentException("Index " + index + " out of bounds");
        }

        WebElement product = products.get(index);
        WebElement parentCol = product.findElement(By.xpath(".."));
        WebElement fixedWrapper = parentCol.findElement(By.cssSelector("div.fixed_wrapper"));
        WebElement productLink = fixedWrapper.findElement(By.cssSelector("a.prdocutname"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", productLink);
        sleep(500);

        try {
            productLink.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", productLink);
        }

        return new ProductPage(driver, waiter);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}