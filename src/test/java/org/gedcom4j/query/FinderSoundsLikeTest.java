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
package org.gedcom4j.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.parser.GedcomParser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link Finder} methods for Soundex matching
 * 
 * @author frizbog
 */
public class FinderSoundsLikeTest {

    /**
     * Class under test
     */
    private Finder classUnderTest;

    /**
     * Set up test fixtures
     * 
     * @throws GedcomParserException
     *             if the file cannot be parsed
     * @throws IOException
     *             if the file cannot be read
     */
    @Before
    public void setUp() throws IOException, GedcomParserException {
        GedcomParser gp = new GedcomParser();
        gp.load("sample/5.5.1 sample 1.ged");
        Gedcom gedcom = gp.getGedcom();
        classUnderTest = new Finder(gedcom);
    }

    /**
     * Test for {@link Finder#findByNameSoundsLike(String, String)}
     */
    @Test
    public void testFindByNameStringStringNegative() {
        List<Individual> matches = classUnderTest.findByNameSoundsLike("Willis", "Edmund Henry");
        assertNotNull(matches);
        assertEquals(0, matches.size());
    }

    /**
     * Test for {@link Finder#findByNameSoundsLike(String, String)}
     */
    @Test
    public void testFindByNameStringStringPositive() {
        List<Individual> matches = classUnderTest.findByNameSoundsLike("Willy", "Richerd Pedlie");
        assertNotNull(matches);
        assertEquals(6, matches.size());
        Individual m = matches.get(0);
        assertNotNull(m.getNames());
        assertEquals(1, m.getNames().size());
        assertNotNull(m.getNames().get(0));
        if ("@I64@".equals(m.getXref())) {
            assertEquals("Richard Pedley /Walley/", m.getNames().get(0).getBasic());
        }
        if ("@I48@".equals(m.getXref())) {
            assertEquals("Richard Pedley /Walley/, Jp", m.getNames().get(0).getBasic());
        }
    }

    /**
     * Test for {@link Finder#findByNameSoundsLike(String, String)}
     */
    @Test
    public void testFindByNameStringStringPositive1() {
        List<Individual> matches = classUnderTest.findByNameSoundsLike("Wally", "Richrd Pedly");
        assertNotNull(matches);
        assertEquals(6, matches.size());
        Individual m1 = matches.get(0);
        assertNotNull(m1);
        if ("@I64@".equals(m1.getXref())) {
            assertEquals("Richard Pedley /Walley/", m1.getNames().get(0).getBasic());
        }
        if ("@I48@".equals(m1.getXref())) {
            assertEquals("Richard Pedley /Walley/, Jp", m1.getNames().get(0).getBasic());
        }

        Individual m2 = matches.get(1);
        assertNotNull(m2);
        if ("@I64@".equals(m2.getXref())) {
            assertEquals("Richard Pedley /Walley/", m2.getNames().get(0).getBasic());
        }
        if ("@I48@".equals(m2.getXref())) {
            assertEquals("Richard Pedley /Walley/, Jp", m2.getNames().get(0).getBasic());
        }
    }

    /**
     * Test for {@link Finder#findByNameSoundsLike(String, String)}
     */
    @Test
    public void testFindByNameStringStringStringStringPositive2() {
        List<Individual> matches = classUnderTest.findByNameSoundsLike("Walley", "Richard Pedley");
        assertNotNull(matches);
        assertEquals(6, matches.size());
        List<Individual> matches2 = classUnderTest.findByNameSoundsLike("Waly", "Richrrrd Pedly");
        assertNotNull(matches2);
        assertEquals(6, matches2.size());
        assertEquals(matches, matches2);
    }

}
