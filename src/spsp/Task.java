package spsp;

import java.util.Arrays;

public class Task {
    private String name;
    private int[] skills;//skill yang dibutuhkan untuk mengerjakan task ini
    private double effort;

    public Task(String name, int[] skills, double effort) {
        this.name = name;
        this.skills = skills;
        this.effort = effort;
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

    public double getEffort() {
        return effort;
    }

    public void setEffort(double effort) {
        this.effort = effort;
    }

    @Override
    public String toString() {
        return "Task{" + "name=" + name + ", skills=" + Arrays.toString(skills) + ", effort=" + effort + '}';
    }    
    
}
