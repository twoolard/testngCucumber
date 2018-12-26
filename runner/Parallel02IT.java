import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(
        strict = true,
        features = {"/Users/twoolard/IdeaProjects/testngCucumber/src/test/resources/features/test.feature"},
        plugin = {"json:/Users/twoolard/IdeaProjects/testngCucumber/target/cucumber.json/2.json"},
        monochrome = false,
        tags = {},
        glue = {"com.company.stepdefs"})
public class Parallel02IT extends AbstractTestNGCucumberTests {
}
