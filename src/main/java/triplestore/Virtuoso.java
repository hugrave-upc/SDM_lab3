package triplestore;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;

/**
 * Created by edoardo on 03/05/2019.
 */
public class Virtuoso {
    private static final String VirtURL = "jdbc:virtuoso://sandslash.fib.upc.es:1111/";
    private static final String user = "dba";
    private static final String psw = "dba";
    private static final String graph_name = "http://localhost:8890/research";

    public static VirtModel connect() {
        System.out.println("Connecting to Virtuoso...");
        VirtGraph graph = new VirtGraph(graph_name, VirtURL, user, psw);
        System.out.println("Connected.");
        graph.clear();
        return new VirtModel(graph);
    }
}
