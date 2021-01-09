package com.mattLearn.model;

public class Student {

    private int age;
    private String name;
    private int grade;
    private boolean isMale;

    public Student(int age, String name, int grade, boolean isMale) {
        this.age = age;
        this.name = name;
        this.grade = grade;
        this.isMale = isMale;
    }

    public Student(String name) {
        this.name = name;
        this.age = 0;
        this.grade = 0;
        this.isMale = true;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }
}
