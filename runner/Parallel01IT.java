import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(
        strict = true,
        features = {"/Users/twoolard/IdeaProjects/testngCucumber/src/test/resources/features/CommentText.feature"},
        plugin = {"json:/Users/twoolard/IdeaProjects/testngCucumber/target/cucumber.json/1.json"},
        monochrome = false,
        tags = {},
        glue = {"com.company.steps"})
public class Parallel01IT extends AbstractTestNGCucumberTests {
}
