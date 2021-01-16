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

package com.cadenzauk.codegen;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class SiestaCodeGenerator {
    private static final String CORE = "siesta/src/main/java/com/cadenzauk/core";
    private static final String SIESTA = "siesta/src/main/java/com/cadenzauk/siesta";

    private final Configuration cfg;

    private SiestaCodeGenerator() {
        cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassForTemplateLoading(SiestaCodeGenerator.class, "templates");

        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    private void generate(String templateName, int n, int max, String fileName) {
        try {
            Map<String,Object> input = new HashMap<>();
            input.put("n", n);
            input.put("max", max);
            input.put("year", Integer.toString(LocalDate.now().getYear()));
            Template template = cfg.getTemplate(templateName);
            FileWriter fileWriter = new FileWriter(new File(fileName));
            template.process(input, fileWriter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateFunction(int n, int max) {
        generate("FunctionN.ftl", n, max, CORE + "/function/Function" + n + ".java");
    }

    private void generateTupleN(int n, int max) {
        generate("TupleN.ftl", n, max, CORE + "/tuple/Tuple" + n + ".java");
    }

    private void generateTuple(int n, int max) {
        generate("Tuple.ftl", n, max, CORE + "/tuple/Tuple.java");
    }

    private void generateProjections(int n) {
        generate("Projections.ftl", n, n, SIESTA + "/Projections.java");
    }

    private void generateRowMappers(int n) {
        generate("RowMappers.ftl", n, n, SIESTA + "/RowMappers.java");
    }

    private void generateRowMapperFactories(int n) {
        generate("RowMapperFactories.ftl", n, n, SIESTA + "/RowMapperFactories.java");
    }

    private void generateExpectingJoin(int n, int max) {
        generate("ExpectingJoinN.ftl", n, max, SIESTA + "/grammar/select/ExpectingJoin" + n + ".java");
    }

    private void generateInProjectionExpectingComma(int n, int max) {
        generate("InProjectionExpectingCommaN.ftl", n, max, SIESTA + "/grammar/select/InProjectionExpectingComma" + n + ".java");
    }

    private void generateTupleBuilderN(int n, int max) {
        generate("TupleBuilderN.ftl", n, max, SIESTA + "/grammar/expression/TupleBuilder" + n + ".java");
    }

    private void generateFunctions(int max) {
        IntStream.range(3, max + 1).forEach(i -> generateFunction(i, max));
    }

    private void generateExpectingJoins(int max) {
        IntStream.range(2, max + 1).forEach(i -> generateExpectingJoin(i, max));
    }

    private void generateInProjectionExpectingCommas(int max) {
        IntStream.range(2, max + 1).forEach(i -> generateInProjectionExpectingComma(i, max));
    }

    private void generateTupleBuilders(int max) {
        IntStream.range(1, max + 1).forEach(i -> generateTupleBuilderN(i, max));
    }

    private void generateTuples(int max) {
        generateTuple(max, max);
        IntStream.range(2, max + 1).forEach(i -> generateTupleN(i, max));
    }

    private void generateAll() {
//        generateFunctions(20);
//        generateTuples(20);
//        generateRowMappers(20);
//        generateRowMapperFactories(20);
//        generateProjections(20);
        generateExpectingJoins(20);
//        generateInProjectionExpectingCommas(19);
//        generateTupleBuilders(20);
    }

    public static void main(String[] args) {
        new SiestaCodeGenerator().generateAll();
    }
}
