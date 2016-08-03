package com.example;

public enum PersonType {

    FOO("foo"), BAR("bar");

    private String anotherName;

    PersonType(String anotherName) {
        this.anotherName = anotherName;
    }


    @Override
    public String toString() {
        return anotherName;
    }
}
