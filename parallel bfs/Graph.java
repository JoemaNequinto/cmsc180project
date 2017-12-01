import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class Graph {
    // number of nodes
    private int size;
    // adjacency matrix
    private int[][] vertices;
    // main queue
    private Queue<Integer> globalQueue;
    // one queue per processor
    private List<Queue<Integer>> localQueues;
    // visited status
    private boolean[] visited;
    // flags
    private boolean isDone;
    private int counter;

    // Constructor, initializing variables
    public Graph(int size, boolean[] visited, int noOfCore) {
        this.size = size;
        localQueues = new ArrayList<Queue<Integer>>(noOfCore);
        for(int i = 0; i < noOfCore; i++){
            localQueues.add(new PriorityQueue<Integer>());
        }
        vertices = new int[size][size];
        this.visited = visited;
        isDone = false;
        globalQueue = new PriorityQueue<Integer>();
        globalQueue.add(size - 1);
        counter = 0;
        // Random 1 or 0 in the adjacency matrix
        // If 1 - neighbors, 0 - not neigbors
        for(int i = 0; i < this.size; i++){
            for(int j = 0; j < this.size; j++){
                Random boolNumber = new Random();
                boolean edge = boolNumber.nextBoolean();
                // Undirected graph
                if(i == j) vertices[i][j] = 1;
                // Random
                else vertices[i][j] = edge ? 1 : 0;
            }
        }
    }

    public List<Queue<Integer>> getLocalQueues() {
        return localQueues;
    }

    public void setLocalQueues(List<Queue<Integer>> localQueues) {
        this.localQueues = localQueues;
    }

    public int getSize() {
        return size;
    }

    public boolean isDone() {
        return isDone;
    }

    public synchronized boolean getVisited(int index) {
        return visited[index];
    }

    public synchronized void setVisited(int index, boolean value) {
        visited[index] = value;
    }

    public synchronized void addQueue(Queue<Integer> temp) {
        while(!temp.isEmpty()){
            globalQueue.add(temp.poll());
        }
    }

    public boolean isNeighbor(int node, int neighbor) {
        return vertices[node][neighbor] == 1 ? true : false;
    }

    public synchronized void incrementCounter() {
        counter++;
    }

    // Parallel BFS
    public synchronized void bfs() {
        // Wait
        while(!isDone && globalQueue.isEmpty()){
            try{
                wait();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Current Processor
        int index = (int)(Thread.currentThread().getId());
        if(!globalQueue.isEmpty()){
            boolean popped = false;
            int node = globalQueue.poll();
            popped = true;
            while(visited[node]){
                if(globalQueue.isEmpty()){
                    isDone = true;
                    popped = false;
                    break;
                } else {
                    node = globalQueue.poll();
                    popped = true;
                }
            }
            if(popped) {
                visited[node] = true;
                counter++;
                boolean flag = false;
                for(int i = 0; i < size; i++){
                    if(node == i) continue;
                    if(isNeighbor(node, i) && !visited[i] && !flag){
                        localQueues.get(index).add(i);
                        flag = true;
                    }
                    if(isNeighbor(node, i) && !visited[i] && flag){
                        globalQueue.add(i);
                    }
                }
            }
        }
        if(globalQueue.isEmpty()) isDone = true;
        if(isDone && counter < size) {
            isDone = false;
            for(int i = 0; i < size; i++){
                if(!visited[i]) globalQueue.add(i);
            }
        }
        // Notify all threads, done executing for this current thread
        notifyAll();
    }

}
