package ontology.owl;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.ModelFactory;
import triplestore.Virtuoso;
import virtuoso.jena.driver.VirtModel;

/**
 * Created by edoardo on 03/05/2019.
 */
public class DefineABox {

    private static OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
    private static final String ns = "http://localhost/resource#";
    private static final String ontNS = "http://localhost/ontology#";
    private static final String baseURI = "http://localhost/resource";
    private static Ontology onto = ontModel.createOntology(baseURI);

    public static void run(VirtModel vm, OntModel TBoxModel) {
        /*
         * Importing the authors
         */
        importAuthors(TBoxModel);

    }

    private static void importAuthors(OntModel TBoxModel) {

    }
}
