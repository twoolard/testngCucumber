package com.company.runners;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(features="src/test/resources/features", glue="com.company.steps", plugin= {"pretty", "html:target/cucumber-reports", "json:target/Cucumber.json", "rerun:target/re-run.txt"}, monochrome = true)
public class RunAllTest extends AbstractTestNGCucumberTests {
}
