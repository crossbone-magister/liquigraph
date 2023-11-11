package com.crossbonemagister.liquigraph;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LiquigraphCliApplicationTest {

    @Test
    public void testMainNoArgs() {
        Assertions.assertThatThrownBy(() -> LiquigraphCliApplication.main(new String[]{}))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Missing argument: changelog file path");
    }

    @Test
    public void testMainEmptyArgs() {
        Assertions.assertThatThrownBy(() -> LiquigraphCliApplication.main(new String[]{""}))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid argument: changelog file");
    }

    @Test
    public void testMain() {
        Assertions.assertThatCode(() -> LiquigraphCliApplication.main(new String[]{"changelog/create-table.yml"}))
            .doesNotThrowAnyException();
    }

}
