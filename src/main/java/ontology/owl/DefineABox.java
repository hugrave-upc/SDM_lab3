package ontology.owl;


import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.impl.RDFLangString;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;

import utils.CSV;
import utils.Utils;

import java.io.IOException;
import java.util.List;

/**
 * Created by edoardo on 03/05/2019.
 */
public class DefineABox {

    private static final String dbo = "http://dbpedia.org/ontology/";

    private static final String ns = "http://localhost/resource#";
    private static final String ontNS = "http://localhost/ontology#";

    private static final String separator = "\t";

    public static void run() {

        try {
            //importWriters();
            //importPapers();
            importWriterProp();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


    }

    private static void importWriters() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String authorFilePath = "src/main/resources/authors.csv";
        List<String[]> lines = CSV.read(authorFilePath, separator);

        OntClass writerClass = ontModel.getOntClass(ontNS + "ScientificWriter");
        DatatypeProperty nameProp = ontModel.getDatatypeProperty(dbo + "name");

        for (String[] line : lines) {
            String writerName = line[0];
            Individual writer = writerClass.createIndividual(ns + Utils.cleanURI(writerName));
            Literal literalName = ontModel.createTypedLiteral(writerName, RDFLangString.rdfLangString);
            Statement s = ontModel.createStatement(writer, nameProp, literalName);
            ontModel.add(s);
        }

    }

    private static void importPapers() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String paperFilePath = "src/main/resources/articles.csv";
        List<String[]> lines = CSV.read(paperFilePath, separator);

        OntClass demoPaperClass = ontModel.getOntClass(ontNS + "DemoPaper");
        OntClass shortPaperClass = ontModel.getOntClass(ontNS + "ShortPaper");
        OntClass fullPaperClass = ontModel.getOntClass(ontNS + "FullPaper");
        OntClass surveyPaperClass = ontModel.getOntClass(ontNS + "SurveyPaper");
        DatatypeProperty titleProp = ontModel.getDatatypeProperty(dbo + "title");
        DatatypeProperty paperIDProp = ontModel.getDatatypeProperty(ontNS + "paperID");

        for (String[] line : lines) {
            String paperID = line[0];
            String paperTitle = line[1];

            OntClass[] paperTypes = {demoPaperClass, shortPaperClass, fullPaperClass, surveyPaperClass};

            int randPaper = Utils.randomInt(0, 4);

            Individual paper = paperTypes[randPaper].createIndividual(ns + "paper" + paperID);
            Literal literalTitle = ontModel.createTypedLiteral(paperTitle, XSDDatatype.XSDstring);
            Literal literalID = ontModel.createTypedLiteral(paperID, XSDDatatype.XSDstring);

            Statement s = ontModel.createStatement(paper, titleProp, literalTitle);
            ontModel.add(s);
            s = ontModel.createStatement(paper, paperIDProp, literalID);
            ontModel.add(s);
        }
    }

    private static void importWriterProp() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String writerProFilePath = "src/main/resources/writes.csv";
        List<String[]> lines = CSV.read(writerProFilePath, separator);

        ObjectProperty writerProp = ontModel.getObjectProperty(dbo + "writer");
        int i = 0;
        for (String[] line : lines) {
            String writerName = Utils.cleanURI(line[0]);
            String paperID = line[2];

            Individual paper = ontModel.getIndividual(ns + paperID);
            Individual writer = ontModel.getIndividual(ns + writerName);
            Statement s = ontModel.createStatement(paper, writerProp, writer);
            ontModel.add(s);
            i++;
            if (i == 10)
                break;
        }
    }
}
