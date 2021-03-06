/**
 * The BSD License
 *
 * Copyright (c) 2010-2016 RIPE NCC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   - Neither the name of the RIPE NCC nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.nro.stats.components.parser;

import net.nro.stats.components.CSVRecordUtil;
import net.ripe.commons.ip.Ipv4;
import net.ripe.commons.ip.Ipv4Range;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IPv4RecordTest {

    @Test
    public void testFits() throws Exception {
        Iterable<CSVRecord> lines = CSVRecordUtil.read("parser/ipv4.txt");
        for (CSVRecord line : lines) {
            assertTrue(String.format("line %d should fit IPv4Record", line.getRecordNumber()), IPv4Record.fits(line));
        }
    }

    @Test
    public void testValuesCorrect() throws Exception {
        Iterable<CSVRecord> lines = CSVRecordUtil.read(
                new StringReader("apnic|AU|ipv4|1.0.0.0|256|20110811|assigned|A91872ED\n" +
                        "apnic|AU|ipv4|1.0.0.0|256|20110811|assigned|\n" +
                        "apnic|AU|ipv4|1.0.0.0|256|20110811|assigned\n" +
                        "apnic|CN|ipv4|1.0.1.0|256|20110414|assigned|A92E1062|ext4|ext5|ext6\n"));


        for (CSVRecord line : lines) {
            IPv4Record record = new IPv4Record(line, "someDate");
            assertEquals("IPv4Record field not correct: registry", line.get(0), record.getRegistry());
            assertEquals("IPv4Record field not correct: countryCode", line.get(1), record.getCountryCode());
            assertEquals("IPv4Record field not correct: type", line.get(2), record.getType());
            assertEquals("IPv4Record field not correct: start", line.get(3), record.getStart());
            assertEquals("IPv4Record field not correct: value", line.get(4), record.getValue());
            assertEquals("IPv4Record field not correct: date", line.get(5), record.getDate());
            assertEquals("IPv4Record field not correct: status", line.get(6), record.getStatus());
            if (line.size() > 7) {
                assertEquals("IPv4Record field not correct: regId", line.get(7), record.getRegId());

                assertEquals("IPv4Record: number of extensions does not match line", line.size() - 8, record.getExtensions().length);
                for (int i = 0; i < record.getExtensions().length; i++) {
                    assertEquals(String.format("extension %d does not match on line %d", i, line.getRecordNumber()), record.getExtensions()[i], line.get(i + 8));
                }
            }
        }
    }

    @Test
    public void testIpRangeConversion() throws Exception {
        IPv4Record record1 = new IPv4Record("", "", "0.0.0.0", "1", "", "", "");
        assertEquals("", Ipv4Range.from(Ipv4.of("0.0.0.0")).to(Ipv4.of("0.0.0.0")), record1.getRange());

        IPv4Record record2 = new IPv4Record("", "", "100.0.0.0", "256", "", "", "");
        assertEquals("", Ipv4Range.from(Ipv4.of("100.0.0.0")).to(Ipv4.of("100.0.0.255")), record2.getRange());

        IPv4Record record3 = new IPv4Record("", "", "192.168.0.0", "1024", "", "", "");
        assertEquals("", Ipv4Range.from(Ipv4.of("192.168.0.0")).to(Ipv4.of("192.168.3.255")), record3.getRange());

    }
}