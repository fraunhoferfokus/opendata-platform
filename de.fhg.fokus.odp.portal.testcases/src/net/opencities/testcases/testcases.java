package net.opencities.testcases;


import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class testcases {
	private WebDriver driver;
	private String baseUrlBar;
	private String baseUrlBer;
	private String baseUrlAms;
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		baseUrlBar = "http://barcelona.data.opencities.net/";
		baseUrlAms = "http://amsterdam.data.opencities.net/";
		baseUrlBer = "http://berlin.data.opencities.net/";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testCasesBar() throws Exception {
		driver.get(baseUrlBar + "/en_GB/web/guest/home");
		driver.findElement(By.cssSelector("li.selected > a > span")).click();
		driver.findElement(By.xpath("//nav[@id='navigation']/ul/li[2]/a/span")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
		driver.findElement(By.linkText("Male population distribution by age")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
	}
	
	@Test
	public void testCasesAms() throws Exception {
		driver.get(baseUrlAms + "/en_GB/web/guest/home");
		driver.findElement(By.cssSelector("li.selected > a > span")).click();
		driver.findElement(By.xpath("//nav[@id='navigation']/ul/li[2]/a/span")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
		driver.findElement(By.linkText("Locations and employment in the tourism sector")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
	}
	
	@Test
	public void testCasesBer() throws Exception {
		driver.get(baseUrlBer + "/en_GB/web/guest/home");
		driver.findElement(By.cssSelector("li.selected > a > span")).click();
		driver.findElement(By.xpath("//nav[@id='navigation']/ul/li[2]/a/span")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
		driver.findElement(By.linkText("Anzahl arbeitsloser Frauen und Männer in Berlin")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
	}



	@After
	public void tearDown() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
}