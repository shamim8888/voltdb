/* This file is part of VoltDB.
 * Copyright (C) 2008-2013 VoltDB Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.voltdb.planner;

import org.voltdb.plannodes.AbstractPlanNode;
import org.voltdb.plannodes.SendPlanNode;
import org.voltdb.plannodes.NestLoopPlanNode;
import org.voltdb.plannodes.SeqScanPlanNode;

public class TestSelfJoins  extends PlannerTestCase {

    public void testSelfJoin() {
        AbstractPlanNode pn = compile("select * FROM R1 A JOIN R1 B ON A.C = B.C");
        pn = pn.getChild(0).getChild(0);
        assertTrue(pn instanceof NestLoopPlanNode);
        assertEquals(4, pn.getOutputSchema().getColumns().size());
        assertEquals(2, pn.getChildCount());
        AbstractPlanNode c = pn.getChild(0);
        assertTrue(c instanceof SeqScanPlanNode);
        SeqScanPlanNode ss = (SeqScanPlanNode) c;
        assertEquals("R1", ss.getTargetTableName());
        assertEquals("A", ss.getTargetTableAlias());
        c = pn.getChild(1);
        assertTrue(c instanceof SeqScanPlanNode);
        ss = (SeqScanPlanNode) c;
        assertEquals("R1", ss.getTargetTableName());
        assertEquals("B", ss.getTargetTableAlias());

        pn = compile("select * FROM R1 JOIN R1 B ON R1.C = B.C");
        pn = pn.getChild(0).getChild(0);
        assertTrue(pn instanceof NestLoopPlanNode);
        assertEquals(4, pn.getOutputSchema().getColumns().size());
        assertEquals(2, pn.getChildCount());
        c = pn.getChild(0);
        assertTrue(c instanceof SeqScanPlanNode);
        ss = (SeqScanPlanNode) c;
        assertEquals("R1", ss.getTargetTableName());
        assertEquals("R1", ss.getTargetTableAlias());
        c = pn.getChild(1);
        assertTrue(c instanceof SeqScanPlanNode);
        ss = (SeqScanPlanNode) c;
        assertEquals("R1", ss.getTargetTableName());
        assertEquals("B", ss.getTargetTableAlias());

        pn = compile("select A.A, A.C, B.A, B.C FROM R1 A JOIN R1 B ON A.C = B.C");
        pn = pn.getChild(0).getChild(0);
        assertTrue(pn instanceof NestLoopPlanNode);
        assertEquals(4, pn.getOutputSchema().getColumns().size());

    }

    public void testPartitionedSelfJoin() {
        // SELF JOIN of the partitioned table on the partitioned column
        AbstractPlanNode pn = compile("select * FROM P1 A JOIN P1 B ON A.A = B.A");
        assertTrue(pn instanceof SendPlanNode);

        // SELF JOIN on non-partitioned columns
        failToCompile("select * FROM P1 A JOIN P1 B ON A.C = B.A",
                "Join of multiple partitioned tables has insufficient join criteria");
        // SELF JOIN on non-partitioned column
        failToCompile("select * FROM P1 A JOIN P1 B ON A.C = B.C",
                      "Join of multiple partitioned tables has insufficient join criteria");
    }

    public void testInvalidSelfJoin() {
        // SELF JOIN with an identical alias
        failToCompile("select * FROM R1 A JOIN R1 A ON A.A = A.A",
                "Not unique table/alias: A");
        failToCompile("select * FROM R1 A JOIN R2 A ON A.A = A.A",
                "Not unique table/alias: A");
    }

    @Override
    protected void setUp() throws Exception {
        setupSchema(TestJoinOrder.class.getResource("testself-joins-ddl.sql"), "testselfjoins", false);
    }

}