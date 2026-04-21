package com.simbirsoft.pages;

import com.simbirsoft.helpers.PriceHelper;
import com.simbirsoft.pages.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
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

    public HeaderComponent getHeader() {
        return header;
    }

    public List<CartItem> getCartItems() {
        waiter.until(ExpectedConditions.visibilityOfAllElements(cartItemRows));
        List<CartItem> items = new ArrayList<>();

        for (WebElement row : cartItemRows) {
            if (row.findElements(By.cssSelector("th")).size() > 0) {
                continue;
            }

            try {
                CartItem item = new CartItem();
                item.name = row.findElement(By.cssSelector("td:nth-child(2) a")).getText().trim();
                item.unitPrice = PriceHelper.parsePrice(row.findElement(By.cssSelector("td:nth-child(4)")).getText());
                item.quantityInput = row.findElement(By.cssSelector("td:nth-child(5) input"));
                item.quantity = Integer.parseInt(item.quantityInput.getAttribute("value"));
                item.totalPrice = PriceHelper.parsePrice(row.findElement(By.cssSelector("td:nth-child(6)")).getText());
                item.removeButton = row.findElement(By.cssSelector("td:nth-child(7) a"));
                item.rowElement = row;
                items.add(item);
            } catch (Exception ignored) {
            }
        }
        return items;
    }

    public CartPage updateQuantity(CartItem item, int newQuantity) {
        item.quantityInput.clear();
        item.quantityInput.sendKeys(String.valueOf(newQuantity));
        return this;
    }

    public CartPage clickUpdate() {
        waiter.until(ExpectedConditions.elementToBeClickable(updateButton));
        updateButton.click();
        sleep(2000);
        return new CartPage(driver, waiter);
    }

    public CartPage removeItem(CartItem item) {
        waiter.until(ExpectedConditions.elementToBeClickable(item.removeButton));
        item.removeButton.click();
        sleep(1500);
        return new CartPage(driver, waiter);
    }

    public double getTotal() {
        sleep(1000);

        try {
            List<WebElement> rows = driver.findElements(By.cssSelector("table#totals_table tr"));
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.cssSelector("td"));
                if (cells.size() >= 2) {
                    String title = cells.get(0).getText().trim().toLowerCase();
                    if (title.contains("total") && !title.contains("sub")) {
                        return PriceHelper.parsePrice(cells.get(1).getText());
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return getCartItems().stream().mapToDouble(item -> item.totalPrice).sum();
    }

    public CartItem findCheapestItem() {
        return getCartItems().stream()
                .min((a, b) -> Double.compare(a.unitPrice, b.unitPrice))
                .orElse(null);
    }

    public int getItemCount() {
        return getCartItems().size();
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static class CartItem {
        public String name;
        public double unitPrice;
        public int quantity;
        public double totalPrice;
        public WebElement removeButton;
        public WebElement quantityInput;
        public WebElement rowElement;
    }
}
