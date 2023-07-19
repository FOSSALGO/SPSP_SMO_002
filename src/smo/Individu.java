package smo;

import java.util.Arrays;
import java.util.Random;
import spsp.Employee;
import spsp.Task;
import spsp.TaskVertex;

public class Individu {

    //INPUT=====================================================================
    private Employee[] employees = null;
    private Task[] tasks = null;
    private int[][] taskPrecedenceGraph = null;
    //VARIABEL PROSES
    public int numEmployees = 0;
    public int numTasks = 0;
    public double[][] dedicationMatrix = null;//X[i][j]
    public int[][] connectionMatrix = null;//digunakan untuk menyatakan ada atau tidak irisan skill antara employee dan task
    public double[] taskDuration = null;
    public double projectCost = Double.MAX_VALUE;
    public double projectDuration = Double.MAX_VALUE;
    public TaskVertex[] taskVertex = null;
    public double fitness = 0;
    public double wCost = 0.5;
    public double wDuration = 0.5;

    public Individu(Employee[] employees, Task[] tasks, int[][] taskPrecedenceGraph) {
        this.employees = employees;
        this.tasks = tasks;
        this.taskPrecedenceGraph = taskPrecedenceGraph;
        this.numEmployees = employees.length;
        this.numTasks = tasks.length;
        this.dedicationMatrix = null;
    }

    public double[][] generateDedicationMatrix() {
        dedicationMatrix = null;// this.dedicationMatrix
        if (employees != null && tasks != null) {
            numEmployees = employees.length;
            numTasks = tasks.length;
            dedicationMatrix = new double[numEmployees][numTasks];//Xij
            connectionMatrix = new int[numEmployees][numTasks];
            for (int i = 0; i < numEmployees; i++) {
                for (int j = 0; j < numTasks; j++) {
                    double xij = 0;
                    connectionMatrix[i][j] = 0;
                    if (isConnected(employees[i].getSkills(), tasks[j].getSkills())) {
                        connectionMatrix[i][j] = 1;
                        //double max = Math.min(employees[i].getMaxDedication(), tasks[j].getEffort());
                        double max = employees[i].getMaxDedication();
                        double value = Algorithm.randomBetweenDouble(0, max);
                        xij = Math.round(value * 100.0) / 100.0;
                    }
                    dedicationMatrix[i][j] = xij;
                }
            }//end of for-i
        }//
        return dedicationMatrix;
    }

    private boolean isConnected(int[] eSkills, int[] tSkills) {
        boolean result = false;
        loop_tSkill:
        for (int i = 0; i < eSkills.length; i++) {
            for (int j = 0; j < tSkills.length; j++) {
                if (eSkills[i] == tSkills[j]) {
                    result = true;
                    break loop_tSkill;
                }
            }
        }
        return result;
    }

    public double[] calculateTasksDuration() {
        taskDuration = null;
        if (//validasi
                employees != null
                && tasks != null
                && dedicationMatrix != null
                && dedicationMatrix.length == employees.length
                && dedicationMatrix[0].length == tasks.length) {

            taskDuration = new double[numTasks];
            //hitung penyebut
            for (int j = 0; j < numTasks; j++) {
                double sigmaXij = 0;
                for (int i = 0; i < numEmployees; i++) {
                    sigmaXij += dedicationMatrix[i][j];
                }
                double durationTj = tasks[j].getEffort() / sigmaXij;
                taskDuration[j] = durationTj;
            }
        }
        return taskDuration;
    }

    public double calculateProjectCost() {
        projectCost = Double.MAX_VALUE;
        if (//validasi
                taskDuration != null
                && dedicationMatrix != null
                && employees != null
                && tasks != null) {
            projectCost = 0;
            for (int i = 0; i < numEmployees; i++) {
                double salaryEi = employees[i].getSalary();
                for (int j = 0; j < numTasks; j++) {
                    if (dedicationMatrix[i][j] > 0) {
                        double taskDuration_j = taskDuration[j];
                        double xij = dedicationMatrix[i][j];
                        projectCost += (salaryEi * xij * taskDuration_j);
                    }
                }
            }
        }
        return projectCost;
    }

