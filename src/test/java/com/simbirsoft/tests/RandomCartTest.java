package com.simbirsoft.tests;

import com.simbirsoft.pages.CartPage;
import com.simbirsoft.pages.HomePage;
import com.simbirsoft.pages.ProductPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Feature("Проверка корзины")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class RandomCartTest extends BaseTest {

    private CartPage cartPage;
    private static final int PRODUCTS_TO_ADD = 3;

    @Test
    @Order(1)
    @Description("TC-3.1: Добавление случайных товаров")
    public void testAddRandomProducts() {
        List<WebElement> products = homePage.getFeaturedProducts();
        int productCount = products.size();

        Assertions.assertTrue(productCount >= PRODUCTS_TO_ADD,
                "Home page should have at least " + PRODUCTS_TO_ADD + " products, found: " + productCount);

        List<Integer> allIndices = IntStream.range(0, productCount).boxed().collect(Collectors.toList());
        Collections.shuffle(allIndices);
        List<Integer> selectedIndices = allIndices.subList(0, PRODUCTS_TO_ADD);

        for (int index : selectedIndices) {
            int quantity = ThreadLocalRandom.current().nextInt(1, 4);
            ProductPage productPage = homePage.clickProductByIndex(index);
            productPage.setQuantity(quantity).addToCart();

            returnToHome();
            homePage = new HomePage(driver, waiter);
            sleep(1500);
        }

        driver.get("https://automationteststore.com/index.php?rt=checkout/cart");
        cartPage = new CartPage(driver, waiter);

        Assertions.assertEquals(PRODUCTS_TO_ADD, cartPage.getItemCount());
        takeScreenshot("cart-with-items");
    }

    @Test
    @Order(2)
    @Description("TC-3.2: Удаление чётных товаров")
    public void testRemoveEvenPositionItems() {
        driver.get("https://automationteststore.com/");
        homePage = new HomePage(driver, waiter);
        sleep(2000);

        addProductsDirectly();

        driver.get("https://automationteststore.com/index.php?rt=checkout/cart");
        cartPage = new CartPage(driver, waiter);

        List<CartPage.CartItem> items = cartPage.getCartItems();
        int initialCount = items.size();

        // Удаляем товары с конца для избежания сдвига индексов
        // Удаляем 3-й товар (индекс 2)
        if (items.size() >= 3) {
            cartPage = cartPage.removeItem(items.get(2));
            sleep(1500);
        }

        // Обновляем список и удаляем 1-й товар (индекс 0)
        items = cartPage.getCartItems();
        if (items.size() >= 1) {
            cartPage = cartPage.removeItem(items.get(0));
            sleep(1500);
        }

        // После удаления 2 товаров из 3 должен остаться 1
        int expectedCount = initialCount - 2;
        Assertions.assertEquals(expectedCount, cartPage.getItemCount(),
                "Should have " + expectedCount + " items left");

        takeScreenshot("cart-after-removing");
    }

    @Test
    @Order(3)
    @Description("TC-3.3: Проверка итоговой стоимости")
    public void testTotalAfterRemovingItems() {
        driver.get("https://automationteststore.com/");
        homePage = new HomePage(driver, waiter);
        sleep(2000);

        addProductsDirectly();

        driver.get("https://automationteststore.com/index.php?rt=checkout/cart");
        cartPage = new CartPage(driver, waiter);

        List<CartPage.CartItem> items = cartPage.getCartItems();
        double sumBeforeRemoval = items.stream().mapToDouble(item -> item.totalPrice).sum();

        while (items.size() > 1) {
            cartPage = cartPage.removeItem(items.get(items.size() - 1));
            sleep(1500);
            items = cartPage.getCartItems();
        }

        Assertions.assertEquals(1, cartPage.getItemCount());

        double itemTotal = items.get(0).totalPrice;
        double displayedTotal = cartPage.getTotal();
        double difference = Math.abs(itemTotal - displayedTotal);

        Assertions.assertTrue(difference <= 2.0,
                "Total difference should be small. Item: " + itemTotal + ", Total: " + displayedTotal);

        takeScreenshot("cart-final-total");
    }

    private void addProductsDirectly() {
        List<WebElement> products = homePage.getFeaturedProducts();
        int productCount = products.size();

        List<Integer> allIndices = IntStream.range(0, productCount).boxed().collect(Collectors.toList());
        Collections.shuffle(allIndices);
        List<Integer> selectedIndices = allIndices.subList(0, PRODUCTS_TO_ADD);

        for (int index : selectedIndices) {
            int quantity = ThreadLocalRandom.current().nextInt(1, 3);
            ProductPage productPage = homePage.clickProductByIndex(index);
            productPage.setQuantity(quantity).addToCart();

            returnToHome();
            homePage = new HomePage(driver, waiter);
            sleep(1500);
        }
    }

    private void returnToHome() {
        try {
            driver.findElement(By.cssSelector("a.logo")).click();
        } catch (Exception e) {
            driver.get("https://automationteststore.com/");
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
