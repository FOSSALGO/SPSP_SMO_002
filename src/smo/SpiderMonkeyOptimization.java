package smo;

import java.util.Random;
import spsp.Employee;
import spsp.Task;

public class SpiderMonkeyOptimization {

    //DATA INPUT
    private Employee[] employees = null;
    private Task[] tasks = null;
    private int[][] taskPrecedenceGraph = null;

    //PARAMETER SMO
    private int I;//Total Number of Iterations
    private int MG;//Allowed Maximum Group
    private double pr;//Perturbation Rate
    private int LLL;//Local Leader Limit
    private int GLL;//Global Leader Limit
    private int N;//Total Number of Spider Monkeys = nPopulasi     

    //VARIABLES
    private int t = 0;//iteration counter
    private int g = 0;//Current Number of Group
    private int groupSize = 1;//banyaknya spider monkey di setiap group
    private Individu[] spiderMonkey = null;//SM = Population of Spider Monkey
    private Individu globalLeader = null;
    private int globalLeaderLimitCounter = 0;//GLLc = Global Leader Limit Counter
    private Individu[] localLeader = null;//LL = List of Local Leader
    private int[] localLeaderLimitCounter = new int[g];//LLLc = Local Leader Limit Counter of kth Group

    //Output
    public Individu bestIndividu = null;

    //Random
    private Random random = new Random();

    public SpiderMonkeyOptimization(
            Employee[] employees,
            Task[] tasks,
            int[][] taskPrecedenceGraph,
            int MAX_ITERATION,
            int allowedMaximumGroup,
            double perturbationRate,
            int localLeaderLimit,
            int globalLeaderLimit,
            int totalNumberOfSpiderMonkey) {
        this.employees = employees;
        this.tasks = tasks;
        this.taskPrecedenceGraph = taskPrecedenceGraph;
        this.I = MAX_ITERATION;
        this.MG = allowedMaximumGroup;
        this.pr = perturbationRate;
        this.LLL = localLeaderLimit;
        this.GLL = globalLeaderLimit;
        this.N = totalNumberOfSpiderMonkey;

    }

    private void validation() {
        if (this.MG <= 0) {
            this.MG = 1;
        }
    }

