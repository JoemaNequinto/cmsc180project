import java.util.Queue;
import java.util.PriorityQueue;

public class Processor extends Thread {
    private int threadNumber;
    private Graph graph;

    public Processor(Graph g, int id) {
        this.threadNumber = id;
        setName("Processor " + id);
        this.graph = g;
    }

    @Override
    public void run() {
        while(!graph.isDone()){
            // Main BFS - global queue
            graph.bfs();
            // notify the scheduler that the current thread is willing to yield its current use of a processor
            yield();
            // Sub BFS - BFS of each local queues
            subBfs(graph.getLocalQueues().get(threadNumber));
        }
    }

    @Override
    public long getId() {
        return threadNumber;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    // BFS of a localQueue
    public void subBfs(Queue<Integer> localQueue){
        Queue<Integer> tempQ = new PriorityQueue<Integer>();
        while(!localQueue.isEmpty()){
            int node = localQueue.poll();
            if(!graph.getVisited(node)){
                graph.setVisited(node, true);
                graph.incrementCounter();
                boolean toLocal = true;
                for(int i = 0; i < graph.getSize(); i++){
                    if(node == i) continue;
                    if(graph.isNeighbor(node, i) && !toLocal && !graph.getVisited(i)){
                        tempQ.add(i);
                    }
                    if(graph.isNeighbor(node, i) && toLocal && !graph.getVisited(i)){
                        localQueue.add(i);
                        toLocal = false;
                    }
                }
            }
        }
    }
}
