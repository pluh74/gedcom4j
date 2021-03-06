/*
 * Copyright (c) 2009-2016 Matthew R. Harrah
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.gedcom4j.model.thirdpartyadapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.CustomFact;
import org.gedcom4j.model.Family;
import org.gedcom4j.model.FamilyEvent;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualAttribute;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.Multimedia;
import org.gedcom4j.model.NoteStructure;
import org.gedcom4j.model.PersonalName;
import org.gedcom4j.model.Place;
import org.gedcom4j.model.Repository;
import org.gedcom4j.model.Source;
import org.gedcom4j.model.StringWithCustomFacts;
import org.gedcom4j.model.Submitter;
import org.gedcom4j.model.enumerations.FamilyEventType;
import org.gedcom4j.model.enumerations.IndividualAttributeType;
import org.gedcom4j.model.enumerations.IndividualEventType;
import org.gedcom4j.parser.GedcomParser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link FamilyHistorianAdapter}
 * 
 * @author frizbog
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessivePublicCount", "PMD.GodClass", "PMD.ExcessiveClassLength",
        "PMD.ExcessiveImports" })
public class FamilyHistorianAdapterTest {

    /**
     * The test gedcom we read from the sample file that HAS custom tags
     */
    private Gedcom gedcomWithCustomTags;

    /**
     * The main person in the sample file with most of the custom facts
     */
    private Individual tom;

    /**
     * The class under test
     */
    private final FamilyHistorianAdapter fha = new FamilyHistorianAdapter();

    /**
     * The test gedcom we read from the sample file that DOES NOT have custom tags, for negative testing
     */
    private Gedcom gedcomWithoutCustomTags;

    /**
     * Sets up each test
     * 
     * @throws GedcomParserException
     *             if the sample file cannot be parsed
     * @throws IOException
     *             if the sample file cannot be read
     */
    @Before
    public void setUp() throws IOException, GedcomParserException {
        GedcomParser gp = new GedcomParser();
        gp.load("sample/famhistcustomtags.ged");
        gedcomWithCustomTags = gp.getGedcom();
        assertNotNull(gedcomWithCustomTags);

        gp = new GedcomParser();
        gp.load("sample/famhistnocustomtags.ged");
        gedcomWithoutCustomTags = gp.getGedcom();
        assertNotNull(gedcomWithoutCustomTags);

        assertNotSame(gedcomWithCustomTags, gedcomWithoutCustomTags);

        tom = gedcomWithCustomTags.getIndividuals().get("@I1@");
        assertNotNull(tom);
    }

    /**
     * Test for {@link FamilyHistorianAdapter#addNamedList(Gedcom, CustomFact)}
     */
    @Test
    public void testAddNamedList() {
        List<CustomFact> lists = fha.getNamedLists(gedcomWithoutCustomTags);
        assertNotNull(lists);
        assertEquals(0, lists.size());

        CustomFact namedList = fha.newNamedList("Frying Pans");
        fha.addNamedList(gedcomWithoutCustomTags, namedList);

        lists = fha.getNamedLists(gedcomWithoutCustomTags);
        assertNotNull(lists);
        assertEquals(1, lists.size());

        lists = fha.getNamedList(gedcomWithoutCustomTags, "Frying Pans");
        assertNotNull(lists);
        assertEquals(1, lists.size());
    }

    /**
     * Test for {@link FamilyHistorianAdapter#removeUnrelatedWitnesses(IndividualEvent)},
     * {@link FamilyHistorianAdapter#newUnrelatedWitness(String)}, and
     * {@link FamilyHistorianAdapter#addUnrelatedWitness(IndividualEvent, CustomFact)}
     */
    @Test
    public void testClearAddUnrelatedWitnesses() {
        IndividualEvent birth = tom.getEventsOfType(IndividualEventType.BIRTH).get(0);
        List<CustomFact> witnessNames = fha.getUnrelatedWitnesses(birth);
        assertNotNull(witnessNames);
        assertEquals(1, witnessNames.size());

        fha.removeUnrelatedWitnesses(birth);
        witnessNames = fha.getUnrelatedWitnesses(birth);
        assertNotNull(witnessNames);
        assertEquals(0, witnessNames.size());

        CustomFact uw = fha.newUnrelatedWitness("Robert Roberts");
        fha.addUnrelatedWitness(birth, uw);

        witnessNames = fha.getUnrelatedWitnesses(birth);
        assertNotNull(witnessNames);
        assertEquals("Robert Roberts", witnessNames.get(0).getDescription().getValue());
    }

    /**
     * Test for {@link FamilyHistorianAdapter#removeWitnessReferences(IndividualEvent)},
     * {@link FamilyHistorianAdapter#newUnrelatedWitness(String)}, and
     * {@link FamilyHistorianAdapter#addUnrelatedWitness(IndividualEvent, CustomFact)}
     */
    @Test
    public void testClearAddWitnessReferences() {

        IndividualEvent birth = tom.getEventsOfType(IndividualEventType.BIRTH).get(0);
        List<CustomFact> witnessRefs = fha.getWitnessReferences(birth);
        assertNotNull(witnessRefs);
        assertEquals(1, witnessRefs.size());

        fha.removeWitnessReferences(birth);
        witnessRefs = fha.getWitnessReferences(birth);
        assertNotNull(witnessRefs);
        assertEquals(0, witnessRefs.size());

        Individual theresa = gedcomWithCustomTags.getIndividuals().get("@I3@");
        CustomFact wr = fha.newWitnessReference(theresa);
        fha.addWitnessReference(gedcomWithCustomTags, birth, wr);

        witnessRefs = fha.getWitnessReferences(birth);
        assertNotNull(witnessRefs);
        assertEquals("@I3@", witnessRefs.get(0).getDescription().getValue());
    }

    /**
     * Test for DNA markers
     */
    @Test
    public void testDnaMarkers() {
        List<CustomFact> dnaMarkers = fha.getDnaMarkers(tom);
        assertNotNull(dnaMarkers);
        assertEquals(1, dnaMarkers.size());
        CustomFact dnaMarker = dnaMarkers.get(0);
        assertEquals("_ATTR", dnaMarker.getTag());
        assertEquals("xxx", dnaMarker.getDescription().getValue());
        assertEquals("DNA Markers", dnaMarker.getType().getValue());

        fha.removeDnaMarkers(tom);
        dnaMarkers = fha.getDnaMarkers(tom);
        assertNotNull(dnaMarkers);
        assertEquals(0, dnaMarkers.size());

        CustomFact n = fha.newDnaMarker("frying pan");
        fha.addDnaMarker(tom, n);

        dnaMarkers = fha.getDnaMarkers(tom);
        assertNotNull(dnaMarkers);
        assertEquals(1, dnaMarkers.size());
        dnaMarker = dnaMarkers.get(0);
        assertEquals("_ATTR", dnaMarker.getTag());
        assertEquals("frying pan", dnaMarker.getDescription().getValue());
        assertEquals("DNA Markers", dnaMarker.getType().getValue());
    }

    /**
     * Test for Elected
     */
    @Test
    public void testElected() {
        List<CustomFact> electeds = fha.getElected(tom);
        assertNotNull(electeds);
        assertEquals(1, electeds.size());
        CustomFact elected = electeds.get(0);
        assertEquals("_ATTR", elected.getTag());
        assertEquals("grand poobah", elected.getDescription().getValue());
        assertEquals("Elected", elected.getType().getValue());

        fha.removeElected(tom);
        electeds = fha.getElected(tom);
        assertNotNull(electeds);
        assertEquals(0, electeds.size());

        CustomFact n = fha.newElected("frying pan");
        fha.addElected(tom, n);

        electeds = fha.getElected(tom);
        assertNotNull(electeds);
        assertEquals(1, electeds.size());
        elected = electeds.get(0);
        assertEquals("_ATTR", elected.getTag());
        assertEquals("frying pan", elected.getDescription().getValue());
        assertEquals("Elected", elected.getType().getValue());
    }

    /**
     * Test for Employment
     */
    @Test
    public void testEmployment() {
        List<CustomFact> employments = fha.getEmployment(tom);
        assertNotNull(employments);
        assertEquals(1, employments.size());
        CustomFact employment = employments.get(0);
        assertEquals("_ATTR", employment.getTag());
        assertEquals("waiter", employment.getDescription().getValue());
        assertEquals("Employment", employment.getType().getValue());

        fha.removeEmployment(tom);
        employments = fha.getEmployment(tom);
        assertNotNull(employments);
        assertEquals(0, employments.size());

        CustomFact n = fha.newEmployment("frying pan");
        fha.addEmployment(tom, n);

        employments = fha.getEmployment(tom);
        assertNotNull(employments);
        assertEquals(1, employments.size());
        employment = employments.get(0);
        assertEquals("_ATTR", employment.getTag());
        assertEquals("frying pan", employment.getDescription().getValue());
        assertEquals("Employment", employment.getType().getValue());
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getFamilyStatus(Family)} and
     * {@link FamilyHistorianAdapter#setFamilyStatus(Family, String)}
     */
    @Test
    public void testFamilyStatusNegative() {
        Family family = gedcomWithoutCustomTags.getFamilies().get("@F1@");
        assertNull(fha.getFamilyStatus(family));
        fha.setFamilyStatus(family, "Common Law");
        assertEquals("Common Law", fha.getFamilyStatus(family));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getFamilyStatus(Family)} and
     * {@link FamilyHistorianAdapter#setFamilyStatus(Family, String)}
     */
    @Test
    public void testFamilyStatusPositive() {
        Family family = gedcomWithCustomTags.getFamilies().get("@F1@");
        assertEquals("Never Married", fha.getFamilyStatus(family));
        fha.setFamilyStatus(family, null);
        assertNull(fha.getFamilyStatus(family));
        fha.setFamilyStatus(family, "Common Law");
        assertEquals("Common Law", fha.getFamilyStatus(family));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#addEmail(org.gedcom4j.model.AbstractEvent, String)},
     * {@link FamilyHistorianAdapter#getEmails(org.gedcom4j.model.AbstractEvent)}, and
     * {@link FamilyHistorianAdapter#removeEmails(org.gedcom4j.model.AbstractEvent)}
     */
    @Test
    public void testGetAddRemoveEmailsToAttribute() {
        IndividualAttribute ia = tom.getAttributesOfType(IndividualAttributeType.FACT).get(0);
        assertEquals("Tomato", ia.getSubType().getValue());
        List<String> emails = fha.getEmails(ia);
        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals("info@internet.com", emails.get(0));

        fha.removeEmails(ia);
        emails = fha.getEmails(ia);
        assertNotNull(emails);
        assertEquals(0, emails.size());

        assertNotNull(fha.addEmail(ia, "matt@gedcom4j.org"));

        emails = fha.getEmails(ia);
        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals("matt@gedcom4j.org", emails.get(0));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#addEmail(org.gedcom4j.model.AbstractEvent, String)},
     * {@link FamilyHistorianAdapter#getEmails(org.gedcom4j.model.AbstractEvent)}, and
     * {@link FamilyHistorianAdapter#removeEmails(org.gedcom4j.model.AbstractEvent)}
     */
    @Test
    public void testGetAddRemoveEmailsToCustomFact() {
        List<CustomFact> dnaMarkers = fha.getDnaMarkers(tom);
        assertNotNull(dnaMarkers);
        assertEquals(1, dnaMarkers.size());

        CustomFact dnaMarker = dnaMarkers.get(0);

        List<String> emails = fha.getEmails(dnaMarker);
        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals("info@internet.com", emails.get(0));

        fha.removeEmails(dnaMarker);
        emails = fha.getEmails(dnaMarker);
        assertNotNull(emails);
        assertEquals(0, emails.size());

        assertNotNull(fha.addEmail(dnaMarker, "matt@gedcom4j.org"));

        emails = fha.getEmails(dnaMarker);
        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals("matt@gedcom4j.org", emails.get(0));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#addEmail(org.gedcom4j.model.AbstractEvent, String)},
     * {@link FamilyHistorianAdapter#getEmails(org.gedcom4j.model.AbstractEvent)}, and
     * {@link FamilyHistorianAdapter#removeEmails(org.gedcom4j.model.AbstractEvent)}
     */
    @Test
    public void testGetAddRemoveEmailsToEvent() {
        IndividualEvent birth = tom.getEventsOfType(IndividualEventType.BIRTH).get(0);
        List<String> emails = fha.getEmails(birth);
        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals("info@internet.com", emails.get(0));

        fha.removeEmails(birth);
        emails = fha.getEmails(birth);
        assertNotNull(emails);
        assertEquals(0, emails.size());

        assertNotNull(fha.addEmail(birth, "matt@gedcom4j.org"));

        emails = fha.getEmails(birth);
        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals("matt@gedcom4j.org", emails.get(0));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#addEmail(Repository, String)}, {@link FamilyHistorianAdapter#getEmails(Repository)},
     * and {@link FamilyHistorianAdapter#removeEmails(Repository)}
     */
    @Test
    public void testGetAddRemoveEmailsToRepository() {

        Repository repository = gedcomWithCustomTags.getRepositories().get("@R0000@");

        List<String> emails = fha.getEmails(repository);
        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals("info@internet.com", emails.get(0));

        fha.removeEmails(repository);
        emails = fha.getEmails(repository);
        assertNotNull(emails);
        assertEquals(0, emails.size());

        assertNotNull(fha.addEmail(repository, "matt@gedcom4j.org"));

        emails = fha.getEmails(repository);
        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals("matt@gedcom4j.org", emails.get(0));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#addEmail(Submitter, String)}, {@link FamilyHistorianAdapter#getEmails(Submitter)}, and
     * {@link FamilyHistorianAdapter#removeEmails(Submitter)}
     */
    @Test
    public void testGetAddRemoveEmailsToSubmitter() {

        Submitter submitter = gedcomWithCustomTags.getSubmitters().get("@SUBM001@");

        List<String> emails = fha.getEmails(submitter);
        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals("info@internet.com", emails.get(0));

        fha.removeEmails(submitter);
        emails = fha.getEmails(submitter);
        assertNotNull(emails);
        assertEquals(0, emails.size());

        assertNotNull(fha.addEmail(submitter, "matt@gedcom4j.org"));

        emails = fha.getEmails(submitter);
        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals("matt@gedcom4j.org", emails.get(0));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#addWebUrl(org.gedcom4j.model.AbstractEvent, String)},
     * {@link FamilyHistorianAdapter#getWebUrls(org.gedcom4j.model.AbstractEvent)}, and
     * {@link FamilyHistorianAdapter#removeWebUrls(org.gedcom4j.model.AbstractEvent)}
     */
    @Test
    public void testGetAddRemoveWebUrlsToAttribute() {
        IndividualAttribute ia = tom.getAttributesOfType(IndividualAttributeType.FACT).get(0);
        assertEquals("Tomato", ia.getSubType().getValue());
        List<String> faxes = fha.getWebUrls(ia);
        assertNotNull(faxes);
        assertEquals(1, faxes.size());
        assertEquals("http://www.internet.com", faxes.get(0));

        fha.removeWebUrls(ia);
        faxes = fha.getWebUrls(ia);
        assertNotNull(faxes);
        assertEquals(0, faxes.size());

        assertNotNull(fha.addWebUrl(ia, "http://gedcom4j.org"));

        faxes = fha.getWebUrls(ia);
        assertNotNull(faxes);
        assertEquals(1, faxes.size());
        assertEquals("http://gedcom4j.org", faxes.get(0));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#addWebUrl(org.gedcom4j.model.AbstractEvent, String)},
     * {@link FamilyHistorianAdapter#getWebUrls(org.gedcom4j.model.AbstractEvent)}, and
     * {@link FamilyHistorianAdapter#removeWebUrls(org.gedcom4j.model.AbstractEvent)}
     */
    @Test
    public void testGetAddRemoveWebUrlsToCustomFact() {
        List<CustomFact> dnaMarkers = fha.getDnaMarkers(tom);
        assertNotNull(dnaMarkers);
        assertEquals(1, dnaMarkers.size());

        CustomFact dnaMarker = dnaMarkers.get(0);

        List<String> faxes = fha.getWebUrls(dnaMarker);
        assertNotNull(faxes);
        assertEquals(1, faxes.size());
        assertEquals("http://www.internet.com", faxes.get(0));

        fha.removeWebUrls(dnaMarker);
        faxes = fha.getWebUrls(dnaMarker);
        assertNotNull(faxes);
        assertEquals(0, faxes.size());

        assertNotNull(fha.addWebUrl(dnaMarker, "http://gedcom4j.org"));

        faxes = fha.getWebUrls(dnaMarker);
        assertNotNull(faxes);
        assertEquals(1, faxes.size());
        assertEquals("http://gedcom4j.org", faxes.get(0));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#addWebUrl(org.gedcom4j.model.AbstractEvent, String)},
     * {@link FamilyHistorianAdapter#getWebUrls(org.gedcom4j.model.AbstractEvent)}, and
     * {@link FamilyHistorianAdapter#removeWebUrls(org.gedcom4j.model.AbstractEvent)}
     */
    @Test
    public void testGetAddRemoveWebUrlsToEvent() {
        IndividualEvent birth = tom.getEventsOfType(IndividualEventType.BIRTH).get(0);
        List<String> faxes = fha.getWebUrls(birth);
        assertNotNull(faxes);
        assertEquals(1, faxes.size());
        assertEquals("http://www.internet.com", faxes.get(0));

        fha.removeWebUrls(birth);
        faxes = fha.getWebUrls(birth);
        assertNotNull(faxes);
        assertEquals(0, faxes.size());

        assertNotNull(fha.addWebUrl(birth, "http://gedcom4j.org"));

        faxes = fha.getWebUrls(birth);
        assertNotNull(faxes);
        assertEquals(1, faxes.size());
        assertEquals("http://gedcom4j.org", faxes.get(0));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#addWebUrl(Repository, String)}, {@link FamilyHistorianAdapter#getWebUrls(Repository)},
     * and {@link FamilyHistorianAdapter#removeWebUrls(Repository)}
     */
    @Test
    public void testGetAddRemoveWebUrlsToRepository() {

        Repository repository = gedcomWithCustomTags.getRepositories().get("@R0000@");

        List<String> faxes = fha.getWebUrls(repository);
        assertNotNull(faxes);
        assertEquals(1, faxes.size());
        assertEquals("http://www.internet.com", faxes.get(0));

        fha.removeWebUrls(repository);
        faxes = fha.getWebUrls(repository);
        assertNotNull(faxes);
        assertEquals(0, faxes.size());

        assertNotNull(fha.addWebUrl(repository, "http://gedcom4j.org"));

        faxes = fha.getWebUrls(repository);
        assertNotNull(faxes);
        assertEquals(1, faxes.size());
        assertEquals("http://gedcom4j.org", faxes.get(0));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#addWebUrl(Submitter, String)}, {@link FamilyHistorianAdapter#getWebUrls(Submitter)},
     * and {@link FamilyHistorianAdapter#removeWebUrls(Submitter)}
     */
    @Test
    public void testGetAddRemoveWebUrlsToSubmitter() {

        Submitter submitter = gedcomWithCustomTags.getSubmitters().get("@SUBM001@");

        List<String> faxes = fha.getWebUrls(submitter);
        assertNotNull(faxes);
        assertEquals(1, faxes.size());
        assertEquals("http://www.internet.com", faxes.get(0));

        fha.removeWebUrls(submitter);
        faxes = fha.getWebUrls(submitter);
        assertNotNull(faxes);
        assertEquals(0, faxes.size());

        assertNotNull(fha.addWebUrl(submitter, "http://gedcom4j.org"));

        faxes = fha.getWebUrls(submitter);
        assertNotNull(faxes);
        assertEquals(1, faxes.size());
        assertEquals("http://gedcom4j.org", faxes.get(0));
    }

    /**
     * Positive test for {@link FamilyHistorianAdapter#getNamedList(Gedcom, String)}
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetNamedListImmutable() {
        List<CustomFact> lists = fha.getNamedList(gedcomWithCustomTags, "Key Individuals");
        assertNotNull(lists);
        assertEquals(1, lists.size());
        lists.clear();
    }

    /**
     * Negative test for {@link FamilyHistorianAdapter#getNamedList(Gedcom, String)}
     */
    @Test
    public void testGetNamedListNegative() {
        List<CustomFact> lists = fha.getNamedList(gedcomWithoutCustomTags, "Key Individuals");
        assertNotNull(lists);
        assertEquals(0, lists.size());

        lists = fha.getNamedList(gedcomWithCustomTags, "My Favorite Frying Pans");
        assertNotNull(lists);
        assertEquals(0, lists.size());
    }

    /**
     * Positive test for {@link FamilyHistorianAdapter#getNamedList(Gedcom, String)}
     */
    @Test
    public void testGetNamedListPositive() {
        List<CustomFact> lists = fha.getNamedList(gedcomWithCustomTags, "Key Individuals");
        assertNotNull(lists);
        assertEquals(1, lists.size());
        assertEquals("Key Individuals", lists.get(0).getDescription().getValue());
    }

    /**
     * Positive test for {@link FamilyHistorianAdapter#getNamedLists(Gedcom)}
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetNamedListsImmutable() {
        List<CustomFact> lists = fha.getNamedLists(gedcomWithCustomTags);
        assertNotNull(lists);
        assertEquals(1, lists.size());
        lists.clear();
    }

    /**
     * Negative test for {@link FamilyHistorianAdapter#getNamedLists(Gedcom)}
     */
    @Test
    public void testGetNamedListsNegative() {
        List<CustomFact> lists = fha.getNamedLists(gedcomWithoutCustomTags);
        assertNotNull(lists);
        assertEquals(0, lists.size());
    }

    /**
     * Positive test for {@link FamilyHistorianAdapter#getNamedLists(Gedcom)}
     */
    @Test
    public void testGetNamedListsPositive() {
        List<CustomFact> lists = fha.getNamedLists(gedcomWithCustomTags);
        assertNotNull(lists);
        assertEquals(1, lists.size());
        assertEquals("Key Individuals", lists.get(0).getDescription().getValue());
    }

    /**
     * Positive test for {@link FamilyHistorianAdapter#getRootIndividual(Gedcom)}
     */
    @Test
    public void testGetRootIndividualPositive() {
        Individual individual = fha.getRootIndividual(gedcomWithCustomTags);
        assertNotNull(individual);
        assertEquals(tom, individual);
        assertSame(tom, individual);
    }

    /**
     * Test {@link FamilyHistorianAdapter#getFlags(Individual)} and {@link FamilyHistorianAdapter#setFlags(Individual, CustomFact)}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetSetFlagsNegative() {
        CustomFact flags = fha.getFlags(tom);
        assertNotNull(flags);
        assertEquals(2, flags.getCustomFacts().size());

        CustomFact newFlags = fha.newNamedList("This is intentionally wrong!");
        assertNotNull(newFlags);
        fha.setFlags(tom, newFlags);
    }

    /**
     * Test {@link FamilyHistorianAdapter#getFlags(Individual)} and {@link FamilyHistorianAdapter#setFlags(Individual, CustomFact)}
     */
    @Test
    public void testGetSetFlagsPositive() {
        CustomFact flags = fha.getFlags(tom);
        assertNotNull(flags);
        assertEquals(2, flags.getCustomFacts().size());

        CustomFact newFlags = fha.newFlags();
        assertNotNull(newFlags);
        fha.setFlags(tom, newFlags);

        flags = fha.getFlags(tom);
        assertNotNull(flags);
        assertNull(flags.getCustomFacts());
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getNameUsed(PersonalName)} and
     * {@link FamilyHistorianAdapter#setNameUsed(PersonalName, String)}
     */
    @Test
    public void testGetSetNameUsedNegative() {
        PersonalName pn = new PersonalName();
        List<CustomFact> nameUsed = fha.getNameUsed(pn);
        assertNotNull(nameUsed);
        assertEquals(0, nameUsed.size());
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getNameUsed(PersonalName)} and
     * {@link FamilyHistorianAdapter#setNameUsed(PersonalName, String)}
     */
    @Test
    public void testGetSetNameUsedPositive() {
        PersonalName pn = tom.getNames().get(0);
        assertNotNull(pn);
        List<CustomFact> nameUsed = fha.getNameUsed(pn);
        assertNotNull(nameUsed);
        assertEquals(1, nameUsed.size());
        assertEquals("Tony", nameUsed.get(0).getDescription().getValue());

        fha.setNameUsed(pn, "Billy Jo Bob");
        nameUsed = fha.getNameUsed(pn);
        assertEquals("Billy Jo Bob", nameUsed.get(0).getDescription().getValue());
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getOtherPlaceName(IndividualEvent)} and
     * {@link FamilyHistorianAdapter#setOtherPlaceName(IndividualEvent, String)}
     */
    @Test
    public void testGetSetOtherPlaceNamePositive() {
        Place here = new Place();
        here.setPlaceName("Earth");

        IndividualEvent ie = new IndividualEvent();
        ie.setType(IndividualEventType.IMMIGRATION);
        ie.setPlace(here);

        assertNull(fha.getOtherPlaceName(ie));

        fha.setOtherPlaceName(ie, "Cete Alpha V");
        assertEquals("Cete Alpha V", fha.getOtherPlaceName(ie));

        fha.setOtherPlaceName(ie, null);
        assertNull(fha.getOtherPlaceName(ie));

    }

    /**
     * Test for {@link FamilyHistorianAdapter#getFactSetSentenceTemplate(org.gedcom4j.model.CustomFact)} and
     * {@link FamilyHistorianAdapter#setFactSetSentenceTemplate(org.gedcom4j.model.CustomFact, String)}
     */
    @Test
    public void testGetSetSentenceCustomFact() {
        CustomFact cf = new CustomFact("_ATTR");
        cf.setType(new StringWithCustomFacts("Victory"));

        assertNull(fha.getFactSetSentenceTemplate(cf));

        fha.setFactSetSentenceTemplate(cf, "The quick brown fox jumps over the lazy dog.");
        assertEquals("The quick brown fox jumps over the lazy dog.", fha.getFactSetSentenceTemplate(cf));

        fha.setFactSetSentenceTemplate(cf, null);
        assertNull(fha.getFactSetSentenceTemplate(cf));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getFactSetSentenceTemplate(org.gedcom4j.model.AbstractEvent)} and
     * {@link FamilyHistorianAdapter#setFactSetSentenceTemplate(org.gedcom4j.model.AbstractEvent, String)}
     */
    @Test
    public void testGetSetSentenceFamilyEvent() {
        FamilyEvent fe = new FamilyEvent();
        fe.setType(FamilyEventType.EVENT);

        assertNull(fha.getFactSetSentenceTemplate(fe));

        fha.setFactSetSentenceTemplate(fe, "The quick brown fox jumps over the lazy dog.");
        assertEquals("The quick brown fox jumps over the lazy dog.", fha.getFactSetSentenceTemplate(fe));

        fha.setFactSetSentenceTemplate(fe, null);
        assertNull(fha.getFactSetSentenceTemplate(fe));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getFactSetSentenceTemplate(org.gedcom4j.model.AbstractEvent)} and
     * {@link FamilyHistorianAdapter#setFactSetSentenceTemplate(org.gedcom4j.model.AbstractEvent, String)}
     */
    @Test
    public void testGetSetSentenceIndividualAttribute() {
        IndividualAttribute ia = new IndividualAttribute();
        ia.setType(IndividualAttributeType.FACT);

        assertNull(fha.getFactSetSentenceTemplate(ia));

        fha.setFactSetSentenceTemplate(ia, "The quick brown fox jumps over the lazy dog.");
        assertEquals("The quick brown fox jumps over the lazy dog.", fha.getFactSetSentenceTemplate(ia));

        fha.setFactSetSentenceTemplate(ia, null);
        assertNull(fha.getFactSetSentenceTemplate(ia));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getFactSetSentenceTemplate(org.gedcom4j.model.AbstractEvent)} and
     * {@link FamilyHistorianAdapter#setFactSetSentenceTemplate(org.gedcom4j.model.AbstractEvent, String)}
     */
    @Test
    public void testGetSetSentenceIndividualEvent() {
        IndividualEvent ie = new IndividualEvent();
        ie.setType(IndividualEventType.EVENT);

        assertNull(fha.getFactSetSentenceTemplate(ie));

        fha.setFactSetSentenceTemplate(ie, "The quick brown fox jumps over the lazy dog.");
        assertEquals("The quick brown fox jumps over the lazy dog.", fha.getFactSetSentenceTemplate(ie));

        fha.setFactSetSentenceTemplate(ie, null);
        assertNull(fha.getFactSetSentenceTemplate(ie));
    }

    /**
     * Negative test case for {@link FamilyHistorianAdapter#getUID(Gedcom)} and
     * {@link FamilyHistorianAdapter#setUID(Gedcom, String)}
     */
    @Test
    public void testGetSetUIDNegative() {
        assertNull(fha.getUID(gedcomWithoutCustomTags));
        fha.setUID(gedcomWithoutCustomTags, "FryingPan");
        assertEquals("FryingPan", fha.getUID(gedcomWithoutCustomTags));
    }

    /**
     * Positive test case for {@link FamilyHistorianAdapter#getUID(Gedcom)} and
     * {@link FamilyHistorianAdapter#setUID(Gedcom, String)}
     */
    @Test
    public void testGetSetUIDPositive() {
        assertEquals("{C2159006-9E8E-4149-87DB-36E5F6D08A37}", fha.getUID(gedcomWithCustomTags));
        fha.setUID(gedcomWithCustomTags, "FryingPan");
        assertEquals("FryingPan", fha.getUID(gedcomWithCustomTags));
    }

    /**
     * Negative test case for {@link FamilyHistorianAdapter#getVariantExportFormat(Gedcom)} and
     * {@link FamilyHistorianAdapter#setVariantExportFormat(Gedcom, String)}
     */
    @Test
    public void testGetSetVariantExportFormatNegative() {
        assertNull(fha.getVariantExportFormat(gedcomWithoutCustomTags));
        fha.setVariantExportFormat(gedcomWithoutCustomTags, "FryingPan");
        assertEquals("FryingPan", fha.getVariantExportFormat(gedcomWithoutCustomTags));
    }

    /**
     * Positive test case for {@link FamilyHistorianAdapter#getVariantExportFormat(Gedcom)} and
     * {@link FamilyHistorianAdapter#setVariantExportFormat(Gedcom, String)}
     */
    @Test
    public void testGetSetVariantExportFormatPositive() {
        assertEquals("DSR", fha.getVariantExportFormat(gedcomWithCustomTags));
        fha.setVariantExportFormat(gedcomWithCustomTags, "FryingPan");
        assertEquals("FryingPan", fha.getVariantExportFormat(gedcomWithCustomTags));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getUnrelatedWitnesses(IndividualEvent)}
     */
    @Test
    public void testGetUnrelatedWitnesses() {
        IndividualEvent birth = tom.getEventsOfType(IndividualEventType.BIRTH).get(0);
        List<CustomFact> witnessNames = fha.getUnrelatedWitnesses(birth);
        assertNotNull(witnessNames);
        assertEquals(1, witnessNames.size());
        CustomFact w1 = witnessNames.get(0);
        assertEquals("Wally Witness", w1.getDescription().getValue());
        assertEquals("Disinterested Observer", w1.getCustomFactsWithTag("ROLE").get(0).getDescription().getValue());
        assertEquals("It was sorta awkward", w1.getNoteStructures().get(0).getLines().get(0));
    }

    /**
     * Test that the results from {@link FamilyHistorianAdapter#getUnrelatedWitnesses(IndividualEvent)} are immutable
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetWitnessNamesImmutable() {
        IndividualEvent birth = tom.getEventsOfType(IndividualEventType.BIRTH).get(0);
        List<CustomFact> witnessNames = fha.getUnrelatedWitnesses(birth);
        assertNotNull(witnessNames);
        witnessNames.clear();
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getWitnessReferences(IndividualEvent)}
     */
    @Test
    public void testGetWitnessReferences() {
        IndividualEvent birth = tom.getEventsOfType(IndividualEventType.BIRTH).get(0);
        List<CustomFact> witnessRefs = fha.getWitnessReferences(birth);
        assertNotNull(witnessRefs);
        assertEquals(1, witnessRefs.size());
        CustomFact w1 = witnessRefs.get(0);
        assertEquals("@I3@", w1.getDescription().getValue());
        assertEquals("Birthing Coach", w1.getCustomFactsWithTag("ROLE").get(0).getDescription().getValue());
        assertEquals("Passed out during delivery", w1.getNoteStructures().get(0).getLines().get(0));
    }

    /**
     * Test for Height
     */
    @Test
    public void testHeight() {
        List<CustomFact> heights = fha.getHeight(tom);
        assertNotNull(heights);
        assertEquals(1, heights.size());
        CustomFact height = heights.get(0);
        assertEquals("_ATTR", height.getTag());
        assertEquals("5' 1\"", height.getDescription().getValue());
        assertEquals("Height", height.getType().getValue());

        fha.removeHeight(tom);
        heights = fha.getHeight(tom);
        assertNotNull(heights);
        assertEquals(0, heights.size());

        CustomFact n = fha.newHeight("frying pan");
        fha.addHeight(tom, n);

        heights = fha.getHeight(tom);
        assertNotNull(heights);
        assertEquals(1, heights.size());
        height = heights.get(0);
        assertEquals("_ATTR", height.getTag());
        assertEquals("frying pan", height.getDescription().getValue());
        assertEquals("Height", height.getType().getValue());
    }

    /**
     * Test for Medical Condition
     */
    @Test
    public void testMedicalCondition() {
        List<CustomFact> medicalConditions = fha.getMedicalCondition(tom);
        assertNotNull(medicalConditions);
        assertEquals(1, medicalConditions.size());
        CustomFact medicalCondition = medicalConditions.get(0);
        assertEquals("_ATTR", medicalCondition.getTag());
        assertEquals("Caries", medicalCondition.getDescription().getValue());
        assertEquals("Medical Condition", medicalCondition.getType().getValue());

        fha.removeMedicalCondition(tom);
        medicalConditions = fha.getMedicalCondition(tom);
        assertNotNull(medicalConditions);
        assertEquals(0, medicalConditions.size());

        CustomFact n = fha.newMedicalCondition("frying pan");
        fha.addMedicalCondition(tom, n);

        medicalConditions = fha.getMedicalCondition(tom);
        assertNotNull(medicalConditions);
        assertEquals(1, medicalConditions.size());
        medicalCondition = medicalConditions.get(0);
        assertEquals("_ATTR", medicalCondition.getTag());
        assertEquals("frying pan", medicalCondition.getDescription().getValue());
        assertEquals("Medical Condition", medicalCondition.getType().getValue());
    }

    /**
     * Test for Military ID
     */
    @Test
    public void testMilitaryId() {
        List<CustomFact> militaryIds = fha.getMilitaryId(tom);
        assertNotNull(militaryIds);
        assertEquals(1, militaryIds.size());
        CustomFact militaryId = militaryIds.get(0);
        assertEquals("_ATTR", militaryId.getTag());
        assertEquals("99999", militaryId.getDescription().getValue());
        assertEquals("Military ID", militaryId.getType().getValue());

        fha.removeMilitaryId(tom);
        militaryIds = fha.getMilitaryId(tom);
        assertNotNull(militaryIds);
        assertEquals(0, militaryIds.size());

        CustomFact n = fha.newMilitaryId("frying pan");
        fha.addMilitaryId(tom, n);

        militaryIds = fha.getMilitaryId(tom);
        assertNotNull(militaryIds);
        assertEquals(1, militaryIds.size());
        militaryId = militaryIds.get(0);
        assertEquals("_ATTR", militaryId.getTag());
        assertEquals("frying pan", militaryId.getDescription().getValue());
        assertEquals("Military ID", militaryId.getType().getValue());
    }

    /**
     * Test for Military Service
     */
    @Test
    public void testMilitaryService() {
        List<CustomFact> militaryServices = fha.getMilitaryService(tom);
        assertNotNull(militaryServices);
        assertEquals(1, militaryServices.size());
        CustomFact militaryService = militaryServices.get(0);
        assertEquals("_ATTR", militaryService.getTag());
        assertEquals("Coast Guard", militaryService.getDescription().getValue());
        assertEquals("Military Service", militaryService.getType().getValue());

        fha.removeMilitaryService(tom);
        militaryServices = fha.getMilitaryService(tom);
        assertNotNull(militaryServices);
        assertEquals(0, militaryServices.size());

        CustomFact n = fha.newMilitaryService("frying pan");
        fha.addMilitaryService(tom, n);

        militaryServices = fha.getMilitaryService(tom);
        assertNotNull(militaryServices);
        assertEquals(1, militaryServices.size());
        militaryService = militaryServices.get(0);
        assertEquals("_ATTR", militaryService.getTag());
        assertEquals("frying pan", militaryService.getDescription().getValue());
        assertEquals("Military Service", militaryService.getType().getValue());
    }

    /**
     * Test for Mission
     */
    @Test
    public void testMission() {
        List<CustomFact> missions = fha.getMission(tom);
        assertNotNull(missions);
        assertEquals(1, missions.size());
        CustomFact mission = missions.get(0);
        assertEquals("_ATTR", mission.getTag());
        assertEquals("QWERTY", mission.getDescription().getValue());
        assertEquals("Mission (LDS)", mission.getType().getValue());

        fha.removeMission(tom);
        missions = fha.getMission(tom);
        assertNotNull(missions);
        assertEquals(0, missions.size());

        CustomFact n = fha.newMission("frying pan");
        fha.addMission(tom, n);

        missions = fha.getMission(tom);
        assertNotNull(missions);
        assertEquals(1, missions.size());
        mission = missions.get(0);
        assertEquals("_ATTR", mission.getTag());
        assertEquals("frying pan", mission.getDescription().getValue());
        assertEquals("Mission (LDS)", mission.getType().getValue());
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getMultimediaDate(Multimedia)} and
     * {@link FamilyHistorianAdapter#setMultimediaDate(Multimedia, String)}
     */
    @Test
    public void testMultimediaDate() {
        Multimedia mm = gedcomWithCustomTags.getMultimedia().get("@O1@");
        assertEquals("7 JUL 1977", fha.getMultimediaDate(mm));

        fha.setMultimediaDate(mm, null);
        assertNull(fha.getMultimediaDate(mm));

        fha.setMultimediaDate(mm, "4 JUL 1976");
        assertEquals("4 JUL 1976", fha.getMultimediaDate(mm));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getMultimediaFile(Multimedia)} and
     * {@link FamilyHistorianAdapter#setMultimediaFile(Multimedia, String)}
     */
    @Test
    public void testMultimediaFile() {
        Multimedia mm = gedcomWithCustomTags.getMultimedia().get("@O1@");
        assertEquals("photo.jpg", fha.getMultimediaFile(mm));

        fha.setMultimediaFile(mm, null);
        assertNull(fha.getMultimediaFile(mm));

        fha.setMultimediaFile(mm, "photo.png");
        assertEquals("photo.png", fha.getMultimediaFile(mm));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getMultimediaKeys(Multimedia)} and
     * {@link FamilyHistorianAdapter#setMultimediaKeys(Multimedia, String)}
     */
    @Test
    public void testMultimediaKeys() {
        Multimedia mm = gedcomWithCustomTags.getMultimedia().get("@O1@");
        assertEquals("Picture", fha.getMultimediaKeys(mm));

        fha.setMultimediaKeys(mm, null);
        assertNull(fha.getMultimediaKeys(mm));

        fha.setMultimediaKeys(mm, "Movie");
        assertEquals("Movie", fha.getMultimediaKeys(mm));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getMultimediaNote(Multimedia)} and
     * {@link FamilyHistorianAdapter#setMultimediaNote(Multimedia, String)}
     */
    @Test
    public void testMultimediaNote() {
        Multimedia mm = gedcomWithCustomTags.getMultimedia().get("@O1@");
        assertEquals("Picture is a bit small and may not be him.", fha.getMultimediaNote(mm));

        fha.setMultimediaNote(mm, null);
        assertNull(fha.getMultimediaNote(mm));

        fha.setMultimediaNote(mm, "Black and white");
        assertEquals("Black and white", fha.getMultimediaNote(mm));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getMultimediaNoteArea(NoteStructure)} and
     * {@link FamilyHistorianAdapter#setMultimediaNoteArea(NoteStructure, String)}
     */
    @Test
    public void testMultimediaNoteArea() {
        Multimedia mm = gedcomWithCustomTags.getMultimedia().get("@O1@");
        assertNotNull(mm.getNoteStructures());
        assertEquals(1, mm.getNoteStructures().size());

        NoteStructure n = mm.getNoteStructures().get(0);

        assertEquals("{154,46,243,127}", fha.getMultimediaNoteArea(n));

        fha.setMultimediaNoteArea(n, null);
        assertNull(fha.getMultimediaNoteArea(n));

        fha.setMultimediaNoteArea(n, "Chess");
        assertEquals("Chess", fha.getMultimediaNoteArea(n));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getMultimediaNoteASID(NoteStructure)} and
     * {@link FamilyHistorianAdapter#setMultimediaNoteASID(NoteStructure, String)} (whatever an ASID is...)
     */
    @Test
    public void testMultimediaNoteASID() {
        Multimedia mm = gedcomWithCustomTags.getMultimedia().get("@O1@");
        assertNotNull(mm.getNoteStructures());
        assertEquals(1, mm.getNoteStructures().size());

        NoteStructure n = mm.getNoteStructures().get(0);

        assertEquals("1", fha.getMultimediaNoteASID(n));

        fha.setMultimediaNoteASID(n, null);
        assertNull(fha.getMultimediaNoteASID(n));

        fha.setMultimediaNoteASID(n, "3");
        assertEquals("3", fha.getMultimediaNoteASID(n));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getMultimediaNoteCaption(NoteStructure)} and
     * {@link FamilyHistorianAdapter#setMultimediaNoteCaption(NoteStructure, String)}
     */
    @Test
    public void testMultimediaNoteCaption() {
        Multimedia mm = gedcomWithCustomTags.getMultimedia().get("@O1@");
        assertNotNull(mm.getNoteStructures());
        assertEquals(1, mm.getNoteStructures().size());

        NoteStructure n = mm.getNoteStructures().get(0);

        assertEquals("Y", fha.getMultimediaNoteCaption(n));

        fha.setMultimediaNoteCaption(n, null);
        assertNull(fha.getMultimediaNoteCaption(n));

        fha.setMultimediaNoteCaption(n, "N");
        assertEquals("N", fha.getMultimediaNoteCaption(n));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getMultimediaNoteExclusion(NoteStructure)} and
     * {@link FamilyHistorianAdapter#setMultimediaNoteExclusion(NoteStructure, String)}
     */
    @Test
    public void testMultimediaNoteExclusion() {
        Multimedia mm = gedcomWithCustomTags.getMultimedia().get("@O1@");
        assertNotNull(mm.getNoteStructures());
        assertEquals(1, mm.getNoteStructures().size());

        NoteStructure n = mm.getNoteStructures().get(0);

        assertEquals("ALL", fha.getMultimediaNoteExclusion(n));

        fha.setMultimediaNoteExclusion(n, null);
        assertNull(fha.getMultimediaNoteExclusion(n));

        fha.setMultimediaNoteExclusion(n, "Synchronicity");
        assertEquals("Synchronicity", fha.getMultimediaNoteExclusion(n));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#isEditingEnabled(CustomFact)} and
     * {@link FamilyHistorianAdapter#setEditingEnabled(CustomFact, boolean)}
     */
    @Test
    public void testNamedListEditingEnabled() {
        CustomFact namedList = fha.getNamedList(gedcomWithCustomTags, "Key Individuals").get(0);
        assertTrue(fha.isEditingEnabled(namedList));
        fha.setEditingEnabled(namedList, false);
        assertFalse(fha.isEditingEnabled(namedList));
        fha.setEditingEnabled(namedList, true);
        assertTrue(fha.isEditingEnabled(namedList));
    }

    /**
     * Test for Namesake
     */
    @Test
    public void testNamesake() {
        List<CustomFact> namesakes = fha.getNamesake(tom);
        assertNotNull(namesakes);
        assertEquals(1, namesakes.size());
        CustomFact namesake = namesakes.get(0);
        assertEquals("_ATTR", namesake.getTag());
        assertEquals("Tom Thompson (a complete stranger)", namesake.getDescription().getValue());
        assertEquals("Namesake", namesake.getType().getValue());

        fha.removeNamesake(tom);
        namesakes = fha.getNamesake(tom);
        assertNotNull(namesakes);
        assertEquals(0, namesakes.size());

        CustomFact n = fha.newNamesake("frying pan");
        fha.addNamesake(tom, n);

        namesakes = fha.getNamesake(tom);
        assertNotNull(namesakes);
        assertEquals(1, namesakes.size());
        namesake = namesakes.get(0);
        assertEquals("_ATTR", namesake.getTag());
        assertEquals("frying pan", namesake.getDescription().getValue());
        assertEquals("Namesake", namesake.getType().getValue());
    }

    /**
     * Test {@link FamilyHistorianAdapter#newPlace(String, String)} with a bad xref
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNewPlaceNegativeBadXref() {
        fha.newPlace("bad", "asdf");
    }

    /**
     * Test {@link FamilyHistorianAdapter#newPlace(String, String)} with a blank place name
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNewPlaceNegativeBlankPlaceName() {
        fha.newPlace("@P1@", " ");
    }

    /**
     * Test {@link FamilyHistorianAdapter#newPlace(String, String)} with a null place name
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNewPlaceNegativeNullPlaceName() {
        fha.newPlace("@P1@", null);
    }

    /**
     * Test {@link FamilyHistorianAdapter#newPlace(String, String)} with a null xref
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNewPlaceNegativeNullXref() {
        fha.newPlace(null, "asdf");
    }

    /**
     * Test {@link FamilyHistorianAdapter#newPlace(String, String)} with the place name and xref transposed
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNewPlaceNegativeXrefPlacenameTransposed() {
        fha.newPlace("Virginia", "@P1@");
    }

    /**
     * Test {@link FamilyHistorianAdapter#newPlace(String, String)} with good parameters
     */
    @Test
    public void testNewPlacePositive() {
        assertNotNull(fha.newPlace("@P1@", "Florida"));
    }

    /**
     * Test for Ordinances
     */
    @Test
    public void testOrdinances() {
        List<CustomFact> ordinances = fha.getOrdinances(tom);
        assertNotNull(ordinances);
        assertEquals(1, ordinances.size());
        CustomFact ordinance = ordinances.get(0);
        assertEquals("_ATTR", ordinance.getTag());
        assertEquals("wasd", ordinance.getDescription().getValue());
        assertEquals("Ordinance", ordinance.getType().getValue());

        fha.removeOrdinances(tom);
        ordinances = fha.getOrdinances(tom);
        assertNotNull(ordinances);
        assertEquals(0, ordinances.size());

        CustomFact n = fha.newOrdinance("frying pan");
        fha.addOrdinance(tom, n);

        ordinances = fha.getOrdinances(tom);
        assertNotNull(ordinances);
        assertEquals(1, ordinances.size());
        ordinance = ordinances.get(0);
        assertEquals("_ATTR", ordinance.getTag());
        assertEquals("frying pan", ordinance.getDescription().getValue());
        assertEquals("Ordinance", ordinance.getType().getValue());
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getPlaceRecords(Gedcom)},
     * {@link FamilyHistorianAdapter#getPlaceRecord(Gedcom, String)},
     * {@link FamilyHistorianAdapter#removePlaceRecord(Gedcom, String)}, {@link FamilyHistorianAdapter#removePlaceRecords(Gedcom)},
     * {@link FamilyHistorianAdapter#addPlaceRecord(Gedcom, CustomFact)}, and
     * {@link FamilyHistorianAdapter#newPlace(String, String)}
     */
    @Test
    public void testPlaceRecords() {
        List<CustomFact> places = fha.getPlaceRecords(gedcomWithCustomTags);
        assertNotNull(places);
        assertEquals(11, places.size());

        CustomFact p1 = fha.getPlaceRecord(gedcomWithCustomTags, "@P1@");
        assertEquals(places.get(0), p1);
        assertEquals("Ansted, WV, USA", p1.getDescription().getValue());

        fha.removePlaceRecord(gedcomWithCustomTags, p1.getXref());
        assertNull(fha.getPlaceRecord(gedcomWithCustomTags, "@P1@"));

        places = fha.getPlaceRecords(gedcomWithCustomTags);
        assertNotNull(places);
        assertEquals(10, places.size());

        fha.addPlaceRecord(gedcomWithCustomTags, p1);
        assertSame(p1, fha.getPlaceRecord(gedcomWithCustomTags, "@P1@"));
        assertEquals("Ansted, WV, USA", p1.getDescription().getValue());

        places = fha.getPlaceRecords(gedcomWithCustomTags);
        assertNotNull(places);
        assertEquals(11, places.size());

        fha.removePlaceRecords(gedcomWithCustomTags);
        assertNull(fha.getPlaceRecord(gedcomWithCustomTags, "@P1@"));

        places = fha.getPlaceRecords(gedcomWithCustomTags);
        assertNotNull(places);
        assertEquals(0, places.size());

        CustomFact newPlace = fha.newPlace("@P999@", "Cucamonga");
        fha.addPlaceRecord(gedcomWithCustomTags, newPlace);
        p1 = fha.getPlaceRecord(gedcomWithCustomTags, "@P999@");
        assertEquals(p1, newPlace);
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getPlaceRecords(Gedcom)}
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testPlaceRecordsImmutable() {
        List<CustomFact> places = fha.getPlaceRecords(gedcomWithCustomTags);
        assertNotNull(places);
        assertEquals(11, places.size());
        places.clear();
    }

    /**
     * Test for {@link FamilyHistorianAdapter#removeNamedList(Gedcom, String)}
     */
    @Test
    public void testRemoveNamedList() {
        List<CustomFact> lists = fha.getNamedLists(gedcomWithCustomTags);
        assertNotNull(lists);
        assertEquals(1, lists.size());

        fha.removeNamedList(gedcomWithCustomTags, "Key Individuals");

        lists = fha.getNamedList(gedcomWithCustomTags, "Key Individuals");
        assertNotNull(lists);
        assertEquals(0, lists.size());
    }

    /**
     * Test for {@link FamilyHistorianAdapter#removeNamedLists(Gedcom)}
     */
    @Test
    public void testRemoveNamedLists() {
        List<CustomFact> lists = fha.getNamedLists(gedcomWithCustomTags);
        assertNotNull(lists);
        assertEquals(1, lists.size());

        fha.removeNamedLists(gedcomWithCustomTags);

        lists = fha.getNamedLists(gedcomWithCustomTags);
        assertNotNull(lists);
        assertEquals(0, lists.size());
    }

    /**
     * Negative test {@link FamilyHistorianAdapter#getRootIndividual(Gedcom)} and
     * {@link FamilyHistorianAdapter#setRootIndividual(Gedcom, Individual)}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetRootIndividualNegative1() {
        Individual individual = fha.getRootIndividual(gedcomWithoutCustomTags);
        assertNull(individual);

        fha.setRootIndividual(gedcomWithoutCustomTags, tom);
    }

    /**
     * Negative test for {@link FamilyHistorianAdapter#getRootIndividual(Gedcom)} and positive test for
     * {@link FamilyHistorianAdapter#setRootIndividual(Gedcom, Individual)}
     */
    @Test
    public void testSetRootIndividualNegative2() {
        Individual individual = fha.getRootIndividual(gedcomWithoutCustomTags);
        assertNull(individual);

        Individual tomsMom = gedcomWithoutCustomTags.getIndividuals().get("@I3@");
        fha.setRootIndividual(gedcomWithoutCustomTags, tomsMom);

        assertEquals(tomsMom, fha.getRootIndividual(gedcomWithoutCustomTags));
        assertSame(tomsMom, fha.getRootIndividual(gedcomWithoutCustomTags));
    }

    /**
     * Test for {@link FamilyHistorianAdapter#getSourceType(Source)} and
     * {@link FamilyHistorianAdapter#setSourceType(Source, String)}
     */
    @Test
    public void testSourceType() {
        Source src = gedcomWithCustomTags.getSources().get("@S1@");
        assertEquals("Hearsay", fha.getSourceType(src));

        fha.setSourceType(src, null);
        assertNull(fha.getSourceType(src));

        fha.setSourceType(src, "Napkin drawing");
        assertEquals("Napkin drawing", fha.getSourceType(src));
    }
}
