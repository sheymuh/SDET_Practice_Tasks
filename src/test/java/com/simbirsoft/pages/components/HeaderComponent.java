package com.simbirsoft.pages.components;

import com.simbirsoft.pages.BasePage;
import com.simbirsoft.pages.CartPage;
import com.simbirsoft.pages.SearchResultPage;
import io.qameta.allure.Step;
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

    @FindBy(css = "ul.nav.topcart")
    private WebElement cartDropdown;

    @FindBy(css = "li[data-id='menu_cart'] a")
    private WebElement cartLink;

    public HeaderComponent(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
    }

    @Step("Поиск и отправка запроса: '{query}'")
    public SearchResultPage searchForAndSubmit(String query) {
        waiter.until(ExpectedConditions.elementToBeClickable(searchInput));
        searchInput.clear();
        searchInput.sendKeys(query);
        searchInput.sendKeys(Keys.ENTER);
        return new SearchResultPage(driver, waiter);
    }

    @Step("Переход в корзину")
    public CartPage goToCart() {
        waiter.until(ExpectedConditions.elementToBeClickable(cartDropdown));
        initActions().moveToElement(cartDropdown).perform();

        waiter.until(ExpectedConditions.elementToBeClickable(cartLink));
        cartLink.click();

        return new CartPage(driver, waiter);
    }
}
