package com.simbirsoft.pages;

import com.simbirsoft.pages.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.Collectors;

public class SearchResultPage extends BasePage {

    private final HeaderComponent header;

    @FindBy(id = "sort")
    private WebElement sortDropdown;

    @FindBy(css = "div.thumbnails.grid div.thumbnail")
    private List<WebElement> productCards;

    public SearchResultPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        this.header = new HeaderComponent(driver, waiter);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 15), this);
    }

    public HeaderComponent getHeader() {
        return header;
    }

    public SearchResultPage sortByNameAsc() {
        waiter.until(ExpectedConditions.elementToBeClickable(sortDropdown));
        new Select(sortDropdown).selectByVisibleText("Name A - Z");
        sleep(1500);
        return this;
    }

    public List<String> getProductNames() {
        waitForProductsToLoad();
        return productCards.stream()
                .map(this::extractProductName)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());
    }

    public ProductPage clickProductByIndex(int index) {
        waitForProductsToLoad();

        if (index >= productCards.size()) {
            throw new IllegalArgumentException("Index " + index + " out of bounds");
        }

        WebElement productLink = getProductLink(productCards.get(index));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", productLink);
        sleep(500);

        try {
            productLink.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", productLink);
        }

        return new ProductPage(driver, waiter);
    }

    public int getProductCount() {
        try {
            waitForProductsToLoad();
            return productCards.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private String extractProductName(WebElement card) {
        try {
            WebElement parentCol = card.findElement(By.xpath(".."));
            WebElement fixedWrapper = parentCol.findElement(By.cssSelector("div.fixed_wrapper"));
            return fixedWrapper.findElement(By.cssSelector("a.prdocutname")).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private WebElement getProductLink(WebElement card) {
        WebElement parentCol = card.findElement(By.xpath(".."));
        WebElement fixedWrapper = parentCol.findElement(By.cssSelector("div.fixed_wrapper"));
        return fixedWrapper.findElement(By.cssSelector("a.prdocutname"));
    }

    private void waitForProductsToLoad() {
        sleep(2000);
        waiter.until(ExpectedConditions.visibilityOfAllElements(productCards));
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
