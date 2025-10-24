package com.ssafy.alrebaba.language.domain;

public enum LanguageName {

    Python("파이썬"),
    JavaScript("자바스크립트"),
    Java("자바"),
    C("C언어"),
    C_PlusPlus("C++"),
    C_Sharp("C샵"),
    TypeScript("TypeScript"),
    PHP("PHP"),
    Ruby("Ruby"),
    Swift("Swift"),
    kotlin("Kotlin"),
    Go("Go"),
    Rust("Rust"),
    SQL("SQL");

    private String key;

    LanguageName(String key){
        this.key = key;
    }

    public String getKey() {
        return this.key;

    }

}

