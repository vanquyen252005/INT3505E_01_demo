package com.example.model;

public class StudentResult {
    private float gpa;
    private int drl;
    private String rank;

    public StudentResult() {} // cần cho SOAP

    public StudentResult(float gpa, int drl, String rank) {
        this.gpa = gpa;
        this.drl = drl;
        this.rank = rank;
    }

    public float getGpa() {
        return gpa;
    }

    public void setGpa(float gpa) {
        this.gpa = gpa;
    }

    public int getDrl() {
        return drl;
    }

    public void setDrl(int drl) {
        this.drl = drl;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
