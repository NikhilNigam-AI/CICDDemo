package DemoApp;


import com.applitools.eyes.*;
import com.applitools.eyes.selenium.*;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;
import com.applitools.eyes.visualgrid.model.IosDeviceName;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Runs Applitools test for the demo app https://demo.applitools.com
 */
@RunWith(JUnit4.class)
public class DemoApp {
    private EyesRunner runner;
    private Eyes eyes;
    private static BatchInfo batch;
    private WebDriver driver;
    private String url1 ="https://demo.applitools.com";
    //private String url1 = "https://demo.applitools.com/index_v2.html";
    private String url2 ="https://demo.applitools.com/app.html";
    //private String url2 = "https://demo.applitools.com/app_v2.html";

    @BeforeClass
    public static void setBatch() {
        // Must be before ALL tests (at Class-level)
        String batchName = System.getenv("APPLITOOLS_BATCH_NAME");
        
        if(batchName == null){
            batchName = "Demo Batch";
        }    
        batch = new BatchInfo(batchName);
        
        
        String batchId = System.getenv("APPLITOOLS_BATCH_ID");
        if(batchId != null) {
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
        eyes = new Eyes();
        eyes.setLogHandler(new FileLogger("./DemoApp.log",true,true));

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
    }

    @Test
    public void DemoApp_Diff_Test_Strict() throws Exception {
        try {
            var flag = false;
            var tName = "Basic Diff Test (STRICT mode)";
            JavascriptExecutor js = (JavascriptExecutor)driver;

            //System.setProperty("webdriver.chrome.driver","/Users/Nikhil/Documents/chromedriver/v81/chromedriver.exe");
            // Set AUT's name, test name and viewport size (width X height)
            // We have set it to 800 x 600 to accommodate various screens. Feel free to
            // change it.
            eyes.open(driver, "Demo App", tName, new RectangleSize(1200, 800));

            //Check point
            printConsoleOutput(tName,eyes);

            // Navigate the browser to the  app.
            driver.get(url1);

            // To see visual bugs after the first run, use the commented line below instead.
            //driver.get("https://demo.applitools.com/index_v2.html");

            // Induce Error 1 -  Remove logo via Java script
            BreakSite(flag, js, "remove","document.getElementsByClassName(\"btn btn-primary\")[0]");

            // Visual checkpoint #1 - Check the login page.
            // eyes.checkWindow("Home Page");
            eyes.check("Login Page",Target.window().fully());

            // navigate to catalogue page
            driver.get(url2);

            // Induce Error 2- Remove logo and change background color of text box
            //if(flag)
            //js.executeScript("document.getElementById(\"ext-input-2\").style.backgroundColor='red';");
            BreakSite(flag, js, "remove","document.getElementsByClassName(\"element-box-tp\")[0]");

            // Visual checkpoint #2 - Check the app page.
            eyes.check("Main Page", Target.window().fully());

            // End the test.
            eyes.closeAsync();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    @Test
    public void DemoApp_Diff_Test_Layout() throws Exception {
        try {
            var flag = false;
            var tName = "Basic Diff Test (LAYOUT mode)";
            JavascriptExecutor js = (JavascriptExecutor)driver;

            //System.setProperty("webdriver.chrome.driver","/Users/Nikhil/Documents/chromedriver/v81/chromedriver.exe");
            // Set AUT's name, test name and viewport size (width X height)
            // We have set it to 800 x 600 to accommodate various screens. Feel free to
            // change it.
            eyes.open(driver, "Demo App", tName, new RectangleSize(1200, 800));

            //Check point
            printConsoleOutput(tName,eyes);

            // navigate to catalogue page
            driver.get("https://demo.applitools.com/app.html");

            // Induce Error - Remove logo and change background color of text box
            BreakSite(flag, js, "css-font","document.getElementsByClassName(\"element-box-tp\")[0]");

            // Visual checkpoint #2 - Check the app page.
            eyes.check("Main Page", Target.window().layout().fully());

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

    public static void printConsoleOutput( String tname, Eyes obj)
    {
        System.out.println("Executing test ["+tname+"] with configurations:");
        System.out.println(obj.getConfiguration().toString());

    }

    private static void BreakSite(Boolean flag, JavascriptExecutor js,String changeType, WebElement selectedElement ) {

        if(flag) {
            switch (changeType) {
                case "css":
                    js.executeScript("arguments[0].setAttribute('style','color: red')", selectedElement);
                    js.executeScript("arguments[0].setAttribute('style','fill: green')", selectedElement);
                    js.executeScript("arguments[0].setAttribute('style','font-size: 8px')", selectedElement);
                    break;

                case "remove":
                    js.executeScript("arguments[0].remove();", selectedElement);
                    break;

                case "content":
                default:
                    //js.executeScript("arguments[0].setAttribute("innerHtml",;", selectedElement);
                    break;

            }
        }

    }

    private static void BreakSite(Boolean flag, JavascriptExecutor js,String changeType, String domElement ) {

        if(flag) {
            switch (changeType) {
                case "css":
                    js.executeScript(domElement +".setAttribute('style','position:relative; bottom:30px; font-size:20px; border: 3px solid #73AD21');");
                    break;
                case "css-position":
                    js.executeScript(domElement+ ".setAttribute('style','position:relative; bottom:40px; border: 1px solid red');");
                    break;
                case "css-font":
                    js.executeScript(domElement+ ".setAttribute('style','font-size: 25px');");
                    break;
                case "css-border":
                    js.executeScript(domElement+ ".setAttribute('style','border: 3px solid #73AD21');");
                    break;
                case "bg":
                    js.executeScript(domElement +"style.backgroundColor='red';");
                    break;

                case "remove":
                    js.executeScript(domElement+ ".remove();");
                    break;

                case "table":
                    js.executeScript(domElement + ".deleteRow(1);");
                    break;

                case "content":
                default:
                    //js.executeScript("arguments[0].setAttribute("innerHtml",;", selectedElement);
                    break;

            }
        }

    }

}
