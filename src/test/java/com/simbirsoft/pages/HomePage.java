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
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class HomePage extends BasePage {

    private final HeaderComponent header;

    @FindBy(xpath = "//ul[contains(@class, 'nav')]//a[contains(text(),'Fragrance')]")
    private WebElement fragranceMenu;

    @FindBy(xpath = "//li[a[contains(text(),'Fragrance')]]//div[contains(@class, 'subcategories')]//a[contains(text(),'Men')]")
    private WebElement menSubcategory;

    @FindBy(css = "section#featured div.thumbnail")
    private List<WebElement> featuredProducts;

    @FindBy(css = "a.logo")
    private WebElement logoButton;

    public HomePage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        this.header = new HeaderComponent(driver, waiter);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 15), this);
    }

    public HeaderComponent getHeader() {
        return header;
    }

    @Step("Переход в категорию Fragrance > Men")
    public CategoryPage navigateToFragranceMenCategory() {
        waiter.until(ExpectedConditions.visibilityOf(fragranceMenu));
        initActions().moveToElement(fragranceMenu).perform();
        waiter.until(ExpectedConditions.elementToBeClickable(menSubcategory));
        menSubcategory.click();

        return new CategoryPage(driver, waiter);
    }

    public List<WebElement> getFeaturedProducts() {
        waiter.until(ExpectedConditions.visibilityOfAllElements(featuredProducts));
        return featuredProducts;
    }

    @Step("Возврат на главную страницу")
    public HomePage returnToHome() {
        waiter.until(ExpectedConditions.elementToBeClickable(logoButton));
        logoButton.click();
        waiter.until(ExpectedConditions.visibilityOf(logoButton));
        waiter.until(ExpectedConditions.visibilityOfAllElements(featuredProducts));
        waiter.until(driver -> Objects.equals(((JavascriptExecutor) driver)
                .executeScript("return document.readyState"), "complete"));
        return new HomePage(driver, waiter);
    }

    @Step("Добавление {productCount} случайных товаров в корзину")
    public HomePage addRandomProductsToCart(int productCount, int minQuantity, int maxQuantity) {
        int addedCount = 0;
        int maxAttempts = productCount * 3;
        int attempts = 0;

        while (addedCount < productCount && attempts < maxAttempts) {
            attempts++;

            List<WebElement> currentProducts = getAllAvailableProducts();
            if (currentProducts.isEmpty()) {
                continue;
            }

            int randomIndex = ThreadLocalRandom.current().nextInt(0, currentProducts.size());
            int quantity = ThreadLocalRandom.current().nextInt(minQuantity, maxQuantity);

            try {
                WebElement productLink = getProductLink(currentProducts.get(randomIndex));
                String productName = productLink.getText();
                String currentUrl = driver.getCurrentUrl();

                System.out.println("Attempt " + attempts + ": Trying to add product: " + productName);

                scrollAndClick(productLink);
                waiter.until(driver -> !Objects.equals(driver.getCurrentUrl(), currentUrl));

                ProductPage productPage = new ProductPage(driver, waiter);
                productPage.setQuantity(quantity);

                CartPage cartPage = productPage.addToCart();

                if (cartPage.getItemCount() > addedCount) {
                    addedCount++;
                    System.out.println("Successfully added: " + productName + ". Total added: " + addedCount);
                } else {
                    System.out.println("Failed to add: " + productName + " - item count didn't increase");
                }

                returnToHome();
            } catch (Exception e) {
                System.err.println("Error adding product: " + e.getMessage());
                try {
                    returnToHome();
                    waiter.until(driver -> Objects.equals(((JavascriptExecutor) driver)
                            .executeScript("return document.readyState"), "complete"));
                } catch (Exception ex) {
                    System.err.println("Error returning to home: " + e.getMessage());
                }
            }
        }

        System.out.println("Final: Added " + addedCount + " out of " + productCount + " products");

        if (addedCount < productCount) {
            throw new IllegalStateException(
                    String.format("Only added %d out of %d products after %d attempts",
                            addedCount, productCount, attempts));
        }

        return this;
    }

    private List<WebElement> getAllAvailableProducts() {
        List<WebElement> allProducts = new ArrayList<>();

        waiter.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("section#featured")));
        allProducts.addAll(getFeaturedProducts());

        List<WebElement> latestProducts = waiter.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("section#latest div.thumbnail")));
        allProducts.addAll(latestProducts);

        List<WebElement> bestsellerProducts = waiter.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("section#bestseller div.thumbnail")));
        allProducts.addAll(bestsellerProducts);

        return allProducts;
    }

    private WebElement getProductLink(WebElement product) {
        WebElement parentCol = product.findElement(By.xpath(".."));
        WebElement fixedWrapper = parentCol.findElement(By.cssSelector("div.fixed_wrapper"));
        return fixedWrapper.findElement(By.cssSelector("a.prdocutname"));
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
