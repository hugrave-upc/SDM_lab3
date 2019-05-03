package ontology.owl;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import virtuoso.jena.driver.*;
import org.apache.jena.graph.Node;


/**
 * Created by edoardo on 03/05/2019.
 */
public class DefineTBox {
    private static final String VirtURL = "jdbc:virtuoso://sandslash.fib.upc.es:1111/";
    private static final String user = "dba";
    private static final String psw = "dba";
    private static final String graph_name = "http://localhost:8890/research";
    private static final String dbo = "http://dbpedia.org/ontology/";

    // Creating the ontology
    private static OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
    private static OntModel tmpModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
    private static final String ns = "http://localhost/ontology#";
    private static final String baseURI = "http://localhost/ontology";
    private static Ontology onto = ontModel.createOntology(baseURI);


    public static void run() throws Exception {
        System.out.println("Connecting to Virtuoso...");
        VirtGraph graph = new VirtGraph(graph_name, VirtURL, user, psw);
        System.out.println("Connected.");
        graph.clear();

        VirtModel vm = new VirtModel(graph);

        /*
         * Defining the classes hierarchy
         */
        OntClass writer = ontModel.createClass(ns + "ScientificWriter");
        OntClass reviewer = ontModel.createClass(ns + "Reviewer");
        writer.addSubClass(reviewer);
        OntClass review = ontModel.createClass(ns + "Review");
        // Integrating with the written work of dbpedia
        subClassOf(review, dbo + "WrittenWork");
        // Integrating the writer class from dbpedia
        subClassOf(writer, dbo + "Writer");

        OntClass collection = ontModel.createClass(ns + "ScientificCollection");
        OntClass proceeding = ontModel.createClass(ns + "ConferenceProceeding");
        OntClass volume = ontModel.createClass(ns + "JournalVolume");
        collection.addSubClass(proceeding);
        collection.addSubClass(volume);

        tmpModel.read(dbo + "AcademicJournal");
        OntClass journal = tmpModel.getOntClass(dbo + "AcademicJournal");
        OntClass openJournal = ontModel.createClass(ns + "OpenAccessJournal");
        OntClass subscribingJournal = ontModel.createClass(ns + "SubscribingJournal");
        journal.addSubClass(openJournal);
        journal.addSubClass(subscribingJournal);


        tmpModel.read(dbo + "AcademicConference");
        OntClass academicConference = tmpModel.getOntClass(dbo + "AcademicConference");
        OntClass conference = ontModel.createClass(ns + "Conference");
        OntClass workshop = ontModel.createClass(ns + "Workshop");
        academicConference.addSubClass(conference);
        academicConference.addSubClass(workshop);

        OntClass edition = ontModel.createClass(ns + "Edition");

        OntClass topic = ontModel.createClass(ns + "ScientificTopic");
        // Integrating with the academic subject from dbpedia
        subClassOf(topic, dbo + "AcademicSubject");

        OntClass keyword = ontModel.createClass(ns + "Keyword");

        OntClass paper = ontModel.createClass(ns + "ScientificPaper");
        subClassOf(paper, dbo + "Article");

        OntClass confPaper = ontModel.createClass(ns + "ConferencePaper");
        OntClass demoPaper = ontModel.createClass(ns + "DemoPaper");
        OntClass shortPaper = ontModel.createClass(ns + "ShortPaper");
        confPaper.addSubClass(demoPaper);
        confPaper.addSubClass(shortPaper);

        OntClass jourPaper = ontModel.createClass(ns + "JournalPaper");
        OntClass fullPaper = ontModel.createClass(ns + "FullPaper");
        OntClass surveyPaper = ontModel.createClass(ns + "SurveyPaper");
        jourPaper.addSubClass(fullPaper);
        jourPaper.addSubClass(surveyPaper);

        paper.addSubClass(confPaper);
        paper.addSubClass(jourPaper);

        tmpModel.read(dbo + "Organisation");
        tmpModel.read(dbo + "Company");
        tmpModel.read(dbo + "University");
        OntClass organization = tmpModel.getOntClass(dbo + "Organisation");
        OntClass company = tmpModel.getOntClass(dbo + "Company");
        OntClass university = tmpModel.getOntClass(dbo + "University");

        tmpModel.read(dbo + "City");
        OntClass city = tmpModel.getOntClass(dbo + "City");

        /*
         * Defining the object properties
         */
        tmpModel.read(dbo + "writer");
        ObjectProperty writer_prop = tmpModel.createObjectProperty(dbo + "writer");
        writer_prop.setDomain(paper);
        writer_prop.addDomain(review);
        writer_prop.setRange(writer);

        ObjectProperty collection_prop = ontModel.createObjectProperty(ns + "collection");
        collection_prop.addDomain(paper);
        collection_prop.addRange(collection);
        ObjectProperty proceeding_prop = ontModel.createObjectProperty(ns + "proceeding");
        proceeding_prop.addDomain(confPaper);
        proceeding_prop.addRange(proceeding);
        ObjectProperty volume_prop = ontModel.createObjectProperty(ns + "volume");
        volume_prop.addDomain(jourPaper);
        volume_prop.addRange(volume);

        collection_prop.addSubProperty(proceeding_prop);
        collection_prop.addSubProperty(volume_prop);

        tmpModel.read(dbo + "publisher");
        ObjectProperty publisher = tmpModel.getObjectProperty(dbo + "publisher");
        publisher.addDomain(collection);

        ObjectProperty conferencePublisher = ontModel.createObjectProperty(ns + "conferencePublisher");
        conferencePublisher.addDomain(proceeding);
        conferencePublisher.addRange(academicConference);
        ObjectProperty jourPublisher = ontModel.createObjectProperty(ns + "journalPublisher");
        jourPublisher.addDomain(volume);
        jourPublisher.addRange(journal);

        publisher.addSubProperty(conferencePublisher);
        publisher.addSubProperty(jourPublisher);

        ObjectProperty edition_prop = ontModel.createObjectProperty(ns + "edition");
        edition_prop.setDomain(academicConference);
        edition_prop.setRange(edition);

        ObjectProperty cites = ontModel.createObjectProperty(ns + "cites");
        cites.setDomain(paper);
        cites.setRange(paper);

        ObjectProperty topic_prop = ontModel.createObjectProperty(ns + "topic");
        topic_prop.setDomain(collection);
        topic_prop.addDomain(keyword);
        topic_prop.setRange(topic);

        tmpModel.read(dbo + "employer");
        ObjectProperty employer = tmpModel.getObjectProperty(dbo + "employer");
        employer.addDomain(writer);
        employer.addRange(organization);

        ObjectProperty about = ontModel.createObjectProperty(ns + "about");
        about.setDomain(review);
        about.setRange(paper);

        tmpModel.read(dbo + "city");
        ObjectProperty inCity = tmpModel.getObjectProperty(dbo + "city");
        inCity.addDomain(edition);
        inCity.addDomain(organization);
        //inCity.addRange(city);

        /*
         * Defining the data properties
         */
        DatatypeProperty ID = ontModel.createDatatypeProperty(ns + "ID");
        ID.setRange(XSD.xstring);
        DatatypeProperty paperID = ontModel.createDatatypeProperty(ns + "paperID");
        paperID.setDomain(paper);
        paperID.setRange(XSD.xstring);
        DatatypeProperty proceedID = ontModel.createDatatypeProperty(ns + "proceedingID");
        proceedID.setDomain(proceeding);
        proceedID.setRange(XSD.xstring);
        DatatypeProperty volumeID = ontModel.createDatatypeProperty(ns + "volumeID");
        volumeID.setDomain(volume);
        volumeID.setRange(XSD.xstring);
        DatatypeProperty editionID = ontModel.createDatatypeProperty(ns + "editionID");
        editionID.setDomain(edition);
        editionID.setRange(XSD.xstring);

        DatatypeProperty decision = ontModel.createDatatypeProperty(ns + "decision");
        decision.setDomain(review);
        decision.setRange(XSD.xstring);
        DatatypeProperty description = ontModel.createDatatypeProperty(ns + "description");
        description.setDomain(review);
        description.setRange(XSD.xstring);

        tmpModel.read(dbo + "name");
        DatatypeProperty name = tmpModel.getDatatypeProperty(dbo + "name");
        name.setDomain(writer);
        name.addDomain(organization);
        name.addDomain(keyword);
        //name.setRange(XSD.xstring);

        tmpModel.read(dbo + "title");
        DatatypeProperty title = tmpModel.getDatatypeProperty(dbo + "title");
        title.setDomain(paper);
        //title.setRange(XSD.xstring);

        DatatypeProperty year = ontModel.createDatatypeProperty(ns + "year");
        year.setDomain(volume);
        year.addDomain(edition);
        year.setRange(XSD.gYear);


        Model researchModel = vm.add(ontModel);
        //researchModel.write(System.out);

    }

    private static void subClassOf(OntClass child, String parentURI) {
        tmpModel.read(parentURI);
        OntClass parent = tmpModel.getOntClass(parentURI);
        parent.addSubClass(child);
    }
}
