package ontology.owl;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import triplestore.Virtuoso;
import virtuoso.jena.driver.*;


/**
 * Created by edoardo on 03/05/2019.
 */
public class DefineTBox {
    private static final String dbo = "http://dbpedia.org/ontology/";

    // Creating the ontology
    private static OntModel ontModel = ResearchOntModel.getInstance();
    private static final String ns = "http://localhost/ontology#";


    public static void run() {
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

        ontModel.read(dbo + "AcademicJournal");
        OntClass journal = ontModel.getOntClass(dbo + "AcademicJournal");
        OntClass openJournal = ontModel.createClass(ns + "OpenAccessJournal");
        OntClass subscribingJournal = ontModel.createClass(ns + "SubscribingJournal");
        journal.addSubClass(openJournal);
        journal.addSubClass(subscribingJournal);
        journal.removeSuperClass(ontModel.getResource(dbo + "PeriodicalLiterature"));

        ontModel.read(dbo + "AcademicConference");
        OntClass academicConference = ontModel.getOntClass(dbo + "AcademicConference");
        OntClass conference = ontModel.createClass(ns + "Conference");
        OntClass workshop = ontModel.createClass(ns + "Workshop");
        academicConference.addSubClass(conference);
        academicConference.addSubClass(workshop);

        OntClass edition = ontModel.createClass(ns + "Edition");
        subClassOf(edition, dbo + "SocietalEvent");

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

        ontModel.read(dbo + "Organisation");
        ontModel.read(dbo + "Company");
        ontModel.read(dbo + "University");
        OntClass organization = ontModel.getOntClass(dbo + "Organisation");
        OntClass company = ontModel.getOntClass(dbo + "Company");
        OntClass university = ontModel.getOntClass(dbo + "University");

        ontModel.read(dbo + "City");

        /*
         * Defining the object properties
         */
        ontModel.read(dbo + "writer");
        ObjectProperty writer_prop = ontModel.createObjectProperty(dbo + "writer");
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

        ontModel.read(dbo + "publisher");
        ObjectProperty publisher = ontModel.getObjectProperty(dbo + "publisher");
        publisher.addDomain(collection);

        ObjectProperty conferencePublisher = ontModel.createObjectProperty(ns + "conferencePublisher");
        conferencePublisher.addDomain(proceeding);
        conferencePublisher.addRange(edition);
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


        ontModel.read(dbo + "employer");
        ObjectProperty employer = ontModel.getObjectProperty(dbo + "employer");
        employer.addDomain(writer);
        employer.addRange(organization);

        ObjectProperty about = ontModel.createObjectProperty(ns + "about");
        about.setDomain(review);
        about.setRange(paper);

        ontModel.read(dbo + "city");
        ObjectProperty inCity = ontModel.getObjectProperty(dbo + "city");
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
        DatatypeProperty proceedID = ontModel.createDatatypeProperty(ns + "proceedingID");
        proceedID.setDomain(proceeding);
        DatatypeProperty volumeID = ontModel.createDatatypeProperty(ns + "volumeID");
        volumeID.setDomain(volume);
        DatatypeProperty editionID = ontModel.createDatatypeProperty(ns + "editionID");
        editionID.setDomain(edition);

        ID.addSubProperty(paperID);
        ID.addSubProperty(proceedID);
        ID.addSubProperty(volumeID);
        ID.addSubProperty(editionID);


        DatatypeProperty decision = ontModel.createDatatypeProperty(ns + "decision");
        decision.setDomain(review);
        decision.setRange(XSD.xstring);
        DatatypeProperty description = ontModel.createDatatypeProperty(ns + "description");
        description.setDomain(review);
        description.setRange(XSD.xstring);

        ontModel.read(dbo + "name");
        DatatypeProperty name = ontModel.getDatatypeProperty(dbo + "name");
        name.setDomain(writer);
        name.addDomain(organization);
        name.addDomain(journal);
        name.addDomain(academicConference);

        ontModel.read(dbo + "title");
        DatatypeProperty title = ontModel.getDatatypeProperty(dbo + "title");
        title.setDomain(paper);
        //title.setRange(XSD.xstring);

        DatatypeProperty year = ontModel.createDatatypeProperty(ns + "year");
        year.setDomain(volume);
        year.addDomain(edition);
        year.setRange(XSD.gYear);

        DatatypeProperty url = ontModel.createDatatypeProperty(ns + "url");
        url.setDomain(organization);
        url.setRange(XSD.anyURI);

    }

    private static void subClassOf(OntClass child, String parentURI) {
        ontModel.read(parentURI);
        OntClass parent = ontModel.getOntClass(parentURI);
        parent.addSubClass(child);
    }
}
