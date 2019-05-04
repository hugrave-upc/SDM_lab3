package ontology.owl;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.impl.RDFLangString;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.relaxng.datatype.Datatype;
import triplestore.Virtuoso;
import virtuoso.jena.driver.VirtModel;

/**
 * Created by edoardo on 03/05/2019.
 */
public class DefineABox {

    private static OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
    private static OntModel tmpModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

    private static final String dbo = "http://dbpedia.org/ontology/";

    private static final String ns = "http://localhost/resource#";
    private static final String ontNS = "http://localhost/ontology#";
    private static final String baseURI = "http://localhost/resource";
    private static Ontology onto = ontModel.createOntology(baseURI);

    public static void run(VirtModel vm, OntModel TBoxModel) {
        /*
         * Importing the authors
         */
        importWriters(TBoxModel);

        Model researchResource = vm.add(ontModel);
    }

    private static void importWriters(OntModel tboxModel) {

        OntClass writer = tboxModel.getOntClass(ontNS + "ScientificWriter");
        Individual sokratis = writer.createIndividual(ns + "Sokratis");

        DatatypeProperty name = tboxModel.getDatatypeProperty(dbo + "name");
        if (name == null) {
            System.out.println("ops");
            System.exit(1);
        }

        Literal sokratis_name = ontModel.createTypedLiteral("Sokratis", RDFLangString.rdfLangString);
        Statement s = ontModel.createStatement(sokratis, name, sokratis_name);
        ontModel.add(s);
    }
}