    public void run() {
        //Step 1: INISIALISASI--------------------------------------------------
        this.t = 1;/* (1) t ← 1 */
        this.spiderMonkey = new Individu[N];/* (2) create N spider moneys and append them to SM */

        // (3)Assign each SMi in SM with a random solution 
        int indexGlobalLeader = -1;
        double globalFitness = 0;
        for (int i = 0; i < N; i++) {
            spiderMonkey[i] = new Individu(employees, tasks, taskPrecedenceGraph);
            spiderMonkey[i].generateDedicationMatrix();
            spiderMonkey[i].calculate();
            if (globalFitness < spiderMonkey[i].fitness) {
                globalFitness = spiderMonkey[i].fitness;
                indexGlobalLeader = i;
            }
        }

        /* (4) g = 1 initially consider all spiderMonkey into one group */
        this.g = 1;
        this.groupSize = N;//mula-mula hanya ada satu group jadi group tersebut akan berukuran = N

        /* (5) Select Local Leader and Global Leader // Both leaders are same due to single group */
        //GLOBAL GLOBAL
        if (indexGlobalLeader >= 0) {
            this.globalLeader = spiderMonkey[indexGlobalLeader].clone();
        } else {
            this.globalLeader = spiderMonkey[0].clone();
        }
        this.globalLeaderLimitCounter = 0;//GLLc

        //LOCAL LEADER
        this.localLeader = new Individu[g];
        this.localLeader[0] = this.globalLeader.clone();
        this.localLeaderLimitCounter = new int[g];//LLLc

        //Step 2: MAIN OPERATION------------------------------------------------
        while (t <= I) {
            //-----------------------------------------------------
            // (2) UPDATE of Spider Monkeys                        
            //-----------------------------------------------------
            double[] minCost = new double[g];

            // UPDATE Spider Monkeys base on local Leader-----------------------
            for (int k = 0; k < g; k++) { //iterasi di group-k
                //set first and last
                int first = k * groupSize;
                int last = first + groupSize - 1;
                if (k == g - 1) {
                    last = N - 1;
                }

                //update spider monkey base on local leader---------------------
                minCost[k] = Double.MAX_VALUE;
                for (int i = first; i <= last; i++) {
                    double u = random.nextDouble();//U(0,1)
                    if (u >= pr) {
                        //interaction-------------------------------------------
                        int r = Algorithm.randomBetweenInteger(first, last);
                        while (r == i && last - first > 0) {
                            r = Algorithm.randomBetweenInteger(first, last);
                        }
                        Individu rms = spiderMonkey[r];
                        int[][] operation1 = spiderMonkey[i].interaction(localLeader[k]);
                        int[][] operation2 = spiderMonkey[i].interaction(rms);
                        int[][] operation = Algorithm.mergeOperations(operation1, operation2);

                        //operation is applied to SM new
                        Individu SMNew = spiderMonkey[i].clone();
                        SMNew.applyOperation(operation);

                        //update SM with SMNew if fitness of SMnew is better than SM
                        if (SMNew.fitness > spiderMonkey[i].fitness) {
                            spiderMonkey[i] = SMNew;
                        }

                        //save minCost[k]
                        double cost = 1.0 / spiderMonkey[i].fitness;
                        if (cost < minCost[k]) {
                            minCost[k] = cost;
                        }
                    }
                }
            }//end of for k

            // UPDATE Spider Monkeys base on global Leader----------------------
            for (int k = 0; k < g; k++) { //iterasi di group-k
                //set first and last
                int first = k * groupSize;
                int last = first + groupSize - 1;
                if (k == g - 1) {
                    last = N - 1;
                }

                //update spider monkey base on global leader---------------------
                for (int i = first; i <= last; i++) {
                    double u = random.nextDouble();//U(0,1)
                    double cost = 1.0 / spiderMonkey[i].fitness;
                    double min_cost = minCost[k];
                    double prob = 0.9 * (min_cost / cost) + 0.1;
                    if (u <= prob) {
                        //interaction-------------------------------------------
                        //int r = Algorithm.randomBetweenInteger(first, last);
                        int r = Algorithm.randomBetweenInteger(0, N - 1);
                        //while(r==i  && last-first>0){
                        while (r == i && N - 1 > 0) {
                            //r = Algorithm.randomBetweenInteger(first, last);
                            r = Algorithm.randomBetweenInteger(0, N - 1);
                        }
                        Individu rms = spiderMonkey[r];
                        int[][] operation1 = spiderMonkey[i].interaction(globalLeader);
                        int[][] operation2 = spiderMonkey[i].interaction(rms);
                        int[][] operation = Algorithm.mergeOperations(operation1, operation2);

                        //operation is applied to SM new
                        Individu SMNew = spiderMonkey[i].clone();
                        SMNew.applyOperation(operation);

                        //update SM with SMNew if fitness of SMnew is better than SM
                        if (SMNew.fitness > spiderMonkey[i].fitness) {
                            spiderMonkey[i] = SMNew;
                        }
                    }
                }

            }//end of for k

            //------------------------------------------------------
            //(3) UPDATE of Local Leader and Global Leader        
            //------------------------------------------------------
            //(3.1) check new local leader
            Individu newGlobalLeader = globalLeader.clone();
            for (int k = 0; k < g; k++) { //iterasi di group-k
                //set first and last
                int first = k * groupSize;
                int last = first + groupSize - 1;
                if (k == g - 1) {
                    last = N - 1;
                }

                Individu newLocalLeader = localLeader[k].clone();
                for (int i = first; i <= last; i++) {
                    if (spiderMonkey[i].fitness > newLocalLeader.fitness) {
                        newLocalLeader = spiderMonkey[i].clone();
                    }
                }

                //mengganti local leader dengan local leader yang baru jika ditemukan SM yang fitnesnya lebih besar dari fitness local leader
                if (newLocalLeader.fitness > localLeader[k].fitness) {
                    localLeader[k] = newLocalLeader.clone();
                    localLeaderLimitCounter[k] = 0;
                } else {
                    localLeaderLimitCounter[k]++;//jika tidak menemukan Local Leader yang baru maka LLLc diincrement
                }

                //check apakah newLocalLeader lebih baik dari newGlobalLeader
                if (newLocalLeader.fitness > newGlobalLeader.fitness) {
                    newGlobalLeader = newLocalLeader.clone();
                }

            }//end of for k

            //(3.2) check new global leader
            if (newGlobalLeader.fitness > globalLeader.fitness) {
                globalLeader = newGlobalLeader.clone();
                globalLeaderLimitCounter = 0;
            } else {
                globalLeaderLimitCounter++;//jika tidak menemukan Global Leader yang baru maka GLLc diincrement
            }

            //------------------------------------------------------
            // (4) DECISION PHASE of Local Leader and Global Leader 
            //------------------------------------------------------
            //(4.1) Decision Phase of Local Leader
            for (int k = 0; k < g; k++) { //iterasi di group-k
                if (localLeaderLimitCounter[k] > LLL) {
                    localLeaderLimitCounter[k] = 0;

                    //ganti semua spider monkey yang ada di group k jika LLL sudah tercapai
                    int first = k * groupSize;
                    int last = first + groupSize - 1;
                    if (k == g - 1) {
                        last = N - 1;
                    }

                    for (int i = first; i <= last; i++) {
                        double u = random.nextDouble();
                        if (u >= pr) {
                            Individu SMNew = new Individu(employees, tasks, taskPrecedenceGraph);
                            SMNew.generateDedicationMatrix();
                            SMNew.calculate();
                            spiderMonkey[i] = SMNew;
                        } else {
                            int[][] operation1 = globalLeader.interaction(spiderMonkey[i]);
                            int[][] operation2 = localLeader[k].interaction(spiderMonkey[i]);
                            int[][] operation = Algorithm.mergeOperations(operation1, operation2);

                            //operation is applied to SM new
                            Individu SMNew = spiderMonkey[i].clone();
                            SMNew.applyOperation(operation);

                            //update SM with SMNew if fitness of SMnew is better than SM
                            if (SMNew.fitness > spiderMonkey[i].fitness) {
                                spiderMonkey[i] = SMNew;
                            }
                        }
                    }//en of for i
                }//end of if
            }//end of for k

            //(4.2) Decision Phase of Global Leader
            if (globalLeaderLimitCounter > GLL) {
                globalLeaderLimitCounter = 0;
                if (g < MG) {
                    //Divide the spider monkeys into g + 1 number of group
                    g++;
                    groupSize = (int) Math.floor((double) N / (double) g);
                    this.localLeader = new Individu[g];
                    this.localLeaderLimitCounter = new int[g];//LLc untuk semua Local Leader di nol kan lagi

                    indexGlobalLeader = -1;
                    globalFitness = 0;

                    //check new local leader and global leader
                    for (int k = 0; k < g; k++) { //iterasi di group-k
                        //set first and last
                        int first = k * groupSize;
                        int last = first + groupSize - 1;
                        if (k == g - 1) {
                            last = N - 1;
                        }

                        //find new local leader
                        Individu newLocalLeader = spiderMonkey[first];
                        for (int i = first + 1; i <= last; i++) {
                            if (spiderMonkey[i].fitness > newLocalLeader.fitness) {
                                newLocalLeader = spiderMonkey[i];
                            }
                        }
                        //set new local leader
                        localLeader[k] = newLocalLeader;

                        //find new global leader
                        if (globalFitness < localLeader[k].fitness) {
                            globalFitness = localLeader[k].fitness;
                            indexGlobalLeader = k;
                        }
                    }//end of for k

                    //set new global leader
                    if (indexGlobalLeader >= 0) {
                        if (localLeader[indexGlobalLeader].fitness > globalLeader.fitness) {
                            globalLeader = localLeader[indexGlobalLeader].clone();
                        }
                    }

                } else {
                    //gabungkan kembali semua spider monkey menjadi satu group
                    //disband all the groups and form a single group
                    g = 1;
                    groupSize = (int) Math.floor((double) N / (double) g);
                    localLeader = new Individu[g];
                    localLeader[0] = globalLeader.clone();
                    localLeaderLimitCounter = new int[g];//LLc di-nol-kan lagi
                }
            }//end of if(globalLeaderLimitCounter>GLL)

            //INCREMENT of t----------------------------------------------------
            t = t + 1;// increment of t (iteration)
        }//end of while

        //SET BEST INDIVIDU
        //global leader as best individu
        bestIndividu = globalLeader;

    }

}
