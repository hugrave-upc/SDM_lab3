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

            System.out.println("Importing companies...");
            importCompanies();

            System.out.println("Importing universities...");
            importUniversities();

            System.out.println("Importing writers...");
            importWriters();

            System.out.println("Importing journals...");
            importJournals();

            System.out.println("Importing volumes...");
            importVolumes();
            importYearVolumes();

            System.out.println("Importing conferences...");
            importConferences();

            System.out.println("Importing editions...");
            importEditions();
            importEditionCities();
            importConferenceEditions();

            System.out.println("Importing papers...");
            importPapers();

            System.out.println("Importing writer properties...");
            importWriterProp();

            System.out.println("Importing citations...");
            importCitations();

            System.out.println("Importing paper and volume relation...");
            importPaperVolume();

            System.out.println("Importing paper and proceeding relation...");
            importPaperProceeding();

            System.out.println("Importing reviews...");
            importReviews();

            System.out.println("Done.");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


    }


    private static void importCompanies() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String companiesFilePath = "src/main/resources/companies.csv";
        List<String[]> lines = CSV.read(companiesFilePath, separator);

        OntClass companyClass = ontModel.getOntClass(dbo + "Company");
        OntClass cityClass = ontModel.getOntClass(dbo + "City");
        ObjectProperty cityProp = ontModel.getObjectProperty(dbo + "city");
        DatatypeProperty nameProp = ontModel.getDatatypeProperty(dbo + "name");

        for (String[] line : lines) {
            String companyName = line[0];
            String cityName = line[1];
            Utils.addCompany(Utils.cleanURI(companyName));

            // Inserting the company
            Individual company = companyClass.createIndividual(ns + Utils.cleanURI(companyName));
            Literal literalName = ontModel.createTypedLiteral(companyName, RDFLangString.rdfLangString);
            ontModel.add(company, nameProp, literalName);

            // Inserting the city
            Resource city;
            if (!Utils.hasCity(cityName)) {
                String cityURI = Utils.getClosestCity(cityName);
                if (cityURI.contains("dbpedia")) {
                    ontModel.read(cityURI);
                    city = ontModel.getResource(cityURI);
                } else {
                    city = cityClass.createIndividual(cityURI);
                    Literal literalCityName = ontModel.createTypedLiteral(cityName, RDFLangString.rdfLangString);
                    ontModel.add(city, nameProp, literalCityName);
                }
            }
            else {
                city = ontModel.getResource(Utils.getCityURI(cityName));
            }

            // Create the relation between company and city
            ontModel.add(company, cityProp, city);

        }
    }

    private static void importUniversities() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String companiesFilePath = "src/main/resources/world-universities_out.csv";
        List<String[]> lines = CSV.read(companiesFilePath, separator);

        OntClass universityClass = ontModel.getOntClass(dbo + "University");
        OntClass cityClass = ontModel.getOntClass(dbo + "City");
        ObjectProperty cityProp = ontModel.getObjectProperty(dbo + "city");
        DatatypeProperty nameProp = ontModel.getDatatypeProperty(dbo + "name");
        DatatypeProperty urlProp = ontModel.getDatatypeProperty(ontNS + "url");

        for (String[] line : lines) {
            String universityName = line[0];
            String cityName = line[1];
            String url = line[2];
            Utils.addUniversity(Utils.cleanURI(universityName));

            // Inserting the university
            Individual university = universityClass.createIndividual(ns + Utils.cleanURI(universityName));
            // Inserting the name
            Literal literalName = ontModel.createTypedLiteral(universityName, RDFLangString.rdfLangString);
            ontModel.add(university, nameProp, literalName);
            // Inserting the URL
            Literal literalURL = ontModel.createTypedLiteral(url, XSDDatatype.XSDanyURI);
            ontModel.add(university, urlProp, literalURL);

            // Inserting the city
            Resource city;
            if (!Utils.hasCity(cityName)) {
                String cityURI = Utils.getClosestCity(cityName);
                if (cityURI.contains("dbpedia")) {
                    ontModel.read(cityURI);
                    city = ontModel.getResource(cityURI);
                } else {
                    city = cityClass.createIndividual(cityURI);
                    Literal literalCityName = ontModel.createTypedLiteral(cityName, RDFLangString.rdfLangString);
                    ontModel.add(city, nameProp, literalCityName);
                }
            }
            else {
                city = ontModel.getResource(Utils.getCityURI(cityName));
            }
            // Create the relation between university and city
            ontModel.add(university, cityProp, city);

        }
    }

    private static void importWriters() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String authorFilePath = "src/main/resources/authors.csv";
        List<String[]> lines = CSV.read(authorFilePath, separator);

        OntClass writerClass = ontModel.getOntClass(ontNS + "ScientificWriter");
        ObjectProperty employerProp = ontModel.getObjectProperty(dbo + "employer");
        DatatypeProperty nameProp = ontModel.getDatatypeProperty(dbo + "name");

        List<String> companies = Utils.getCompanies();
        List<String> universities = Utils.getUniversities();

        for (String[] line : lines) {
            String writerName = line[0];
            Individual writer = writerClass.createIndividual(ns + Utils.cleanURI(writerName));
            Literal literalName = ontModel.createTypedLiteral(writerName, RDFLangString.rdfLangString);
            ontModel.add(writer, nameProp, literalName);

            // add employer
            int choice = Utils.randomInt(0,2);
            Individual organization;
            if (choice == 0) {  // University
                String universityURI = universities.get(Utils.randomInt(0, universities.size()));
                organization = ontModel.getIndividual(ns + universityURI);
            }
            else {  // Company
                String companyURI = companies.get(Utils.randomInt(0, companies.size()));
                organization = ontModel.getIndividual(ns + companyURI);
            }
            ontModel.add(writer, employerProp, organization);

        }

    }

    private static void importJournals () throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String journalsFilePath = "src/main/resources/journals.csv";
        List<String[]> lines = CSV.read(journalsFilePath, separator);

        OntClass openJournalClass = ontModel.getOntClass(ontNS + "OpenAccessJournal");
        OntClass subscribingJournalClass = ontModel.getOntClass(ontNS + "SubscribingJournal");
        OntClass[] journalClasses = {openJournalClass, subscribingJournalClass};

        DatatypeProperty nameProp = ontModel.getDatatypeProperty(dbo + "name");


        for (String[] line : lines) {
            String journalName = line[0];

            Individual journal = journalClasses[Utils.randomInt(0,2)].createIndividual(ns + Utils.cleanURI(journalName));
            Literal literalName = ontModel.createTypedLiteral(journalName, RDFLangString.rdfLangString);
            ontModel.add(journal, nameProp, literalName);
        }
    }

    private static void importConferences () throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String conferencesFilePath = "src/main/resources/conferences.csv";
        List<String[]> lines = CSV.read(conferencesFilePath, separator);

        OntClass conferenceClass = ontModel.getOntClass(ontNS + "Conference");
        OntClass workshopClass = ontModel.getOntClass(ontNS + "Workshop");
        OntClass[] conferenceClasses = {conferenceClass, workshopClass};

        DatatypeProperty nameProp = ontModel.getDatatypeProperty(dbo + "name");

        for (String[] line : lines) {
            String conferenceName = line[0];

            Individual conference = conferenceClasses[Utils.randomInt(0,2)].createIndividual(ns + Utils.cleanURI(conferenceName));
            Literal literalName = ontModel.createTypedLiteral(conferenceName, RDFLangString.rdfLangString);
            ontModel.add(conference, nameProp, literalName);
        }
    }

    private static void importVolumes() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String volumesFilePath = "src/main/resources/volumes.csv";
        List<String[]> lines = CSV.read(volumesFilePath, separator);

        OntClass volumeClass = ontModel.getOntClass(ontNS + "JournalVolume");
        ObjectProperty publisherProp = ontModel.getObjectProperty(ontNS + "journalPublisher");
        DatatypeProperty volumeIDProp = ontModel.getDatatypeProperty(ontNS + "volumeID");


        for (String[] line : lines) {
            String volumeID = Utils.cleanURI(line[0]);
            String journalName = line[0].split("\\|")[0];

            // Adding the volume
            Individual volume = volumeClass.createIndividual(ns + volumeID);
            Literal literalID = ontModel.createTypedLiteral(volumeID, XSDDatatype.XSDstring);
            ontModel.add(volume, volumeIDProp, literalID);

            // Adding the relation between volume and journal
            Individual journal = ontModel.getIndividual(ns + Utils.cleanURI(journalName));
            ontModel.add(volume, publisherProp, journal);

        }
    }

    private static void importEditions() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String editionsFilePath = "src/main/resources/editions.csv";
        List<String[]> lines = CSV.read(editionsFilePath, separator);

        OntClass editionClass = ontModel.getOntClass(ontNS + "Edition");
        OntClass proceedingClass = ontModel.getOntClass(ontNS + "ConferenceProceeding");
        ObjectProperty publisherProp = ontModel.getObjectProperty(ontNS + "conferencePublisher");
        DatatypeProperty editionIDProp = ontModel.getDatatypeProperty(ontNS + "editionID");
        DatatypeProperty proceedingIDProp = ontModel.getDatatypeProperty(ontNS + "proceedingID");


        for (String[] line : lines) {
            String editionID = Utils.cleanURI(line[0]);
            String proceedingID = Utils.makeISBN();
            Utils.putProceeding(editionID, proceedingID);

            // Adding the edition
            Individual edition = editionClass.createIndividual(ns + editionID);
            Literal literalID = ontModel.createTypedLiteral(editionID, XSDDatatype.XSDstring);
            ontModel.add(edition, editionIDProp, literalID);

            // Adding the proceeding
            Individual proceeding = proceedingClass.createIndividual(ns + proceedingID);
            literalID = ontModel.createTypedLiteral(proceedingID, XSDDatatype.XSDstring);
            ontModel.add(proceeding, proceedingIDProp, literalID);

            ontModel.add(proceeding, publisherProp, edition);

        }
    }

    private static void importEditionCities() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String editionCitiesFilePath = "src/main/resources/occurs_in.csv";
        List<String[]> lines = CSV.read(editionCitiesFilePath, separator);

        OntClass cityClass = ontModel.getOntClass(dbo + "City");
        ObjectProperty cityProp = ontModel.getObjectProperty(dbo + "city");
        DatatypeProperty nameProp = ontModel.getDatatypeProperty(dbo + "name");

        for (String[] line : lines) {
            String editionID = Utils.cleanURI(line[0]);
            String cityName = line[1];

            Individual edition = ontModel.getIndividual(ns + editionID);
            Resource city;
            if (!Utils.hasCity(cityName)) {
                String cityURI = Utils.getClosestCity(cityName);
                if (cityURI.contains("dbpedia")) {
                    ontModel.read(cityURI);
                    city = ontModel.getResource(cityURI);
                } else {
                    city = cityClass.createIndividual(cityURI);
                    Literal literalCityName = ontModel.createTypedLiteral(cityName, RDFLangString.rdfLangString);
                    ontModel.add(city, nameProp, literalCityName);
                }
            }
            else {
                city = ontModel.getResource(Utils.getCityURI(cityName));
            }
            ontModel.add(edition, cityProp, city);

        }
    }

    private static void importConferenceEditions() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String confEdFilePath = "src/main/resources/has_an.csv";
        List<String[]> lines = CSV.read(confEdFilePath, separator);

        ObjectProperty editionProp = ontModel.getObjectProperty(ontNS + "edition");
        DatatypeProperty yearProp = ontModel.getDatatypeProperty(ontNS + "year");


        for (String[] line : lines) {
            String conferenceID = Utils.cleanURI(line[0]);
            String year = line[1];
            String editionID = Utils.cleanURI(line[2]);

            Individual conference = ontModel.getIndividual(ns + conferenceID);
            Individual edition = ontModel.getIndividual(ns + editionID);
            ontModel.add(conference, editionProp, edition);

            Literal literalYear = ontModel.createTypedLiteral(year, XSDDatatype.XSDgYear);
            ontModel.add(edition, yearProp, literalYear);

        }
    }

    private static void importYearVolumes() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String yearVolumesFilePath = "src/main/resources/in_year_vol.csv";
        List<String[]> lines = CSV.read(yearVolumesFilePath, separator);

        DatatypeProperty yearProp = ontModel.getDatatypeProperty(ontNS + "year");


        for (String[] line : lines) {
            String volumeID = Utils.cleanURI(line[0]);
            String year = line[1];

            Literal literalYear = ontModel.createTypedLiteral(year, XSDDatatype.XSDgYear);
            Individual volume = ontModel.getIndividual(ns + volumeID);
            ontModel.add(volume, yearProp, literalYear);
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
        OntClass[] paperTypes = {demoPaperClass, shortPaperClass, fullPaperClass, surveyPaperClass};

        DatatypeProperty titleProp = ontModel.getDatatypeProperty(dbo + "title");
        DatatypeProperty paperIDProp = ontModel.getDatatypeProperty(ontNS + "paperID");

        for (String[] line : lines) {
            String paperID = line[0];
            String paperTitle = line[1];

            int randPaper = Utils.randomInt(0, 4);

            Individual paper = paperTypes[randPaper].createIndividual(ns + "paper" + paperID);
            Literal literalTitle = ontModel.createTypedLiteral(paperTitle, XSDDatatype.XSDstring);
            Literal literalID = ontModel.createTypedLiteral(paperID, XSDDatatype.XSDstring);

            ontModel.add(paper, titleProp, literalTitle);
            ontModel.add(paper, paperIDProp, literalID);
        }
    }

    private static void importCitations() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String citesProFilePath = "src/main/resources/cites.csv";
        List<String[]> lines = CSV.read(citesProFilePath, separator);

        ObjectProperty citesProp = ontModel.getObjectProperty(ontNS + "cites");

        for (String[] line : lines) {
            String paperID_1 = "paper" + line[0];
            String paperID_2 = "paper" + line[1];

            Individual paper_1 = ontModel.getIndividual(ns + paperID_1);
            Individual paper_2 = ontModel.getIndividual(ns + paperID_2);

            ontModel.add(paper_1, citesProp, paper_2);
        }
    }

    private static void importPaperVolume() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String volumePropFilePath = "src/main/resources/vol_contains.csv";
        List<String[]> lines = CSV.read(volumePropFilePath, separator);

        ObjectProperty volumeProp = ontModel.getObjectProperty(ontNS + "volume");

        for (String[] line : lines) {
            String volumeID = Utils.cleanURI(line[0]);
            String paperID = "paper" + line[2];

            Individual volume = ontModel.getIndividual(ns + volumeID);
            Individual paper = ontModel.getIndividual(ns + paperID);

            ontModel.add(paper, volumeProp, volume);
        }
    }

    private static void importPaperProceeding() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String paperProceedFilePath = "src/main/resources/proc_contains.csv";
        List<String[]> lines = CSV.read(paperProceedFilePath, separator);

        ObjectProperty proceedingProp = ontModel.getObjectProperty(ontNS + "proceeding");

        for (String[] line : lines) {
            String editionID = Utils.cleanURI(line[0]);
            String paperID = "paper" + line[2];
            String proceedingID = Utils.getProceeding(editionID);

            Individual proceeding = ontModel.getIndividual(ns + proceedingID);
            Individual paper = ontModel.getIndividual(ns + paperID);
            ontModel.add(paper, proceedingProp, proceeding);

        }
    }

    private static void importWriterProp() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String writerProFilePath = "src/main/resources/writes.csv";
        List<String[]> lines = CSV.read(writerProFilePath, separator);

        ObjectProperty writerProp = ontModel.getObjectProperty(dbo + "writer");

        for (String[] line : lines) {
            String writerName = Utils.cleanURI(line[0]);
            String paperID = "paper" + line[2];

            Individual paper = ontModel.getIndividual(ns + paperID);
            Individual writer = ontModel.getIndividual(ns + writerName);

            ontModel.add(paper, writerProp, writer);
        }
    }

    private static void importReviews() throws IOException {
        OntModel ontModel = ResearchOntModel.getInstance();

        String reviewsFilePath = "src/main/resources/reviews_summary.csv";
        List<String[]> lines = CSV.read(reviewsFilePath, separator);

        OntClass reviewerClass = ontModel.getOntClass(ontNS + "Reviewer");
        OntClass reviewClass = ontModel.getOntClass(ontNS + "Review");
        ObjectProperty writerProp = ontModel.getObjectProperty(dbo + "writer");
        ObjectProperty aboutProp = ontModel.getObjectProperty(ontNS + "about");
        DatatypeProperty decisionProp = ontModel.getDatatypeProperty(ontNS + "decision");
        DatatypeProperty descriptionProp = ontModel.getDatatypeProperty(ontNS + "description");


        for (String[] line : lines) {
            String reviewerName = line[0];
            String paperID = "paper" + line[1];
            String decision = line[2];
            String description = line[3];

            Individual reviewer = ontModel.getIndividual(ns + Utils.cleanURI(reviewerName));
            reviewer.addOntClass(reviewerClass);
            // ID of the review would be the concatenation of paper and author
            Individual review = reviewClass.createIndividual(ns + paperID + Utils.cleanURI(reviewerName));
            ontModel.add(review, writerProp, reviewer);

            // Link the review to the paper
            Individual paper = ontModel.getIndividual(ns + paperID);
            ontModel.add(review, aboutProp, paper);

            // Add data to the review
            Literal literalDecision = ontModel.createTypedLiteral(decision, XSDDatatype.XSDstring);
            Literal literalDescription = ontModel.createTypedLiteral(description, XSDDatatype.XSDstring);
            ontModel.add(review, decisionProp, literalDecision);
            ontModel.add(review, descriptionProp, literalDescription);

        }
    }
}
