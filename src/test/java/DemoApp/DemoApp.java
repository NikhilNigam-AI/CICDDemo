package DemoApp;


import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;
import com.applitools.eyes.visualgrid.model.IosDeviceName;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.google.common.base.Strings.isNullOrEmpty;


@RunWith(JUnit4.class)
public class DemoApp {
    private EyesRunner runner;
    private Eyes eyes;
    private static BatchInfo batch;
    private WebDriver driver;
    private static JavascriptExecutor js;
    private static boolean flag = true;

    // URLs to test
    static final String url1 ="https://www.iag.com.au/";
    static final String url2 ="https://www.iag.com.au/about-us/who-we-are/purpose-and-strategy";
    static final String url3 ="https://www.iag.com.au/shareholder-centre/share-price-information#share_history";

    @BeforeClass
    public static void setBatch() {
        // Must be before ALL tests (at Class-level)
        batch = new BatchInfo("IAG Demo");

        String batchId = System.getenv("APPLITOOLS_BATCH_ID");
        if (batchId != null) {
            batch.setId(batchId);
            System.out.println("Applitools Batch ID is " + batchId);
        }

    }

    @Before
    public void beforeEach() {
        // Initialize the Runner for your test.
        //runner = new ClassicRunner();
        runner = new VisualGridRunner(10);

        Configuration sconf = new Configuration();
        sconf.setIgnoreDisplacements(true);
        sconf.setIgnoreCaret(true);
        sconf.setMatchTimeout(10000);
        sconf.setStitchMode(StitchMode.CSS);
        sconf.setMatchLevel(MatchLevel.STRICT);
        sconf.setWaitBeforeScreenshots(2000);
        sconf.setBatch(batch);
        sconf.addBrowser(2560, 1440, BrowserType.CHROME);
        sconf.addBrowser(1366, 768, BrowserType.CHROME_TWO_VERSIONS_BACK);
        sconf.addBrowser(1200, 800, BrowserType.CHROME_ONE_VERSION_BACK);
        sconf.addBrowser(1024, 768, BrowserType.SAFARI);
        sconf.addBrowser(1366, 768, BrowserType.EDGE_CHROMIUM);
        sconf.addBrowser(1024, 768, BrowserType.FIREFOX);
        sconf.addDeviceEmulation(DeviceName.Pixel_4);
        sconf.addDeviceEmulation(DeviceName.OnePlus_7T_Pro);
        sconf.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_XR));
        sconf.addBrowser(new IosDeviceInfo(IosDeviceName.iPad_Air_2));
        sconf.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_11_Pro_Max));
        sconf.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_12));

        // Initialize the eyes SDK
        eyes = new Eyes(runner);
        eyes.setLogHandler(new FileLogger("/Users/nikhil/Documents/demos/Java/logs/CustomerApp.log",true,true));

        // Raise an error if no API Key has been found.
        if(isNullOrEmpty(System.getenv("APPLITOOLS_API_KEY"))) {
            throw new RuntimeException("No API Key found; Please set environment variable 'APPLITOOLS_API_KEY'.");
        }
        else
        {
            System.out.println("Applitools key is " + System.getenv("APPLITOOLS_API_KEY"));
        }

        // Set your personal Applitols API Key from your environment variables.
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

        // set configuration
        eyes.setConfiguration(sconf);

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--headless");

        // Use Chrome browser
        driver = new ChromeDriver(options);
        js = (JavascriptExecutor)driver;
    }

    @Test
    public void IAGApp_Test() throws Exception {

        try {

            String testName = "IAG Pages";

            eyes.open(driver, "IAG1", testName, new RectangleSize(1200, 800));

            // Navigate to https://www.iag.com.au/
            navigateToPage(url1);

            // Visual checkpoint
            eyes.check("Home",Target.window().fully());

            // navigate to https://www.iag.com.au/about-us/who-we-are/purpose-and-strategy
            navigateToPage(url2);

            // Visual checkpoint
            eyes.check("Purpose", Target.window().content().fully());

            // Navigate to https://www.iag.com.au/shareholder-centre/share-price-information#share_history
            navigateToPage(url3);

            // Visual checkpoint
            eyes.check("Share Price", Target.window().layout().fully());

            // End the test.
            eyes.closeAsync();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    @After
    public void afterEach() {
        // Close the browser.
        driver.quit();

        // If the test was aborted before eyes.close was called, ends the test as
        // aborted.
        eyes.abortIfNotClosed();

        // Wait and collect all test results
        TestResultsSummary allTestResults = runner.getAllTestResultsImpl(false);
        // Print results
        System.out.println(allTestResults);
    }

    private void navigateToPage(String url)
    {
        driver.get(url);
        switch(url)
        {
            case url1:
                // Induce Error 1 -  Remove logo via Java script
                BreakSite(flag, js, "content","document.querySelector(\"#prices > li:nth-child(1) > a > span.price\")", new String[] {"$4.80"});
                BreakSite(flag, js, "hide","document.querySelector('#md-megamenu-6 > div > ul > li:nth-child(2) > a > span')", null);
                BreakSite(flag, js, "bg","document.querySelector(\"#page > section > div\")", new String[] {"Green"});
                break;
            case url2:
                // Induce Error 2- Remove logo and change background color of text box
                BreakSite(flag, js, "content","document.querySelector(\"#node-2606 > div > div.field.field-name-field-news-intro-paragraph.field-type-text-long.field-label-hidden > div > div\")", new String[] {"IAG’s purpose means that whether you are a customer, partner, employee. Applitools content change in between. IAG believes its purpose will enable it to become a more sustainable business over the long term and deliver stronger and more consistent returns for its shareholders."});
                BreakSite(flag, js, "hide", "document.querySelector(\"#node-2606 > div > div.field.field-name-body.field-type-text-with-summary.field-label-hidden > div > div > ul:nth-child(12) > li:nth-child(1)\")", null);
                BreakSite(flag, js, "hide", "document.querySelector('#page > section > div > div.social > ul > li.rrssb-linkedin')", null);
                break;
            case url3:
                BreakSite(flag, js, "bg", "document.querySelector('#share_data > ul > li:nth-child(1) > h4')", new String[] {"red"});
                BreakSite(flag, js, "hide", "document.querySelector(\"#share_calc > form > select\")", null);
                break;
            default:
                break;
        }
    }

    public static void printConsoleOutput( String tname, Eyes obj)
    {
        System.out.println("Executing test ["+tname+"] with configurations:");
        System.out.println(obj.getConfiguration().toString());

    }

    private static void BreakSite(Boolean flag, JavascriptExecutor js,String changeType, String domElement, String[] args )
    {

        if(flag) {
            switch (changeType) {
                case "css":
                    js.executeScript(domElement +".setAttribute('style','position:relative; bottom:30px; font-size:20px; border: 3px solid #73AD21');");
                    //js.executeScript("document.querySelector(\"html\").style.height='auto'");
                    break;
                case "css-position":
                    js.executeScript(domElement+ ".setAttribute('style','position:relative; bottom:40px; border: 1px solid red');");
                    break;
                case "css-position2":
                    js.executeScript(domElement+ ".setAttribute('style','position:relative; bottom:10px; border: 2px solid red; background:green');");
                    break;
                case "css-font":
                    js.executeScript(domElement+ ".setAttribute('style','font-size: 30px');");
                    break;
                case "css-border":
                    js.executeScript(domElement+ ".setAttribute('style','border: 3px solid #73AD21');");
                    break;
                case "bg":
                    if(args != null) {
                        js.executeScript(domElement + ".style.backgroundColor='"+ args[0] +"';");
                    }
                    else
                    {
                        js.executeScript(domElement + ".style.backgroundColor='blue';");
                    }

                    break;

                case "hide":
                    js.executeScript(domElement+ ".style.visibility='hidden';");
                    break;


                case "remove":
                    js.executeScript(domElement+ ".remove();");
                    break;

                case "table":
                    js.executeScript(domElement + ".deleteRow(1);");
                    break;

                case "content":
                    try {
                        if (args != null) {
                            js.executeScript(domElement + ".innerHTML='" + args[0] + "'");
                        } else {
                            js.executeScript(domElement + ".innerHTML='Something'");
                        }
                    }
                    catch (Exception e) {
                        js.executeScript(domElement + ".innerText='Alt Text'");
                    }

                    break;
                default:
                    //js.executeScript("arguments[0].setAttribute("innerHtml",;", selectedElement);
                    break;

            }
        }

    }
}