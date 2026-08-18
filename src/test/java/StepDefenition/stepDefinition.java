package StepDefenition;

import java.io.FileOutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class stepDefinition {
    WebDriver driver;

    @Given("I launch the IRCTC application")
    public void i_launch_the_irctc_application() {
        driver = new ChromeDriver();
        driver.get("https://www.irctc.co.in/nget/train-search");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until((ExpectedCondition<Boolean>) wd ->
            ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
        System.out.println("Page Verified: IRCTC opened successfully.");
    }

    @When("I handle the initial dialog if present")
    public void i_handle_the_initial_dialog_if_present() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            WebElement okBtn = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//button[normalize-space()='OK' or normalize-space()='Accept']")));
            okBtn.click();
        } catch (TimeoutException te) {
            System.out.println("No initial OK/Accept dialog found.");
        }
    }

    @And("I enter {string} as the From station")
    public void i_enter_as_the_from_station(String fromStation) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement fromInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-controls='pr_id_1_list']")));
        fromInput.sendKeys(fromStation);
    }

    @And("I select {string} from the suggestions")
    public void i_select_from_the_suggestions(String suggestion) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement fromOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(.,'" + suggestion + "')]")));
        fromOption.click();
        Thread.sleep(1000);
    }

    @When("I enter {string} as the To station")
    public void i_enter_as_the_to_station(String toStation) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement toInput = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//input[@placeholder='To' and @role='searchbox'] | //input[@aria-controls='pr_id_2_list']")));
        toInput.sendKeys(toStation);
        Thread.sleep(1000);
    }

    @And("I select journey date as {int} days from today")
    public void i_select_journey_date_as_days_from_today(int days) throws InterruptedException {
        LocalDate targetDate = LocalDate.now().plusDays(days);
        String formattedDate = targetDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"jDate\"]/span/input")));
        dateInput.click();
        dateInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        dateInput.sendKeys(formattedDate);
        dateInput.sendKeys(Keys.TAB);
        Thread.sleep(1000);
    }

    @And("I choose {string} class")
    public void i_choose_class(String travelClass) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.findElement(By.xpath("//p-dropdown[@id='journeyClass']")).click();
        WebElement classOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(.,'" + travelClass + "')]")));
        classOption.click();
        Thread.sleep(1000);
    }

    @And("I check the Person With Disability Concession option")
    public void i_check_the_person_with_disability_concession_option() throws InterruptedException {
        WebElement pwdCheckbox = driver.findElement(By.xpath("//label[text()='Person With Disability Concession']"));
        pwdCheckbox.click();
        Thread.sleep(1000);
    }

    @And("I confirm the PWD popup")
    public void i_confirm_the_pwd_popup() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//p-confirmdialog//button/span[text()='OK']")));
        okButton.click();
        Thread.sleep(1000);
    }

    @And("I click on search trains")
    public void i_click_on_search_trains() {
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    @Then("I should see the list of available trains")
    public void i_should_see_the_list_of_available_trains() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("train-heading")));
        List<WebElement> trainNames = driver.findElements(By.className("train-heading"));
        System.out.println("Total Trains Found: " + trainNames.size());
        for (WebElement train : trainNames) {
            System.out.println("Train: " + train.getText());
        }
    }

    @And("I capture a full page screenshot of the results")
    public void i_capture_a_full_page_screenshot_of_the_results() {
        ChromiumDriver chromium = (ChromiumDriver) driver;
        Map<String, Object> screenshotParams = Map.of("captureBeyondViewport", true, "fromSurface", true);
        String base64Screenshot = (String) chromium.executeCdpCommand("Page.captureScreenshot", screenshotParams).get("data");
        byte[] decodedScreenshot = Base64.getDecoder().decode(base64Screenshot);

        try (FileOutputStream fos = new FileOutputStream("./IRCTC_Full_Results_" + System.currentTimeMillis() + ".png")) {
            fos.write(decodedScreenshot);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
