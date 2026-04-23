package com.simbirsoft.pages;

import com.simbirsoft.helpers.PriceHelper;
import com.simbirsoft.pages.components.CartItem;
import com.simbirsoft.pages.components.HeaderComponent;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CartPage extends BasePage {

    private final HeaderComponent header;

    @FindBy(css = "div.container-fluid.cart-info table tbody tr")
    private List<WebElement> cartItemRows;

    @FindBy(id = "cart_update")
    private WebElement updateButton;

    public CartPage(WebDriver driver, WebDriverWait waiter) {
        super(driver, waiter);
        this.header = new HeaderComponent(driver, waiter);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
    }

    @Step("Получение списка товаров в корзине")
    public List<CartItem> getCartItems() {
        waiter.until(ExpectedConditions.visibilityOfAllElements(cartItemRows));
        List<CartItem> items = new ArrayList<>();

        for (WebElement row : cartItemRows) {
            if (row.findElements(By.cssSelector("th")).size() > 0) {
                continue;
            }

            try {
                items.add(parseCartItem(row));
            } catch (Exception ignored) {
            }
        }
        return items;
    }

    @Step("Обновление количества товара '{item.name}' до {newQuantity}")
    public CartPage updateQuantity(CartItem item, int newQuantity) {
        item.getQuantityInput().clear();
        item.getQuantityInput().sendKeys(String.valueOf(newQuantity));
        return this;
    }

    @Step("Нажатие кнопки Update")
    public CartPage clickUpdate() {
        waiter.until(ExpectedConditions.elementToBeClickable(updateButton));
        updateButton.click();
        waiter.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div.container-fluid.cart-info")));
        return new CartPage(driver, waiter);
    }

    @Step("Удаление товара '{item.name}' из корзины")
    public CartPage removeItem(CartItem item) {
        waiter.until(ExpectedConditions.elementToBeClickable(item.getRemoveButton()));
        item.getRemoveButton().click();
        waiter.until(ExpectedConditions.stalenessOf(item.getRowElement()));
        waiter.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.container-fluid.cart-info table")));
        return new CartPage(driver, waiter);
    }

    @Step("Удаление товаров на чётных позициях")
    public CartPage removeEvenPositionItems() {
        List<CartItem> items = getCartItems();

        for (int i = items.size() - 1; i >= 0; i--) {
            if ((i + 1) % 2 == 0) {
                items = getCartItems();
                if (i < items.size()) {
                    removeItem(items.get(i));
                }
            }
        }

        return new CartPage(driver, waiter);
    }

    @Step("Получение итоговой суммы корзины без учета доставки")
    public double getSubTotal() {
        waiter.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#totals_table")));

        try {
            List<WebElement> rows = driver.findElements(By.cssSelector("table#totals_table tr"));
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.cssSelector("td"));
                if (cells.size() >= 2) {
                    String title = cells.get(0).getText().trim().toLowerCase();
                    if (title.contains("sub-total") || title.contains("sub total")) {
                        return PriceHelper.parsePrice(cells.get(1).getText());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse cart item: " + e.getMessage());
        }

        return getCartItems().stream().mapToDouble(item -> item.getTotalPrice()).sum();
    }

    @Step("Поиск самого дешёвого товара в корзине")
    public CartItem findCheapestItem() {
        return getCartItems().stream()
                .min(Comparator.comparingDouble(CartItem::getUnitPrice))
                .orElse(null);
    }

    @Step("Получение количества товаров в корзине")
    public int getItemCount() {
        return getCartItems().size();
    }

    private CartItem parseCartItem(WebElement row) {
        CartItem item = new CartItem();
        item.setName(row.findElement(By.cssSelector("td:nth-child(2) a")).getText().trim());
        item.setUnitPrice(PriceHelper.parsePrice(row.findElement(By.cssSelector("td:nth-child(4)")).getText()));
        item.setQuantityInput(row.findElement(By.cssSelector("td:nth-child(5) input")));
        item.setQuantity(Integer.parseInt(item.getQuantityInput().getAttribute("value")));
        item.setTotalPrice(PriceHelper.parsePrice(row.findElement(By.cssSelector("td:nth-child(6)")).getText()));
        item.setRemoveButton(row.findElement(By.cssSelector("td:nth-child(7) a")));
        item.setRowElement(row);
        return item;
    }

    @Step("Получение товаров на нечётных позициях")
    public List<CartItem> getOddPositionItems() {
        List<CartItem> allItems = getCartItems();
        List<CartItem> oddItems = new ArrayList<>();

        for (int i = 0; i < allItems.size(); i++) {
            if ((i + 1) % 2 != 0) {
                oddItems.add(allItems.get(i));
            }
        }

        return oddItems;
    }
}
