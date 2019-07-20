package ru.integrations.testRail;

public class ITestRailPojo {
    private ITestRail testRail;
    private int countError;

    public ITestRail getTestRail() {
        return testRail;
    }

    public void setTestRail(ITestRail testRail) {
        this.testRail = testRail;
    }

    public int getCountError() {
        return countError;
    }

    public void setCountError(int countError) {
        this.countError = countError;
    }
}
