package utils.listener;

import org.testng.IExecutionListener;

public class AnotherExecutionListenerTest implements IExecutionListener {
    @Override
    public void onExecutionStart() {
        System.out.println("Lets see if this will print out");
    }

    @Override
    public void onExecutionFinish() {
        System.out.println("Lets see if this will print out");
    }
}
