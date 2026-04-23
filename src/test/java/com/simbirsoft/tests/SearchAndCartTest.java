package com.simbirsoft.tests;

import com.simbirsoft.helpers.AssertionHelper;
import com.simbirsoft.pages.CartPage;
import com.simbirsoft.pages.ProductPage;
import com.simbirsoft.pages.SearchResultPage;
import com.simbirsoft.pages.components.CartItem;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.ThreadLocalRandom;

@Feature("Проверка поисковой выдачи и корзины")
@Execution(ExecutionMode.CONCURRENT)
public class SearchAndCartTest extends BaseTest {

    private SearchResultPage searchPage;
    private CartPage cartPage;

    @BeforeEach
    public void setUp() {
        super.setUp();
        searchPage = homePage.getHeader().searchForAndSubmit("shirt");
    }

    @Test
    @Description("TC-2.1: Поиск товара 'shirt' и сортировка результатов")
    public void testSearchAndSort() {
        int productCount = searchPage.getProductCount();
        Assertions.assertTrue(productCount > 0, "No products found for 'shirt'");

        searchPage.sortByNameAsc();
        AssertionHelper.assertSortedByNameAsc(searchPage.getProductNames());
    }

    @Test
    @Description("TC-2.2: Добавление 2-го и 3-го товара в корзину со случайным количеством")
    public void testAddSecondAndThirdProducts() {
        int productCount = searchPage.getProductCount();
        Assertions.assertTrue(productCount >= 3,
                "Search should return at least 3 products, found: " + productCount);

        searchPage.sortByNameAsc();

        int quantity2 = ThreadLocalRandom.current().nextInt(1, 11);
        ProductPage productPage2 = searchPage.clickProductByIndex(1);
        productPage2.setQuantity(quantity2);
        cartPage = productPage2.addToCart();

        searchPage = homePage.getHeader().searchForAndSubmit("shirt");

        int quantity3 = ThreadLocalRandom.current().nextInt(1, 11);
        ProductPage productPage3 = searchPage.clickProductByIndex(2);
        productPage3.setQuantity(quantity3);
        cartPage = productPage3.addToCart();

        Assertions.assertEquals(2, cartPage.getItemCount(),
                "Cart should contain 2 items");
    }

    @Test
    @Description("TC-2.3: Удвоение количества самого дешёвого товара и проверка итоговой суммы")
    public void testDoubleCheapestItemQuantity() {
        Assertions.assertTrue(searchPage.getProductCount() >= 3, "Not enough products");

        searchPage.sortByNameAsc();

        int quantity2 = ThreadLocalRandom.current().nextInt(1, 11);
        ProductPage productPage2 = searchPage.clickProductByIndex(1);
        productPage2.setQuantity(quantity2);
        cartPage = productPage2.addToCart();

        searchPage = homePage.getHeader().searchForAndSubmit("shirt");

        int quantity3 = ThreadLocalRandom.current().nextInt(1, 11);
        ProductPage productPage3 = searchPage.clickProductByIndex(2);
        productPage3.setQuantity(quantity3);
        cartPage = productPage3.addToCart();

        double oldTotal = cartPage.getSubTotal();
        CartItem cheapestItem = cartPage.findCheapestItem();
        Assertions.assertNotNull(cheapestItem, "Cheapest item should exist");

        int oldQuantity = cheapestItem.getQuantity();
        double cheapestPrice = cheapestItem.getUnitPrice();

        cartPage.updateQuantity(cheapestItem, oldQuantity * 2)
                .clickUpdate();

        double newTotal = cartPage.getSubTotal();
        double expectedTotal = oldTotal + (cheapestPrice * oldQuantity);

        Assertions.assertEquals(expectedTotal, newTotal, 0.01,
                String.format("Total should be %.2f after doubling quantity", expectedTotal));
    }
}
