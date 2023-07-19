package spsp;

import java.util.ArrayList;

public class TaskVertex {

    private final String name;
    private double duration;
    private double startTime;
    public double endTime;
    public ArrayList<TaskVertex> leftTaskVertex = null;
    public ArrayList<TaskVertex> rightTaskVertex = null;

    public TaskVertex(String name, double duration) {
        this.name = name;
        this.duration = duration;
    }

    public void addLeft(TaskVertex taskVertex) {
        if (leftTaskVertex == null) {
            leftTaskVertex = new ArrayList<>();
        }
        leftTaskVertex.add(taskVertex);
    }

    public void addRight(TaskVertex taskVertex) {
        if (rightTaskVertex == null) {
            rightTaskVertex = new ArrayList<>();
        }
        rightTaskVertex.add(taskVertex);
    }

    //data binding
    public void updateStartTime(double startTime) {
        this.startTime = startTime;
        this.endTime = this.startTime + this.duration;
        //update start time for all right nodes
        if (rightTaskVertex != null) {
            for (int i = 0; i < rightTaskVertex.size(); i++) {
                double maxEndTime = rightTaskVertex.get(i).getMaxEndTimeFromLeftTaskVertex();
                rightTaskVertex.get(i).updateStartTime(maxEndTime);
            }
        }
    }

    private double getMaxEndTimeFromLeftTaskVertex() {
        double maxEndTime = 0;
        if (leftTaskVertex != null) {
            for (int i = 0; i < leftTaskVertex.size(); i++) {
                if (leftTaskVertex.get(i).endTime > maxEndTime) {
                    maxEndTime = leftTaskVertex.get(i).endTime;
                }
            }
        }
        return maxEndTime;
    }

    public TaskVertex clone() {
        TaskVertex newTaskVertex = new TaskVertex(name, duration);
        newTaskVertex.startTime = startTime;
        newTaskVertex.endTime = endTime;
        newTaskVertex.leftTaskVertex = null;
        if (leftTaskVertex != null) {
            newTaskVertex.leftTaskVertex = new ArrayList<>();
            for (TaskVertex tv : leftTaskVertex) {
                newTaskVertex.leftTaskVertex.add(tv);
            }
        }
        newTaskVertex.rightTaskVertex = null;
        if (rightTaskVertex != null) {
            newTaskVertex.rightTaskVertex = new ArrayList<>();
            for (TaskVertex tv : rightTaskVertex) {
                newTaskVertex.rightTaskVertex.add(tv);
            }
        }
        return newTaskVertex;
    }

    public String toString() {
        String info = "-----------------------------\n";
        info = info + "Node       : " + this.name + "\n";
        info = info + "start time : " + this.startTime + "\n";
        info = info + "duration   : " + this.duration + "\n";
        info = info + "end time   : " + this.endTime + "\n";
        info = info + "Left Nodes <---\n";
        if (leftTaskVertex != null) {
            for (int i = 0; i < leftTaskVertex.size(); i++) {
                info = info + leftTaskVertex.get(i).name + "\n";
            }
        } else {
            info = info + "NULL\n";
        }
        info = info + "---> Right Node\n";
        if (rightTaskVertex != null) {
            for (int i = 0; i < rightTaskVertex.size(); i++) {
                info = info + rightTaskVertex.get(i).name + "\n";
            }

        } else {
            info = info + "NULL\n";
        }
        return info;
    }

}
