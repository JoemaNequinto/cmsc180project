import java.util.Calendar;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Main <noOfNodes> <noOfCores>");
            return;
        }

        long start, finish;
        final int noOfNodes = Integer.parseInt(args[0]);
        final int noOfCores = Integer.parseInt(args[1]);

        boolean[] visited = new boolean[noOfNodes];
        
        // Parallel BFS
        for(int i = 0; i < noOfNodes; i++){
            visited[i] = false;
        }
        
        start = Calendar.getInstance().getTimeInMillis();
        ParallelBFS parallelBFS = new ParallelBFS(noOfNodes, visited, noOfCores);
        finish = Calendar.getInstance().getTimeInMillis();
        
        System.out.println("Parallel Time: " +  (double)((finish - start) / 1000.0) + " seconds");
        
        boolean success = true;
        for(int i = 0; i < noOfCores; i++){
            if(!parallelBFS.getVisited()[i]){
                success = false;
                System.out.println("Parallel BFS failed, some nodes are not visited!");
                break;
            }
        }
        if(success) System.out.println("Parallel BFS successful, all nodes are visited!");

        // Serial BFS
        for(int i = 0; i < noOfNodes; i++) {
            visited[i] = false;
        }
        start = Calendar.getInstance().getTimeInMillis();
        SerialBFS serialBFS = new SerialBFS(noOfNodes, visited, 1);
        Thread serial = new Thread(serialBFS);
        serial.start();
        try {
            serial.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        finish = Calendar.getInstance().getTimeInMillis();
        System.out.println("Serial Time: " + (double)((finish - start) / 1000.0) + " seconds");
        success = true;
        for(int i = 0; i < noOfNodes; i++) {
            if(!visited[i]) {
                success = false;
                System.out.println("Serial BFS failed, some nodes are not visited!");
                break;
            }
        }
        if(success) System.out.println("Serial BFS successful, all nodes are visited!");
    }
}
