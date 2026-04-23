package com.simbirsoft.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait waiter;
    private Actions actions;

    public BasePage(WebDriver driver, WebDriverWait waiter) {
        this.driver = driver;
        this.waiter = waiter;
    }

    protected Actions initActions() {
        if (actions == null) {
            actions = new Actions(driver);
        }
        return actions;
    }
}
