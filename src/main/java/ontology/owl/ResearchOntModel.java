package ontology.owl;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Created by edoardo on 05/05/2019.
 */
public class ResearchOntModel {

    private OntModel model;

    private ResearchOntModel() {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        String baseURI = "http://localhost/ontology";
        Ontology onto = this.model.createOntology(baseURI);
    }

    private static final ResearchOntModel instance = new ResearchOntModel();

    public static OntModel getInstance() {
        return instance.model;
    }
}
