package com.simbirsoft.pages;

import com.simbirsoft.pages.components.HeaderComponent;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Objects;

public class ProductPage extends BasePage {

    private final HeaderComponent header;

    @FindBy(id = "product_quantity")
    private WebElement quantityInput;

    @FindBy(css = "a.cart")
    private WebElement addToCartButton;

    public ProductPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        this.header = new HeaderComponent(driver, waiter);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
    }

    @Step("Установка количества товара: {quantity}")
    public ProductPage setQuantity(int quantity) {
        waiter.until(ExpectedConditions.visibilityOf(quantityInput));
        quantityInput.clear();
        quantityInput.sendKeys(String.valueOf(quantity));
        return this;
    }

    @Step("Выбор доступных опций товара")
    public ProductPage selectAvailableOptions() {
        List<WebElement> radioOptions = driver.findElements(By.cssSelector(".form-group input[type='radio']"));
        if (!radioOptions.isEmpty()) {
            radioOptions.stream()
                    .filter(WebElement::isEnabled)
                    .findFirst().ifPresent(firstAvailableRadio -> ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstAvailableRadio));
        }

        List<WebElement> selectElements = driver.findElements(By.cssSelector(".form-group select"));
        if (!selectElements.isEmpty()) {
            for (WebElement select : selectElements) {
                List<WebElement> options = select.findElements(By.cssSelector("option:not([disabled])"));
                if (!options.isEmpty()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].selected = true;", options.get(0));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'));", select);
                }
            }
        }

        return this;
    }

    @Step("Добавление товара в корзину")
    public CartPage addToCart() {
        String productUrl = driver.getCurrentUrl();
        System.out.println("Adding product from URL: " + productUrl);

        selectAvailableOptions();

        waiter.until(ExpectedConditions.elementToBeClickable(addToCartButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartButton);

        if (Objects.equals(driver.getCurrentUrl(), productUrl)) {
            System.out.println("Warning: URL didn't change after clicking Add to Cart");
            addToCartButton.click();
        }

        return new CartPage(driver, waiter);
    }
}
