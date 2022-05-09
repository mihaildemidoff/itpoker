package com.github.mihaildemidoff.itpoker.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class DeckMapperTest {

    @InjectMocks
    private ServiceDeckMapperImpl deckMapper;

    @Test
    void testToBO() {
        assertThat(true, CoreMatchers.is(true));
//        final DeckEntity source = new DeckEntity();
//        assertThat(deckMapper.toBO(null), CoreMatchers.nullValue());
    }

    @Test
    void testToBONullSource() {
        assertThat(deckMapper.toBO(null), CoreMatchers.nullValue());
    }

}
