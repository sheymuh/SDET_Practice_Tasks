package com.simbirsoft.pages;

import com.simbirsoft.pages.components.HeaderComponent;
import io.qameta.allure.Step;
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

    @Step("Сортировка результатов поиска по имени (A-Z)")
    public SearchResultPage sortByNameAsc() {
        waiter.until(ExpectedConditions.elementToBeClickable(sortDropdown));

        String oldFirstProductName;
        if (!productCards.isEmpty()) {
            oldFirstProductName = extractProductName(productCards.get(0));
        } else {
            oldFirstProductName = "";
        }

        new Select(sortDropdown).selectByVisibleText("Name A - Z");

        if (!oldFirstProductName.isEmpty()) {
            waiter.until(driver -> {
                List<WebElement> currentCards = driver.findElements(By.cssSelector("div.thumbnails.grid div.thumbnail"));
                if (currentCards.isEmpty()) {
                    return false;
                }
                String currentFirstName = extractProductName(currentCards.get(0));
                return !currentFirstName.equals(oldFirstProductName) && !currentFirstName.isEmpty();
            });
        }

        return this;
    }

    @Step("Получение названий товаров в результатах поиска")
    public List<String> getProductNames() {
        waitForProductsToLoad();
        return productCards.stream()
                .map(this::extractProductName)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());
    }

    @Step("Клик по товару с индексом {index} в результатах поиска")
    public ProductPage clickProductByIndex(int index) {
        waitForProductsToLoad();

        if (index >= productCards.size()) {
            throw new IllegalArgumentException("Index " + index + " out of bounds");
        }

        WebElement productLink = getProductLink(productCards.get(index));
        scrollAndClick(productLink);

        return new ProductPage(driver, waiter);
    }

    @Step("Получение количества товаров в результатах поиска")
    public int getProductCount() {
        try {
            waitForProductsToLoad();
            return productCards.size();
        } catch (Exception e) {
            System.err.println("Failed to get product count: " + e.getMessage());
            return 0;
        }
    }

    private String extractProductName(WebElement card) {
        try {
            WebElement parentCol = card.findElement(By.xpath(".."));
            WebElement fixedWrapper = parentCol.findElement(By.cssSelector("div.fixed_wrapper"));
            return fixedWrapper.findElement(By.cssSelector("a.prdocutname")).getText().trim();
        } catch (Exception e) {
            System.err.println("Failed to extract product name from search results: " + e.getMessage());
            return "";
        }
    }

    private WebElement getProductLink(WebElement card) {
        WebElement parentCol = card.findElement(By.xpath(".."));
        WebElement fixedWrapper = parentCol.findElement(By.cssSelector("div.fixed_wrapper"));
        return fixedWrapper.findElement(By.cssSelector("a.prdocutname"));
    }

    private void waitForProductsToLoad() {
        waiter.until(ExpectedConditions.visibilityOfAllElements(productCards));
    }

    private void scrollAndClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        waiter.until(ExpectedConditions.elementToBeClickable(element));

        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
}
