package com.practice.alarmmanagerservice;

public class Base {
    private static Base mInstance = null;

    public String check= "0";
    public void setCheck(String check1) {
        check = check1;
    }
    public String getCheck() {
        return check;
    }


    public String usageInstagram= "0";
    public void setUsageInstagram(String usageInstagram1) {
        usageInstagram = usageInstagram1;
    }
    public String getUsageInstagram() {
        return usageInstagram;
    }

    public String usageNetflix= "0";
    public void setUsageNetflix(String usageNetflix1) {
        usageNetflix = usageNetflix1;
    }
    public String getUsageNetflix() {
        return usageNetflix;
    }

    protected Base() {
    }

    public static synchronized Base getInstance() {
        if (null == mInstance) {
            mInstance = new Base();
        }
        return mInstance;
    }
}
