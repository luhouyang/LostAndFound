package com.example.lostfound;

public class UserData {
    String name;
    String organization;
    String matrixNo;
    String identification;
    int credits;

    public UserData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getMatrixNo() {
        return matrixNo;
    }

    public void setMatrixNo(String matrixNo) {
        this.matrixNo = matrixNo;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
