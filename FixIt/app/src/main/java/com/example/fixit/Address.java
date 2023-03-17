package com.example.fixit;

public class Address
{
    private String houseAddress, townName;
    private int postalCode;

    public Address() {
    }

    public Address(String houseAddress, String townName, int postalCode) {
        this.houseAddress = houseAddress;
        this.townName = townName;
        this.postalCode = postalCode;
    }

    public String getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }
}
