/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cadenzauk.siesta;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HiLoGeneratorIntegrationTest extends IntegrationTest {
    private final static Logger LOG = LoggerFactory.getLogger(HiLoGenerator.class);

    @Test
    void hiLoGenerationLoad() {
        Database database = testDatabase(dataSource);
        Sequence<Long> widgetSeq = database.sequence(Long.class, "widget_seq");
        HiLoGenerator sut = HiLoGenerator.newBuilder(widgetSeq)
            .loSize(40)
            .build();

        long start = System.nanoTime();
        long[] ids = IntStream.range(0, 510)
            .parallel()
            .mapToLong(x -> sut.single())
            .sorted()
            .toArray();
        long end = System.nanoTime();

        assertThat(ids, is(LongStream.range(1, 511).toArray()));

        LOG.info("Time = {} ms", TimeUnit.NANOSECONDS.toMillis(end - start));
    }
}
