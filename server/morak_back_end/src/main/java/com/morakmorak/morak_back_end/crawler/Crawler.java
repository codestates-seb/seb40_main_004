package com.morakmorak.morak_back_end.crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public interface Crawler {
    void run();
    default boolean switchTapTo(int tab, WebDriver webDriver) {
        List<String> tabs = new ArrayList<>(webDriver.getWindowHandles());

        if (tabs.size() <= tab) {
            return false;
        }

        webDriver.switchTo().window(tabs.get(tab));
        return true;
    }

    default void clickBtnOnce(String location, WebDriver webDriver, WebDriverWait webDriverWait) {
            List<WebElement> categoryAcceptBtn = selectElementsAndWaitFindByCssSelector(location,webDriver, webDriverWait);
            try {
                categoryAcceptBtn.get(0).click();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
    }

    default List<WebElement> selectElementsAndWaitFindByCssSelector(String location, WebDriver webDriver, WebDriverWait webDriverWait) {
        waitFindByCssSelector(location, webDriverWait);
        return selectElementsByCssSelector(location, webDriver);
    }

    default WebElement selectElementAndWaitFindByCssSelector(String location, WebDriver webDriver, WebDriverWait webDriverWait) {
        waitFindByCssSelector(location, webDriverWait);
        return selectElementByCssSelector(location, webDriver);
    }

    default void waitFindByCssSelector(String location, WebDriverWait webDriverWait) {
        webDriverWait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(location))
        );
    }

    default List<WebElement> selectElementsByCssSelector(String location, WebDriver webDriver) {
        return webDriver.findElements(By.cssSelector(location));
    }

    default WebElement selectElementByCssSelector(String location, WebDriver webDriver) {
        return webDriver.findElement(By.cssSelector(location));
    }
}
