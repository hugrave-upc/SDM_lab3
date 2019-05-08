package utils;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

import java.util.*;

/**
 * Created by edoardo on 05/05/2019.
 */
public class Utils {

    private static List<String> companies = new ArrayList<String>();
    private static List<String> universities = new ArrayList<String>();
    private static Map<String, String> editionProc = new HashMap<String, String>();
    private static Map<String, String> cityName_URI = new HashMap<String, String>();

    private static final String dbr = "http://dbpedia.org/resource/";
    private static final String resNS = "http://localhost/resource#";

    private static Random r = new Random();

    public static String cleanURI(String id) {
        return id.replaceAll(" ", "_");
    }

    // Lower included, upper excluded
    public static int randomInt(int lower, int upper) {
        return (Math.abs(r.nextInt()) % (upper-lower)) + lower;
    }

    public static void addUniversity(String university) {
        universities.add(university);
    }

    public static void addCompany(String company) {
        companies.add(company);
    }

    public static List<String> getCompanies() {
        return companies;
    }

    public static List<String> getUniversities() {
        return universities;
    }

    public static void putProceeding(String editionID, String proceedingID) {
        editionProc.put(editionID, proceedingID);
    }

    public static String getProceeding(String editionID) {
        return editionProc.get(editionID);
    }


    public static String makeISBN()
    {
        String laendercode;
        String bandnr;
        String verlagsnr;
        String checksum;

// Generate Random Numbers for L1L2-B1B2B3-V1V2
        double L1 = Math.random()*(10);
        double L2 = Math.random()*(10);

        double B1 = Math.random()*(10);
        double B2 = Math.random()*(10);
        double B3 = Math.random()*(10);

        double V1 = Math.random()*(10);
        double V2 = Math.random()*(10);

// Check that L1L2 > 0
        if((int)L1 == 0 && (int)L2 == 0) {
            L2++;
        }
// Check that L1B2B3 >= 100
        if((int)B1 == 0) {
            B1++;
        }
// Check that V1V2 > 0
        if((int)V1 == 0 && (int)V2 == 0) {
            V2++;
        }
// Compute check digit with hashOp method
        double C = (hashOp((int)L1) +L2 + hashOp((int)B1) +B2 + hashOp((int)B3) +V1 + hashOp((int)V2))%10;

// Convert the generated numbers to String
        laendercode     = (int)L1+""+(int)L2;
        bandnr          = (int)B1+""+(int)B2+""+(int)B3;
        verlagsnr       = (int)V1+""+(int)V2;
        checksum        = (int)C+"";

        return cleanURI(laendercode + "-" + bandnr + "-" + verlagsnr + "-" + checksum);
    }

    public static int hashOp(int i)
    {
        // used to determine C
        int doubled = 2 * i;
        if ( doubled >= 10 ) {
            doubled = doubled - 9;
        }
        return doubled;
    }

    // Simple entity resolution for the city in DBPEdia
    public static String getClosestCity(String cityName) {
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

        QueryExecution qExec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", pss.asQuery());

        ResultSet rs = qExec.execSelect();
        Resource city = null;
        while(rs.hasNext()) {
            QuerySolution qs = rs.next();
            city = qs.getResource("city");
        }
        qExec.close() ;

        if (city == null) {
            cityName_URI.put(cityName, resNS + cityName);
        }
        else {
            cityName_URI.put(cityName, city.toString());
        }
        return cityName_URI.get(cityName);
    }

    public static boolean hasCity (String cityName) {
        return cityName_URI.containsKey(cityName);
    }

    public static String getCityURI (String cityName) {
        return cityName_URI.get(cityName);
    }


}
