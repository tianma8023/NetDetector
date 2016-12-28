package com.tianma.netdetector.lib;

/**
 * 网络类型
 * @author Tianma at 2016/12/28
 */
public enum NetworkType {
    /**
     * WiFi
     */
    NETWORK_WIFI("WiFi"),
    /**
     * 4G
     */
    NETWORK_4G("4G"),
    /**
     * 3G
     */
    NETWORK_3G("3G"),
    /**
     * 2G
     */
    NETWORK_2G("2G"),
    /**
     * Unknown
     */
    NETWORK_UNKNOWN("Unknown"),
    /**
     * No network
     */
    NETWORK_NO("No network");

    private String desc;
    NetworkType(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
