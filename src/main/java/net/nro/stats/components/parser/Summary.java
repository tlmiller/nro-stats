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

import net.nro.stats.resources.StatsSource;
import org.apache.commons.csv.CSVRecord;

public class Summary implements Line {

    private StatsSource source;
    private final String registry;
    private final String type;
    private final String count;

    public Summary(StatsSource source, String registry, String type, String count) {
        this.source = source;
        this.registry = registry;
        this.type = type;
        this.count = count;
    }
    public Summary(String registry, String type, String count) {
        this(StatsSource.ESTATS, registry, type, count);
    }

    public Summary(StatsSource source, CSVRecord line) {
        if (!fits(line)) throw new RuntimeException("Given line was not a Summary");
        this.source = source;
        this.registry = line.get(0);
        this.type = line.get(2);
        this.count = line.get(4);
    }

    public Summary(CSVRecord line) {
        this(StatsSource.ESTATS, line);
    }

    @Override
    public StatsSource getSource() {
        return source;
    }

    public String getRegistry() {
        return registry;
    }

    public String getType() {
        return type;
    }

    public String getCount() {
        return count;
    }

    public static boolean fits(CSVRecord line) {
        return line.size() == 6 && "summary".equals(line.get(5));
    }

    @Override
    public String toString() {
        return String.format("%s|*|%s|*|%s|summary", getRegistry(), getType(), getCount());
    }
}
