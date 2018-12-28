package utils.listeners;

import static utils.LogUtil.info;

import java.util.ArrayList;
import java.util.List;

import org.testng.IExecutionListener;
import org.testng.annotations.AfterClass;

public class TestNGExecutionListener implements IExecutionListener {
    private static String customerName = "";
    private static List<String> customerNames = new ArrayList<>();



    @Override
    public void onExecutionStart() {
        info("Testing Query manager");
//        customerName = "Customer " + PageUtil.getFormattedDateForTesting();
//        try {
//            new CustomerQueryManager().createCustomer(customerName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        customerNames.add(customerName);
    }

    @Override
    @AfterClass(alwaysRun = true)
    public void onExecutionFinish() {
        info("Testing Clean up Query Manager");

    }
}
