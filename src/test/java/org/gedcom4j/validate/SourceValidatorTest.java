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
package org.gedcom4j.validate;

import org.gedcom4j.model.EventRecorded;
import org.gedcom4j.model.Source;
import org.gedcom4j.model.SourceData;
import org.junit.Test;

/**
 * @author frizbog1
 * 
 */
public class SourceValidatorTest extends AbstractValidatorTestCase {

    /**
     * Test when source has some bad values
     */
    @Test
    public void testBadSource1() {
        Source src = new Source("bad xref");
        src.setRecIdNumber("");
        AbstractValidator av = new SourceValidator(validator, src);
        av.validate();
        assertFindingsContain(Severity.ERROR, src, ProblemCode.MISSING_REQUIRED_VALUE, "recIdNumber");
        assertFindingsContain(Severity.ERROR, src, ProblemCode.XREF_INVALID, "xref");
    }

    /**
     * Test when source has some bad values
     */
    @Test
    public void testBadSource2() {
        Source src = new Source("@Test@");
        src.setData(new SourceData());
        EventRecorded e = new EventRecorded();
        e.setDatePeriod("anytime");
        src.getData().getEventsRecorded(true).add(e);
        AbstractValidator av = new SourceValidator(validator, src);
        av.validate();
        assertNoIssues();
    }

    /**
     * Test for a new {@link Source}
     */
    @Test
    public void testDefault() {
        Source src = new Source((String) null);
        AbstractValidator av = new SourceValidator(validator, src);
        av.validate();
        assertFindingsContain(Severity.ERROR, src, ProblemCode.MISSING_REQUIRED_VALUE, "xref");
    }

    /**
     * Test when source is null
     */
    @Test(expected = NullPointerException.class)
    public void testNullSource() {
        AbstractValidator av = new SourceValidator(validator, null);
        av.validate();
    }
}
