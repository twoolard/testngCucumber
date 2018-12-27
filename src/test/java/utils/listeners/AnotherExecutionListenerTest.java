package utils.listeners;

import static utils.LogUtil.info;

import org.testng.IExecutionListener;

public class AnotherExecutionListenerTest implements IExecutionListener {
    @Override
    public void onExecutionStart() {
        info("Lets see if this will print out");
    }

    @Override
    public void onExecutionFinish() {
        info("Lets see if this will print out");
    }
}
