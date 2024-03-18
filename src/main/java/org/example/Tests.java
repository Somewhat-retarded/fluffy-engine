import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.time.Duration;
import java.util.*;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Tests{
    public static String email;
    public static String password;
    public static WebDriver driver;
    public static JavascriptExecutor js;
    public static WebDriverWait wait;

    //1 Atidaryti tinklalapi
    public static void initNewDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
        driver = new FirefoxDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        driver.get("https://demowebshop.tricentis.com/");
        js = (JavascriptExecutor) driver;
//        wait = new WebDriverWait(driver, Time);
    }
    public static List<String> readNames(String fileName) {
        List<String> productNames = new ArrayList<>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(fileName));

            String line;
            while ((line = reader.readLine()) != null) {
                productNames.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return productNames;
    }

    public static String getRandomString(int length) {
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder builder = new StringBuilder();
        Random rnd = new Random();
        while (builder.length() < length) {
            int index = (int) (rnd.nextFloat() * CHARS.length());
            builder.append(CHARS.charAt(index));
        }

        return builder.toString();
    }

    public static void test(String testFileName) {
        //2 Spausti Login
        WebElement logInBtn = driver.findElement(By.className("ico-login"));
        logInBtn.click();

        //3 Uzpildyti email password ir spausti login
        WebElement emailInput = driver.findElement(By.id("Email"));
        emailInput.sendKeys(email);

        WebElement passwordInput = driver.findElement(By.id("Password"));
        passwordInput.sendKeys(password);

        WebElement loginBtn = driver.findElement(By.className("login-button"));
        loginBtn.click();

        //4 Paspausti DigitalDownloads
        WebElement digitalDownloadsLink = driver.findElement(By.linkText("Digital downloads"));
        digitalDownloadsLink.click();

        //5 Prideti i krepseli nuskaitytas prekes
        List<String> productNames = readNames(testFileName);
        for (String productName : productNames) {
            WebElement productElement = driver.findElement(By.xpath(String.format("//h2[@class = 'product-title']/a[text() = '%s']/../../div[@class='add-info']/div[@class='buttons']/input", productName)));
            productElement.click();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //6 Atsidaryti shopping cart
        WebElement shoppingCartLink = driver.findElement(By.className("ico-cart"));
        shoppingCartLink.click();

        //7 Paspausti i agree ir checkout
        WebElement agreeBox = driver.findElement(By.id("termsofservice"));
        agreeBox.click();

        WebElement checkoutButton = driver.findElement(By.id("checkout"));
        checkoutButton.click();

        //8 Billing address pasirinkimas ir continue
        try {
            driver.findElement(By.id("billing-address-select"));
        } catch (NoSuchElementException e) {
            Select select = new Select(driver.findElement(By.id("BillingNewAddress_CountryId")));
            select.selectByVisibleText("Lithuania");
            driver.findElement(By.id("BillingNewAddress_City")).sendKeys("test");
            driver.findElement(By.id("BillingNewAddress_Address1")).sendKeys("test");
            driver.findElement(By.id("BillingNewAddress_ZipPostalCode")).sendKeys("12345");
            driver.findElement(By.id("BillingNewAddress_PhoneNumber")).sendKeys("1234567890");
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        driver.findElement(By.className("new-address-next-step-button")).click();
        //9 Payment methods continue
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        driver.findElement(By.className("payment-method-next-step-button")).click();
        //10 Payment information continue
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        driver.findElement(By.className("payment-info-next-step-button")).click();
        //11 Confirm order continue
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        driver.findElement(By.className("confirm-order-next-step-button")).click();

        //12 Isitikinti kad uzsakymas uzskaitytas
        driver.findElement(By.linkText("Click here for order details.")).click();
        WebElement orderStatus = driver.findElement(By.xpath("//div[@class = 'order-details']/span[2]"));
        assertEquals(orderStatus.getText(), "Order Status: Pending");
    }


    @BeforeClass
    public static void setup() {
        System.setProperty("webdriver.gecko.driver", "src/geckodriver.exe");
        initNewDriver();

        email = getRandomString(8) + "@gmail.com";
        password = "password123";

        WebElement loginBtn = driver.findElement(By.className("ico-login"));
        loginBtn.click();

        //3 Register new customer skiltyje
        WebElement regBtn = driver.findElement(By.xpath("//input[contains(@class, \"register-button\")]"));
        regBtn.click();

        //4 registracijos formos laukai
        WebElement genderMaleInput = driver.findElement(By.id("gender-male"));
        genderMaleInput.click();

        WebElement firstNameInput = driver.findElement(By.id("FirstName"));
        firstNameInput.sendKeys("FirstName");

        WebElement lastNameInput = driver.findElement(By.id("LastName"));
        lastNameInput.sendKeys("LastName");

        WebElement emailInput = driver.findElement(By.id("Email"));
        emailInput.sendKeys(email);

        WebElement passwordInput = driver.findElement(By.id("Password"));
        passwordInput.sendKeys(password);

        WebElement confirmPasswordInput = driver.findElement(By.id("ConfirmPassword"));
        confirmPasswordInput.sendKeys(password);

        //5 spausti register
        WebElement registerBtn = driver.findElement(By.id("register-button"));
        registerBtn.click();

        //6 spausti continue
        WebElement registerContinueBtn = driver.findElement(By.className("register-continue-button"));
        registerContinueBtn.click();

        driver.quit();
    }

    @Before
    public void resetDriver() {
        initNewDriver();
    }

    @Test
    public void Test1() {
        test("src/data1.txt");
    }

    @Test
    public void Test2() {
        test("src/data2.txt");
    }

    @After
    public void clearDriver() {
        driver.quit();
    }
}
