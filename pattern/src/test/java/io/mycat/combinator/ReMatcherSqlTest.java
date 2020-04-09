package io.mycat.combinator;

import org.junit.Assert;
import org.junit.Test;

public class ReMatcherSqlTest {
    @Test
    public void testCharArray() {
        Assert.assertTrue(ReMatchers.asPredicateForCharArray("^夏威夷$").test("夏威夷".toCharArray()));
    }
}