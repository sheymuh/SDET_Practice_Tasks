package com.simbirsoft.tests;

import com.simbirsoft.helpers.AssertionHelper;
import com.simbirsoft.pages.*;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.ThreadLocalRandom;

@Feature("Проверка поисковой выдачи и корзины")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class SearchAndCartTest extends BaseTest {

    private SearchResultPage searchPage;
    private CartPage cartPage;
    private int quantity2;
    private int quantity3;

    @Test
    @Order(1)
    @Description("TC-2.1: Поиск товара 'shirt' и сортировка результатов")
    public void testSearchAndSort() {
        driver.get("https://automationteststore.com/index.php?rt=product/search&keyword=shirt&sort=pd.name-ASC");
        searchPage = new SearchResultPage(driver, waiter);

        int productCount = searchPage.getProductCount();
        Assertions.assertTrue(productCount > 0, "No products found for 'shirt'");

        AssertionHelper.assertSortedByNameAsc(searchPage.getProductNames());
        takeScreenshot("search-results-sorted");
    }

    @Test
    @Order(2)
    @Description("TC-2.2: Добавление 2-го и 3-го товара в корзину")
    public void testAddSecondAndThirdProducts() {
        driver.get("https://automationteststore.com/index.php?rt=product/search&keyword=shirt&sort=pd.name-ASC");
        searchPage = new SearchResultPage(driver, waiter);

        int productCount = searchPage.getProductCount();
        Assertions.assertTrue(productCount >= 3, "Search should return at least 3 products, found: " + productCount);

        quantity2 = ThreadLocalRandom.current().nextInt(1, 11);
        ProductPage productPage2 = searchPage.clickProductByIndex(1);
        productPage2.setQuantity(quantity2).addToCart();

        driver.get("https://automationteststore.com/index.php?rt=product/search&keyword=shirt&sort=pd.name-ASC");
        searchPage = new SearchResultPage(driver, waiter);

        quantity3 = ThreadLocalRandom.current().nextInt(1, 11);
        ProductPage productPage3 = searchPage.clickProductByIndex(2);
        productPage3.setQuantity(quantity3).addToCart();

        takeScreenshot("products-added-to-cart");
    }

    @Test
    @Order(3)
    @Description("TC-2.3: Удвоение количества самого дешёвого товара")
    public void testDoubleCheapestItemQuantity() {
        driver.get("https://automationteststore.com/index.php?rt=product/search&keyword=shirt&sort=pd.name-ASC");
        searchPage = new SearchResultPage(driver, waiter);
        Assertions.assertTrue(searchPage.getProductCount() >= 3, "Not enough products");

        quantity2 = ThreadLocalRandom.current().nextInt(1, 11);
        ProductPage productPage2 = searchPage.clickProductByIndex(1);
        productPage2.setQuantity(quantity2).addToCart();

        driver.get("https://automationteststore.com/index.php?rt=product/search&keyword=shirt&sort=pd.name-ASC");
        searchPage = new SearchResultPage(driver, waiter);

        quantity3 = ThreadLocalRandom.current().nextInt(1, 11);
        ProductPage productPage3 = searchPage.clickProductByIndex(2);
        productPage3.setQuantity(quantity3).addToCart();

        cartPage = productPage3.getHeader().goToCart();

        double oldTotal = cartPage.getTotal();
        CartPage.CartItem cheapestItem = cartPage.findCheapestItem();
        Assertions.assertNotNull(cheapestItem, "Cheapest item should exist");

        int oldQuantity = cheapestItem.quantity;
        double cheapestPrice = cheapestItem.unitPrice;

        cartPage.updateQuantity(cheapestItem, oldQuantity * 2).clickUpdate();

        double newTotal = cartPage.getTotal();
        double expectedTotal = oldTotal + (cheapestPrice * oldQuantity);

        Assertions.assertEquals(expectedTotal, newTotal, 0.01);
        takeScreenshot("cart-after-doubling");
    }
}
