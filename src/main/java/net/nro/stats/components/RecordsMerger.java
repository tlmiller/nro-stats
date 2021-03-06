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
package net.nro.stats.components;

import net.nro.stats.components.merger.*;
import net.nro.stats.config.ExtendedOutputConfig;
import net.nro.stats.resources.MergedStats;
import net.nro.stats.resources.ParsedRIRStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecordsMerger {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IPv4Merger iPv4Merger;

    @Autowired
    private IPv6Merger iPv6Merger;

    @Autowired
    private ASNMerger asnMerger;

    @Autowired
    private HeaderMerger headerMerger;


    public MergedStats merge(List<ParsedRIRStats> parsedRIRStats) {
        logger.debug("Starting with the merger of RIR stats");

        MergedStats stats = new MergedStats();
        stats.setAsns(asnMerger.mergeToTree(
                parsedRIRStats.stream()
                        .map(ParsedRIRStats::getAsnRecords)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        ));
        stats.setIpv4s(iPv4Merger.mergeToTree(
                parsedRIRStats.stream()
                        .map(ParsedRIRStats::getIpv4Records)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        ));
        stats.setIpv6s(iPv6Merger.mergeToTree(
                parsedRIRStats.stream()
                        .map(ParsedRIRStats::getIpv6Records)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        ));

        stats.setHeaderStartDate(headerMerger.getStartDate(
                parsedRIRStats.stream()
                        .map(ParsedRIRStats::getHeaders)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        ));

        return stats;
    }

    public List<Delta<?>> findDifferences(MergedStats current ,MergedStats previous) {
        List<Delta<?>> diff = new ArrayList<>();
        diff.addAll(asnMerger.treeDiff(current.getAsns(), previous.getAsns()));
        diff.addAll(iPv4Merger.treeDiff(current.getIpv4s(), previous.getIpv4s()));
        diff.addAll(iPv6Merger.treeDiff(current.getIpv6s(), previous.getIpv6s()));

        return diff;
    }
}
