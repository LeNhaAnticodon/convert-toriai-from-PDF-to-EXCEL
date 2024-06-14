package com.example.convert_toriai_from_pdf_to_excel.model;

public class CsvFile {
    private String name;

    private String kouSyuName;

    public String getKouSyuName() {
        return kouSyuName;
    }

    public void setKouSyuName(String kouSyuName) {
        this.kouSyuName = kouSyuName;
    }

    public CsvFile(String name, String kouSyuName) {
        this.name = name;
        this.kouSyuName = kouSyuName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
