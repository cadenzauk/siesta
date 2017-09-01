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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class Select9Test {
    @SuppressWarnings("unused")
    static class Node {
        private int id;
        private int parentId;

        int id() {
            return id;
        }

        int parentId() {
            return parentId;
        }
    }

    private final Database database = Database.newBuilder().defaultSchema("TEST9").build();

    @Test
    void canJoin20Tables() {
        Alias<Node> n1 = database.table(Node.class).as("n1");
        Alias<Node> n2 = database.table(Node.class).as("n2");
        Alias<Node> n3 = database.table(Node.class).as("n3");
        Alias<Node> n4 = database.table(Node.class).as("n4");
        Alias<Node> n5 = database.table(Node.class).as("n5");
        Alias<Node> n6 = database.table(Node.class).as("n6");
        Alias<Node> n7 = database.table(Node.class).as("n7");
        Alias<Node> n8 = database.table(Node.class).as("n8");
        Alias<Node> n9 = database.table(Node.class).as("n9");
        Alias<Node> n10 = database.table(Node.class).as("n10");
        Alias<Node> n11 = database.table(Node.class).as("n11");
        Alias<Node> n12 = database.table(Node.class).as("n12");
        Alias<Node> n13 = database.table(Node.class).as("n13");
        Alias<Node> n14 = database.table(Node.class).as("n14");
        Alias<Node> n15 = database.table(Node.class).as("n15");
        Alias<Node> n16 = database.table(Node.class).as("n16");
        Alias<Node> n17 = database.table(Node.class).as("n17");
        Alias<Node> n18 = database.table(Node.class).as("n18");
        Alias<Node> n19 = database.table(Node.class).as("n19");
        Alias<Node> n20 = database.table(Node.class).as("n20");

        String sql = database.from(n1)
            .leftJoin(n2).on(n2, Node::id).isEqualTo(n1, Node::parentId)
            .leftJoin(n3).on(n3, Node::id).isEqualTo(n2, Node::parentId)
            .leftJoin(n4).on(n4, Node::id).isEqualTo(n3, Node::parentId)
            .leftJoin(n5).on(n5, Node::id).isEqualTo(n4, Node::parentId)
            .leftJoin(n6).on(n6, Node::id).isEqualTo(n5, Node::parentId)
            .leftJoin(n7).on(n7, Node::id).isEqualTo(n6, Node::parentId)
            .leftJoin(n8).on(n8, Node::id).isEqualTo(n7, Node::parentId)
            .leftJoin(n9).on(n9, Node::id).isEqualTo(n8, Node::parentId)
            .leftJoin(n10).on(n10, Node::id).isEqualTo(n9, Node::parentId)
            .leftJoin(n11).on(n11, Node::id).isEqualTo(n10, Node::parentId)
            .leftJoin(n12).on(n12, Node::id).isEqualTo(n11, Node::parentId)
            .leftJoin(n13).on(n13, Node::id).isEqualTo(n12, Node::parentId)
            .leftJoin(n14).on(n14, Node::id).isEqualTo(n13, Node::parentId)
            .leftJoin(n15).on(n15, Node::id).isEqualTo(n14, Node::parentId)
            .leftJoin(n16).on(n16, Node::id).isEqualTo(n15, Node::parentId)
            .leftJoin(n17).on(n17, Node::id).isEqualTo(n16, Node::parentId)
            .leftJoin(n18).on(n18, Node::id).isEqualTo(n17, Node::parentId)
            .leftJoin(n19).on(n19, Node::id).isEqualTo(n18, Node::parentId)
            .leftJoin(n20).on(n20, Node::id).isEqualTo(n19, Node::parentId)
            .where(n1, Node::id).isEqualTo(4)
            .sql();

        assertThat(sql, is("select n1.ID as n1_ID, n1.PARENT_ID as n1_PARENT_ID, " +
            "n2.ID as n2_ID, n2.PARENT_ID as n2_PARENT_ID, " +
            "n3.ID as n3_ID, n3.PARENT_ID as n3_PARENT_ID, " +
            "n4.ID as n4_ID, n4.PARENT_ID as n4_PARENT_ID, " +
            "n5.ID as n5_ID, n5.PARENT_ID as n5_PARENT_ID, " +
            "n6.ID as n6_ID, n6.PARENT_ID as n6_PARENT_ID, " +
            "n7.ID as n7_ID, n7.PARENT_ID as n7_PARENT_ID, " +
            "n8.ID as n8_ID, n8.PARENT_ID as n8_PARENT_ID, " +
            "n9.ID as n9_ID, n9.PARENT_ID as n9_PARENT_ID, " +
            "n10.ID as n10_ID, n10.PARENT_ID as n10_PARENT_ID, " +
            "n11.ID as n11_ID, n11.PARENT_ID as n11_PARENT_ID, " +
            "n12.ID as n12_ID, n12.PARENT_ID as n12_PARENT_ID, " +
            "n13.ID as n13_ID, n13.PARENT_ID as n13_PARENT_ID, " +
            "n14.ID as n14_ID, n14.PARENT_ID as n14_PARENT_ID, " +
            "n15.ID as n15_ID, n15.PARENT_ID as n15_PARENT_ID, " +
            "n16.ID as n16_ID, n16.PARENT_ID as n16_PARENT_ID, " +
            "n17.ID as n17_ID, n17.PARENT_ID as n17_PARENT_ID, " +
            "n18.ID as n18_ID, n18.PARENT_ID as n18_PARENT_ID, " +
            "n19.ID as n19_ID, n19.PARENT_ID as n19_PARENT_ID, " +
            "n20.ID as n20_ID, n20.PARENT_ID as n20_PARENT_ID " +
            "from TEST9.NODE n1 " +
            "left join TEST9.NODE n2 on n2.ID = n1.PARENT_ID " +
            "left join TEST9.NODE n3 on n3.ID = n2.PARENT_ID " +
            "left join TEST9.NODE n4 on n4.ID = n3.PARENT_ID " +
            "left join TEST9.NODE n5 on n5.ID = n4.PARENT_ID " +
            "left join TEST9.NODE n6 on n6.ID = n5.PARENT_ID " +
            "left join TEST9.NODE n7 on n7.ID = n6.PARENT_ID " +
            "left join TEST9.NODE n8 on n8.ID = n7.PARENT_ID " +
            "left join TEST9.NODE n9 on n9.ID = n8.PARENT_ID " +
            "left join TEST9.NODE n10 on n10.ID = n9.PARENT_ID " +
            "left join TEST9.NODE n11 on n11.ID = n10.PARENT_ID " +
            "left join TEST9.NODE n12 on n12.ID = n11.PARENT_ID " +
            "left join TEST9.NODE n13 on n13.ID = n12.PARENT_ID " +
            "left join TEST9.NODE n14 on n14.ID = n13.PARENT_ID " +
            "left join TEST9.NODE n15 on n15.ID = n14.PARENT_ID " +
            "left join TEST9.NODE n16 on n16.ID = n15.PARENT_ID " +
            "left join TEST9.NODE n17 on n17.ID = n16.PARENT_ID " +
            "left join TEST9.NODE n18 on n18.ID = n17.PARENT_ID " +
            "left join TEST9.NODE n19 on n19.ID = n18.PARENT_ID " +
            "left join TEST9.NODE n20 on n20.ID = n19.PARENT_ID " +
            "where n1.ID = ?"));
    }
}
