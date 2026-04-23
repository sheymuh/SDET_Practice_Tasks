package com.simbirsoft.tests;

import com.simbirsoft.pages.CartPage;
import com.simbirsoft.pages.components.CartItem;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

@Feature("Проверка корзины")
@Execution(ExecutionMode.CONCURRENT)
public class RandomCartTest extends BaseTest {

    private CartPage cartPage;
    private static final int PRODUCTS_TO_ADD = 5;

    @BeforeEach
    public void setUp() {
        super.setUp();
        homePage.addRandomProductsToCart(PRODUCTS_TO_ADD, 1, 10);
        cartPage = homePage.getHeader().goToCart();
    }

    @Test
    @Description("TC-3.1: Добавление 5 случайных товаров со случайным количеством")
    public void testAddRandomProducts() {
        Assertions.assertEquals(PRODUCTS_TO_ADD, cartPage.getItemCount());
    }

    @Test
    @Description("TC-3.2: Удаление всех чётных по порядку товаров из корзины")
    public void testRemoveEvenPositionItems() {
        int initialCount = cartPage.getItemCount();

        cartPage = cartPage.removeEvenPositionItems();

        int expectedRemaining = initialCount - (initialCount / 2);
        Assertions.assertEquals(expectedRemaining, cartPage.getItemCount(),
                "Should have " + expectedRemaining + " items left after removing even positions");
    }

    @Test
    @Description("TC-3.3: Проверка итоговой стоимости после удаления чётных товаров")
    public void testTotalAfterRemovingEvenItems() {
        List<CartItem> oddItemsBeforeRemoval = cartPage.getOddPositionItems();

        double expectedTotal = oddItemsBeforeRemoval.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        cartPage = cartPage.removeEvenPositionItems();

        double actualTotal = cartPage.getSubTotal();

        Assertions.assertEquals(expectedTotal, actualTotal, 0.01,
                String.format("Total after removing even items should be %.2f, but was %.2f",
                        expectedTotal, actualTotal));
    }
}
