package com.uxcasuals.uxcasuals_waves.models;

/**
 * Created by Dhakchianandan on 24/09/15.
 */
public class Station {
    private String name;
    private String url;
    private String logo;

    public Station() {
    }

    public Station(String name, String url, String logo) {
        this.name = name;
        this.url = url;
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