    public double calculateProjectDuration() {
        projectDuration = Double.MAX_VALUE;
        taskVertex = null;
        if (//validasi
                taskDuration != null
                && taskPrecedenceGraph != null
                && dedicationMatrix != null
                && employees != null
                && tasks != null) {
            projectDuration = 0;
            numEmployees = employees.length;
            numTasks = tasks.length;

            //inisialisasi task vertex
            taskVertex = new TaskVertex[numTasks];
            for (int i = 0; i < taskVertex.length; i++) {
                String name = "taskVertex-" + i;
                TaskVertex vertex = new TaskVertex(name, taskDuration[i]);
                taskVertex[i] = vertex;
            }

            //set precedence for all taskVertex
            for (int i = 0; i < taskVertex.length; i++) {
                if (taskPrecedenceGraph[i].length > 0) {
                    for (int j = 0; j < taskPrecedenceGraph[i].length; j++) {
                        int v = taskPrecedenceGraph[i][j];
                        if (v >= 0) {
                            taskVertex[i].addLeft(taskVertex[v]);//set left node of node[i]
                            taskVertex[v].addRight(taskVertex[i]);//set right node of node[v]
                        }
                    }
                }
            }

            //set start time untuk semua vertex yang tidak bersyarat (tidak memiliki precedence)
            for (int i = 0; i < taskVertex.length; i++) {
                if (taskVertex[i].leftTaskVertex == null) {
                    taskVertex[i].updateStartTime(0);
                }
            }

            //hitung durasi project dengan cara mencari endTime terbesar
            for (int i = 0; i < taskVertex.length; i++) {
                if (taskVertex[i].endTime > projectDuration) {
                    projectDuration = taskVertex[i].endTime;
                }
            }
        }
        return projectDuration;
    }

    public double calculateFitness() {
        fitness = 0;
        if (//validation
                projectCost >= 0
                && projectCost < Double.MAX_VALUE
                && projectDuration >= 0
                && projectDuration < Double.MAX_VALUE) {
            double q = wCost * projectCost + wDuration * projectDuration;
            if (q > 0) {
                fitness = 1.0 / q;
            } else {
                fitness = 0;
            }
        }
        return fitness;
    }

    public void calculate() {
        calculateTasksDuration();
        calculateProjectCost();
        calculateProjectDuration();
        calculateFitness();
    }

    public int[][] interaction(Individu sm) {
        int[][] interactionMatrix = null;
        if (/*validation*/dedicationMatrix != null
                && sm.dedicationMatrix != null
                && dedicationMatrix.length == sm.dedicationMatrix.length
                && dedicationMatrix[0].length == sm.dedicationMatrix[0].length) {
            interactionMatrix = new int[dedicationMatrix.length][dedicationMatrix[0].length];
            for (int i = 0; i < interactionMatrix.length; i++) {
                for (int j = 0; j < interactionMatrix[i].length; j++) {
                    double value = sm.dedicationMatrix[i][j] - dedicationMatrix[i][j];
                    interactionMatrix[i][j] = 0;
                    if (value > 0) {
                        interactionMatrix[i][j] = 1;
                    } else if (value < 0) {
                        interactionMatrix[i][j] = -1;
                    }
                }
            }
        }
        return interactionMatrix;
    }

    public void applyOperation(int[][] operation) {
        if (//validation
                operation != null
                && dedicationMatrix != null
                && operation.length == dedicationMatrix.length
                && operation[0].length == dedicationMatrix[0].length) {
            for (int i = 0; i < operation.length; i++) {
                for (int j = 0; j < operation[i].length; j++) {
                    double min = 0;
                    double max = 0;

                    int value = operation[i][j];
                    if (value == 1) {
                        min = dedicationMatrix[i][j];
                        max = employees[i].getMaxDedication();
                    } else if (value == -1) {
                        min = 0;
                        max = dedicationMatrix[i][j];
                    }

                    //update Xij
                    if (isConnected(employees[i].getSkills(), tasks[j].getSkills())) {
                        connectionMatrix[i][j] = 1;
                        double newXij = Algorithm.randomBetweenDouble(min, max);
                        double xij = Math.round(newXij * 100.0) / 100.0;
                        dedicationMatrix[i][j] = xij;
                    }
                }
            }//end of for i
            //hitung fitness
            calculate();
        }
    }

    public void printDedicationMatrix() {
        if (dedicationMatrix != null) {
            //cetak dedication matrix
            for (int j = 0; j < dedicationMatrix.length; j++) {
                System.out.println(Arrays.toString(dedicationMatrix[j]));
            }
        } else {
            System.out.println("DEDICATION MATRIX = NULL");
        }
    }

    public Individu clone() {
        Individu newIndividu = new Individu(employees, tasks, taskPrecedenceGraph);
        newIndividu.numEmployees = numEmployees;
        newIndividu.numTasks = numTasks;
        newIndividu.dedicationMatrix = dedicationMatrix.clone();
        newIndividu.connectionMatrix = connectionMatrix.clone();//digunakan untuk menyatakan ada atau tidak irisan skill antara employee dan task
        newIndividu.taskDuration = taskDuration.clone();
        newIndividu.projectCost = projectCost;
        newIndividu.projectDuration = projectDuration;
        newIndividu.taskVertex = taskVertex.clone();
        newIndividu.fitness = fitness;
        newIndividu.wCost = wCost;
        newIndividu.wDuration = wDuration;
        return newIndividu;

    }

}
