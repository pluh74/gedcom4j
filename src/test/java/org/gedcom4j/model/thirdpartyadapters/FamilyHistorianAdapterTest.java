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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.util.List;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.CustomFact;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.parser.GedcomParser;
import org.junit.Before;
import org.junit.Test;

/**
 * @author frizbog
 *
 */
/**
 * @author frizbog
 *
 */
@SuppressWarnings("PMD.TooManyMethods")
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
     * Negative test case for {@link FamilyHistorianAdapter#getUID(Gedcom)} and
     * {@link FamilyHistorianAdapter#setUID(Gedcom, String)}
     */
    @Test
    public void testGetUIDNegative() {
        assertNull(fha.getUID(gedcomWithoutCustomTags));
        fha.setUID(gedcomWithoutCustomTags, "FryingPan");
        assertEquals("FryingPan", fha.getUID(gedcomWithoutCustomTags));
    }

    /**
     * Positive test case for {@link FamilyHistorianAdapter#getUID(Gedcom)} and
     * {@link FamilyHistorianAdapter#setUID(Gedcom, String)}
     */
    @Test
    public void testGetUIDPositive() {
        assertEquals("{C2159006-9E8E-4149-87DB-36E5F6D08A37}", fha.getUID(gedcomWithCustomTags));
        fha.setUID(gedcomWithCustomTags, "FryingPan");
        assertEquals("FryingPan", fha.getUID(gedcomWithCustomTags));
    }

    /**
     * Negative test case for {@link FamilyHistorianAdapter#getVariantExportFormat(Gedcom)} and
     * {@link FamilyHistorianAdapter#setVariantExportFormat(Gedcom, String)}
     */
    @Test
    public void testGetVariantExportFormatNegative() {
        assertNull(fha.getVariantExportFormat(gedcomWithoutCustomTags));
        fha.setVariantExportFormat(gedcomWithoutCustomTags, "FryingPan");
        assertEquals("FryingPan", fha.getVariantExportFormat(gedcomWithoutCustomTags));
    }

    /**
     * Positive test case for {@link FamilyHistorianAdapter#getVariantExportFormat(Gedcom)} and
     * {@link FamilyHistorianAdapter#setVariantExportFormat(Gedcom, String)}
     */
    @Test
    public void testGetVariantExportFormatPositive() {
        assertEquals("DSR", fha.getVariantExportFormat(gedcomWithCustomTags));
        fha.setVariantExportFormat(gedcomWithCustomTags, "FryingPan");
        assertEquals("FryingPan", fha.getVariantExportFormat(gedcomWithCustomTags));
    }

    /**
     * Negative test for {@link FamilyHistorianAdapter#getRootIndividual(Gedcom)} and positive test for
     * {@link FamilyHistorianAdapter#setRootIndividual(Gedcom, Individual)}
     */
    @Test
    public void testRootIndividualNegative2() {
        Individual individual = fha.getRootIndividual(gedcomWithoutCustomTags);
        assertNull(individual);

        Individual tomsMom = gedcomWithoutCustomTags.getIndividuals().get("@I3@");
        fha.setRootIndividual(gedcomWithoutCustomTags, tomsMom);

        assertEquals(tomsMom, fha.getRootIndividual(gedcomWithoutCustomTags));
        assertSame(tomsMom, fha.getRootIndividual(gedcomWithoutCustomTags));
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
}