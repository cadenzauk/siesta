/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

import com.cadenzauk.siesta.ddl.SchemaDefinition;
import com.cadenzauk.siesta.ddl.SchemaGenerator;
import org.springframework.beans.factory.InitializingBean;

public class SpringSiesta implements InitializingBean {
    private Database database;
    private boolean dropFirst = false;
    private SchemaDefinition schemaDefinition;

    public SpringSiesta setDatabase(Database database) {
        this.database = database;
        return this;
    }

    public SpringSiesta setDropFirst(boolean dropFirst) {
        this.dropFirst = dropFirst;
        return this;
    }

    public SpringSiesta setSchemaDefinition(SchemaDefinition schemaDefinition) {
        this.schemaDefinition = schemaDefinition;
        return this;
    }

    @Override
    public void afterPropertiesSet() {
        SchemaGenerator schemaGenerator = new SchemaGenerator(dropFirst);
        schemaGenerator.generate(database, schemaDefinition);
    }
}
