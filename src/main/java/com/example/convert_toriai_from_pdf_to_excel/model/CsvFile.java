package com.example.convert_toriai_from_pdf_to_excel.model;

public class CsvFile {
    private String name;

    public CsvFile(String name) {
        this.name = name;
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
