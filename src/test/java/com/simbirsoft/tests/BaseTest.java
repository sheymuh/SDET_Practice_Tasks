package com.simbirsoft.tests;

import com.simbirsoft.helpers.DriverManager;
import com.simbirsoft.helpers.ParameterProvider;
import com.simbirsoft.pages.HomePage;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.ByteArrayInputStream;
import java.time.Duration;

public abstract class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait waiter;
    protected HomePage homePage;

    @BeforeEach
    public void setUp() {
        driver = DriverManager.getDriver();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(60));

        waiter = new WebDriverWait(driver,
                Duration.ofSeconds(Long.parseLong(ParameterProvider.get("explicit.wait.time"))));

        driver.get(ParameterProvider.get("base.url"));
        homePage = new HomePage(driver, waiter);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            takeScreenshot("test-finished");
            DriverManager.quitDriver();
        }
    }

    protected void takeScreenshot(String name) {
        try {
            Allure.addAttachment(name,
                    new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)));
        } catch (Exception ignored) {
        }
    }
}
