package com.simbirsoft.pages;

import com.simbirsoft.helpers.PriceHelper;
import com.simbirsoft.pages.components.HeaderComponent;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CategoryPage extends BasePage {

    private final HeaderComponent header;

    @FindBy(id = "sort")
    private WebElement sortDropdown;

    @FindBy(css = "div.thumbnails.grid div.thumbnail")
    private List<WebElement> productCards;

    public CategoryPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        this.header = new HeaderComponent(driver, waiter);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 15), this);
    }

    @Step("Сортировка товаров по: {sortOption}")
    public CategoryPage sortBy(String sortOption) {
        waiter.until(ExpectedConditions.elementToBeClickable(sortDropdown));

        String oldUrl = driver.getCurrentUrl();
        new Select(sortDropdown).selectByVisibleText(sortOption);

        waiter.until(driver -> !Objects.equals(driver.getCurrentUrl(), oldUrl));
        waiter.until(ExpectedConditions.visibilityOfAllElements(productCards));

        return this;
    }

    @Step("Получение названий всех товаров на странице")
    public List<String> getProductNames() {
        waitForProductsToLoad();
        return productCards.stream()
                .map(this::extractProductName)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());
    }

    @Step("Получение цен всех товаров на странице")
    public List<Double> getProductPrices() {
        waitForProductsToLoad();
        return productCards.stream()
                .map(this::extractProductPrice)
                .filter(price -> price > 0)
                .collect(Collectors.toList());
    }

    @Step("Получение количества товаров на странице")
    public int getProductCount() {
        try {
            waitForProductsToLoad();
            return productCards.size();
        } catch (Exception e) {
            System.err.println("Failed to extract product price: " + e.getMessage());
            return 0;
        }
    }

    private String extractProductName(WebElement card) {
        try {
            WebElement parentCol = card.findElement(By.xpath(".."));
            WebElement fixedWrapper = parentCol.findElement(By.cssSelector("div.fixed_wrapper"));
            return fixedWrapper.findElement(By.cssSelector("a.prdocutname")).getText().trim();
        } catch (Exception e) {
            System.err.println("Failed to extract product name: " + e.getMessage());
            return "";
        }
    }

    private double extractProductPrice(WebElement card) {
        try {
            WebElement pricetag = card.findElement(By.cssSelector("div.pricetag"));
            WebElement priceContainer = pricetag.findElement(By.cssSelector("div.price"));
            WebElement priceElement = priceContainer.findElement(By.cssSelector("div.oneprice, div.pricenew"));
            return PriceHelper.parsePrice(priceElement.getText().trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void waitForProductsToLoad() {
        waiter.until(ExpectedConditions.visibilityOfAllElements(productCards));
    }
}
