
public class ParallelBFS {
	private int noOfNodes;
	private boolean[] visited;
	private int noOfCores;
	private Graph graph;
	private Thread[] processors;

	public ParallelBFS(int noOfNodes, boolean[] visited, int noOfCores) {
		this.noOfNodes = noOfNodes;
		this.visited = visited;
		this.noOfCores = noOfCores;
		graph = new Graph(noOfNodes, visited, noOfCores);
		processors = new Processor[noOfCores];
		start();
		stop();
	}
	// Create processors (threads) depending on user's input (noOfCores)
	// Start the thread, Run BFS
	public void start() {
		for(int i = 0; i < noOfCores; i++){
            processors[i] = new Processor(graph, i);
            processors[i].start();
        }
	}
	// Stop the thread
	public void stop() {
		for(int i = 0; i < noOfCores; i++){
            try {
                processors[i].join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
	}

	public boolean[] getVisited() {
		return visited;
	}
}