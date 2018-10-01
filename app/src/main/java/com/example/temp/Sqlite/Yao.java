package com.example.temp.Sqlite;

public class Yao {
    private Long id;
    private String value;
    private String true_time;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTrue_time() {
        return true_time;
    }

    public void setTrue_time(String true_time) {
        this.true_time = true_time;
    }

    public Yao(Long id, String value, String true_time) {
        super();
        this.id = id;
        this.value = value;
        this.true_time = true_time;

    }
}