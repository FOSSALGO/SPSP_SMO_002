package spsp;

import java.util.Arrays;

public class Employee {

    private String name;
    private int[] skills;//skill yang dimiliki oleh employee
    private double maxDedication;
    private double salary;

    public Employee(String name, int[] skills, double maxDedication, double salary) {
        this.name = name;
        this.skills = skills;
        this.maxDedication = maxDedication;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getSkills() {
        return skills;
    }

    public void setSkills(int[] skills) {
        this.skills = skills;
    }

    public double getMaxDedication() {
        return maxDedication;
    }

    public void setMaxDedication(double maxDedication) {
        this.maxDedication = maxDedication;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee{" + "name=" + name + ", skills=" + Arrays.toString(skills) + ", maxDedication=" + maxDedication + ", salary=" + salary + '}';
    }
}
