package com.cardiff.comm.devops;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebDriverSelenium {

    @Value("${local.server.port}")
    private int port;

    private WebDriver webDriver;
    private WebDriverWait wait;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    void setupTest() {
        FirefoxOptions options = new FirefoxOptions();

        options.addArguments("--headless");

        webDriver = new FirefoxDriver(options);
        webDriver.manage().window().maximize();

        wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    }

    @AfterEach
    void teardown() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private void pause(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void typeInto(String fieldName, String value) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(fieldName))).clear();
        webDriver.findElement(By.name(fieldName)).sendKeys(value);
    }

    private void clickSubmit() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//form//button[@type='submit'] | //form//input[@type='submit'] | //form//button")
        )).click();
    }

    private void loginAsOrganiser() {
        webDriver.get(url("/organiser_login"));

        typeInto("organiser_username", "Org1");
        typeInto("organiser_access_code", "Org12345");

        clickSubmit();
        pause(2);
    }

    private void loginAsAdmin() {
        webDriver.get(url("/admin_login"));

        typeInto("organiser_username", "admin1");
        typeInto("organiser_access_code", "admin12345");

        clickSubmit();
        pause(2);
    }

    private void updateOrganisation(String title, String description, String address) {
        typeInto("title", title);
        typeInto("description", description);
        typeInto("address", address);

        clickSubmit();
        pause(2);
    }

    // ---------------- ORGANISER TESTS ----------------

    @Test
    public void organiserCanLogin() {
        loginAsOrganiser();

        assertFalse(webDriver.getPageSource().isEmpty());
        assertTrue(
                webDriver.getCurrentUrl().contains("/main")
                        || webDriver.getPageSource().contains("Organiser")
        );
    }

    @Test
    public void organiserCanNavigateToEditOrganisationPage() {
        loginAsOrganiser();

        webDriver.get(url("/organisation/edit/1"));
        pause(2);

        assertTrue(webDriver.getCurrentUrl().contains("/organisation/edit/1"));
        assertFalse(webDriver.getPageSource().isEmpty());
    }

    @Test
    public void organiserCanUpdateOrganisationData() {
        loginAsOrganiser();

        webDriver.get(url("/organisation/edit/1"));

        updateOrganisation(
                "Organiser Updated Organisation",
                "This organisation was updated by organiser Selenium test.",
                "Cardiff"
        );

        assertTrue(
                webDriver.getCurrentUrl().contains("/organisation/view/1")
                        || webDriver.getCurrentUrl().contains("/organisation/edit/1")
        );
    }

    @Test
    public void organiserCanSeeUpdatedOrganisationData() {
        loginAsOrganiser();

        webDriver.get(url("/organisation/edit/1"));

        updateOrganisation(
                "Organiser Check Update",
                "Checking that organiser update was saved correctly.",
                "Cardiff"
        );

        webDriver.get(url("/organisation/edit/1"));
        pause(2);

        assertTrue(webDriver.getPageSource().contains("Organiser Check Update"));
        assertTrue(webDriver.getPageSource().contains("Checking that organiser update was saved correctly."));
        assertTrue(webDriver.getPageSource().contains("Cardiff"));
    }

    @Test
    public void organiserCannotSubmitEmptyOrganisationFields() {
        loginAsOrganiser();

        webDriver.get(url("/organisation/edit/1"));

        typeInto("title", "");
        typeInto("description", "");
        typeInto("address", "");

        clickSubmit();
        pause(2);

        assertTrue(webDriver.getCurrentUrl().contains("/organisation/edit/1"));

        assertTrue(
                webDriver.getPageSource().contains("Organiser Check Update")
                        || webDriver.getPageSource().contains("Checking that organiser update was saved correctly.")
                        || webDriver.getPageSource().contains("Cardiff")
        );
    }

    @Test
    public void organiserCanRemoveSocialLinksSuccessfully() {
        loginAsOrganiser();

        webDriver.get(url("/organisation/edit/1"));

        if (!webDriver.findElements(By.name("instagram")).isEmpty()) {
            webDriver.findElement(By.name("instagram")).clear();
        }

        if (!webDriver.findElements(By.name("facebook")).isEmpty()) {
            webDriver.findElement(By.name("facebook")).clear();
        }

        clickSubmit();
        pause(2);

        assertTrue(
                webDriver.getCurrentUrl().contains("/organisation/view/1")
                        || webDriver.getCurrentUrl().contains("/organisation/edit/1")
        );

        assertFalse(webDriver.getPageSource().contains("Instagram"));
        assertFalse(webDriver.getPageSource().contains("Facebook"));
    }

    // ---------------- ADMIN TESTS ----------------

    @Test
    public void adminCanLogin() {
        loginAsAdmin();

        assertFalse(webDriver.getPageSource().isEmpty());
        assertTrue(
                webDriver.getCurrentUrl().contains("/main")
                        || webDriver.getPageSource().contains("Admin")
        );
    }

    @Test
    public void adminCanNavigateToEditOrganisationPage() {
        loginAsAdmin();

        webDriver.get(url("/organisation/edit/2"));
        pause(2);

        assertTrue(webDriver.getCurrentUrl().contains("/organisation/edit/2"));
        assertFalse(webDriver.getPageSource().isEmpty());
    }

    @Test
    public void adminCanUpdateOrganisationData() {
        loginAsAdmin();

        webDriver.get(url("/organisation/edit/2"));

        updateOrganisation(
                "Admin Updated Organisation",
                "This organisation was updated by admin Selenium test.",
                "Cardiff"
        );

        assertTrue(
                webDriver.getCurrentUrl().contains("/organisation/view/2")
                        || webDriver.getCurrentUrl().contains("/organisation/edit/2")
        );
    }

    @Test
    public void adminCanSeeUpdatedOrganisationData() {
        loginAsAdmin();

        webDriver.get(url("/organisation/edit/2"));

        updateOrganisation(
                "Admin Check Update",
                "Checking that admin update was saved correctly.",
                "Cardiff"
        );

        webDriver.get(url("/organisation/edit/2"));
        pause(2);

        assertTrue(webDriver.getPageSource().contains("Admin Check Update"));
        assertTrue(webDriver.getPageSource().contains("Checking that admin update was saved correctly."));
        assertTrue(webDriver.getPageSource().contains("Cardiff"));
    }

    @Test
    public void adminCannotSubmitEmptyOrganisationFields() {
        loginAsAdmin();

        webDriver.get(url("/organisation/edit/2"));

        typeInto("title", "");
        typeInto("description", "");
        typeInto("address", "");

        clickSubmit();
        pause(2);

        assertTrue(webDriver.getCurrentUrl().contains("/organisation/edit/2"));

        assertTrue(
                webDriver.getPageSource().contains("Admin Check Update")
                        || webDriver.getPageSource().contains("Checking that admin update was saved correctly.")
                        || webDriver.getPageSource().contains("Cardiff")
        );
    }

    @Test
    public void adminCanRemoveSocialLinksSuccessfully() {
        loginAsAdmin();

        webDriver.get(url("/organisation/edit/2"));

        if (!webDriver.findElements(By.name("instagram")).isEmpty()) {
            webDriver.findElement(By.name("instagram")).clear();
        }

        if (!webDriver.findElements(By.name("facebook")).isEmpty()) {
            webDriver.findElement(By.name("facebook")).clear();
        }

        clickSubmit();
        pause(2);

        assertTrue(
                webDriver.getCurrentUrl().contains("/organisation/view/2")
                        || webDriver.getCurrentUrl().contains("/organisation/edit/2")
        );

        assertFalse(webDriver.getPageSource().contains("Instagram"));
        assertFalse(webDriver.getPageSource().contains("Facebook"));
    }
}