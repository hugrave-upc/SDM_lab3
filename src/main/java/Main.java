import ontology.owl.DefineTBox;

/**
 * Created by edoardo on 03/05/2019.
 */


public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage:\ncreate_ontology: Creates a new ontology for the kg.");
            throw new Exception("At least one argument required.");
        }
        String operation = args[0];

        if ("create_ontology".equals(operation)) {
            DefineTBox.run();
        }
    }
}
