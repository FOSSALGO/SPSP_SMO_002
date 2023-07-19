package test;

import java.util.Arrays;
import smo.Individu;
import spsp.Employee;
import spsp.Task;

public class Test002 {

    public static void main(String[] args) {
        //SKILL=================================================================
        String[] skill = {
            "Programming Expertise",//s0
            "Leadership",//s1
            "Database Expertise",//s2
            "UML Expertise",//s3
            "Web Design Expertise",//s4            
        };

        //EMPLOYEES=============================================================
        Employee[] employees = new Employee[5];

        int[] e0_skills = {0, 3};
        double e0_maxDedication = 1.0;
        double e0_salary = 2000;
        Employee e0 = new Employee("e0", e0_skills, e0_maxDedication, e0_salary);
        employees[0] = e0;

        int[] e1_skills = {1, 2, 3};
        double e1_maxDedication = 1.0;
        double e1_salary = 2500;
        Employee e1 = new Employee("e1", e1_skills, e1_maxDedication, e1_salary);
        employees[1] = e1;

        int[] e2_skills = {4};
        double e2_maxDedication = 0.5;
        double e2_salary = 1700;
        Employee e2 = new Employee("e2", e2_skills, e2_maxDedication, e2_salary);
        employees[2] = e2;

        int[] e3_skills = {0, 1};
        double e3_maxDedication = 1.0;
        double e3_salary = 3000;
        Employee e3 = new Employee("e3", e3_skills, e3_maxDedication, e3_salary);
        employees[3] = e3;

        int[] e4_skills = {3, 4};
        double e4_maxDedication = 1.2;
        double e4_salary = 2200;
        Employee e4 = new Employee("e4", e4_skills, e4_maxDedication, e4_salary);
        employees[4] = e4;

        System.out.println(Arrays.toString(employees));

        //TASK==================================================================
        Task[] tasks = new Task[7];

        int[] t0_skills = {3};
        double t0_effort = 1.0;//5.0;
        Task t0 = new Task("t0", t0_skills, t0_effort);
        tasks[0] = t0;

        int[] t1_skills = {0, 2};
        double t1_effort = 20.0;
        Task t1 = new Task("t1", t1_skills, t1_effort);
        tasks[1] = t1;

        int[] t2_skills = {0, 3};
        double t2_effort = 50.0;
        Task t2 = new Task("t2", t2_skills, t2_effort);
        tasks[2] = t2;

        int[] t3_skills = {4};
        double t3_effort = 10.0;
        Task t3 = new Task("t3", t3_skills, t3_effort);
        tasks[3] = t3;

        int[] t4_skills = {1};
        double t4_effort = 50.0;
        Task t4 = new Task("t4", t4_skills, t4_effort);
        tasks[4] = t4;

        int[] t5_skills = {3};
        double t5_effort = 15.0;
        Task t5 = new Task("t5", t5_skills, t5_effort);
        tasks[5] = t5;

        int[] t6_skills = {0};
        double t6_effort = 10.0;
        Task t6 = new Task("t6", t6_skills, t6_effort);
        tasks[6] = t6;

        System.out.println(Arrays.toString(tasks));

        //TASK PRECEDENCE GRAPH ===============================================
        int[][] taskPrecedenceGraph = {
            {},//t0
            {0},//t1
            {0, 1},//t2
            {0},//t3
            {2},//t4
            {1, 3},//t5
            {3, 4},//t6
        };

        //bikin beberapa individu
        Individu sm_0 = new Individu(employees, tasks, taskPrecedenceGraph);
        Individu sm_1 = new Individu(employees, tasks, taskPrecedenceGraph);
        Individu sm_2 = new Individu(employees, tasks, taskPrecedenceGraph);
        Individu sm_3 = new Individu(employees, tasks, taskPrecedenceGraph);
        Individu sm_4 = new Individu(employees, tasks, taskPrecedenceGraph);
        Individu sm_5 = new Individu(employees, tasks, taskPrecedenceGraph);

        Individu[] sm = {sm_0, sm_1, sm_2, sm_3, sm_4, sm_5};

        for (int i = 0; i < sm.length; i++) {
            sm[i].generateDedicationMatrix();
            sm[i].calculate();
        }

        //cetak
        for (int i = 0; i < sm.length; i++) {
            System.out.println("----------------------------------------------------");
            sm[i].printDedicationMatrix();
        }

        //coba interaksi
        //antara sm[2] dan sm[4]
        int[][] mask = sm[2].interaction(sm[4]);
        System.out.println("----------------------------------------------------");
        System.out.println("HASIL INTERAKSI SM2 dan SM4");
        for (int i = 0; i < mask.length; i++) {
            System.out.println(Arrays.toString(mask[i]));
        }
       
    }
}
