package utils.listeners;

import static utils.LogUtil.info;

import org.testng.IExecutionListener;

public class TestNGExecutionListener implements IExecutionListener {

    @Override
    public void onExecutionStart() {
        info("TestNG is staring the execution");
    }

    @Override
    public void onExecutionFinish() {
        info("TestNG has finished, the execution");

    }
}
