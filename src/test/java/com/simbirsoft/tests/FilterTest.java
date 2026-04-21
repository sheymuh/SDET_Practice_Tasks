package com.simbirsoft.tests;

import com.simbirsoft.helpers.AssertionHelper;
import com.simbirsoft.pages.CategoryPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

@Feature("Фильтрация в категориях")
@Execution(ExecutionMode.CONCURRENT)
public class FilterTest extends BaseTest {

    private CategoryPage categoryPage;

    @BeforeEach
    public void navigateToCategory() {
        driver.get("https://automationteststore.com/index.php?rt=product/category&path=49_51");
        categoryPage = new CategoryPage(driver, waiter);
        Assertions.assertTrue(categoryPage.getProductCount() >= 4, "Category should have at least 4 products");
    }

    @Test
    @Description("TC-1.1: Сортировка товаров по имени (A-Z)")
    public void testSortByNameAscending() {
        categoryPage.sortBy("Name A - Z");
        AssertionHelper.assertSortedByNameAsc(categoryPage.getProductNames());
        takeScreenshot("sort-by-name-asc");
    }

    @Test
    @Description("TC-1.2: Сортировка товаров по имени (Z-A)")
    public void testSortByNameDescending() {
        categoryPage.sortBy("Name Z - A");
        AssertionHelper.assertSortedByNameDesc(categoryPage.getProductNames());
        takeScreenshot("sort-by-name-desc");
    }

    @Test
    @Description("TC-1.3: Сортировка товаров по цене (возрастание)")
    public void testSortByPriceAscending() {
        categoryPage.sortBy("Price Low > High");
        AssertionHelper.assertSortedByPriceAsc(categoryPage.getProductPrices());
        takeScreenshot("sort-by-price-asc");
    }

    @Test
    @Description("TC-1.4: Сортировка товаров по цене (убывание)")
    public void testSortByPriceDescending() {
        categoryPage.sortBy("Price High > Low");
        AssertionHelper.assertSortedByPriceDesc(categoryPage.getProductPrices());
        takeScreenshot("sort-by-price-desc");
    }
}
