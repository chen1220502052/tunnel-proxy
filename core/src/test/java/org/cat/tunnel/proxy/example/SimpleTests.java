package org.cat.tunnel.proxy.example;

import org.cat.tunnel.proxy.annotation.FastTest;
import org.cat.tunnel.proxy.core.utils.Calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleTests {


    private final Calculator calculatorUtil = new Calculator();

    @FastTest
    void addition() {
        assertEquals(2, calculatorUtil.add(1, 1));
    }
}
