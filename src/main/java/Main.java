import ontology.owl.DefineABox;
import ontology.owl.DefineTBox;
import ontology.owl.ResearchOntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Resource;
import triplestore.Virtuoso;
import virtuoso.jena.driver.VirtModel;

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
            VirtModel vm = Virtuoso.connect();

            DefineTBox.run();
            DefineABox.run();

            vm.add(ResearchOntModel.getInstance());
        }
        else if ("debug".equals(operation)) {
            String cityName = "rome";
            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(
                    "select ?city\n" +
                            "where {\n" +
                            "?city a dbo:City .\n" +
                            "?city rdfs:label ?label .\n" +
                            "filter (lang(?label) = 'en') .\n" +
                            "filter regex(?label, ?cityName, 'i')\n" +
                            "}\n" +
                            "limit 1"
            );

            pss.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
            pss.setNsPrefix("dbo", "http://dbpedia.org/ontology/");
            pss.setLiteral("cityName", cityName);

            System.out.println(pss.toString());
            QueryExecution qExec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", pss.asQuery());

            ResultSet rs = qExec.execSelect();
            Resource city = null;
            while(rs.hasNext()) {
                QuerySolution qs = rs.next();
                city = qs.getResource("city");
            }
            qExec.close() ;

            System.out.println(city);

        }
    }
}
