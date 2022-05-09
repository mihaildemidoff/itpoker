package com.github.mihaildemidoff.itpoker.mapper;

import com.github.mihaildemidoff.itpoker.model.bo.DeckOptionBO;
import com.github.mihaildemidoff.itpoker.model.entity.DeckOptionEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class DeckOptionMapperTest {

    @InjectMocks
    private ServiceDeckOptionMapperImpl mapper;

    @Test
    void testToBO() {
        final DeckOptionEntity source = DeckOptionEntity.builder()
                .id(RandomUtils.nextLong())
                .deckId(RandomUtils.nextLong())
                .text(RandomStringUtils.randomAlphabetic(10))
                .row(RandomUtils.nextInt())
                .index(RandomUtils.nextInt())
                .createdDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(RandomUtils.nextLong()), ZoneId.systemDefault()))
                .modifiedDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(RandomUtils.nextLong()), ZoneId.systemDefault()))
                .build();
        final DeckOptionBO result = mapper.toBO(source);
        assertThat(result.id(), CoreMatchers.is(source.getId()));
        assertThat(result.deckId(), CoreMatchers.is(source.getDeckId()));
        assertThat(result.text(), CoreMatchers.is(source.getText()));
        assertThat(result.row(), CoreMatchers.is(source.getRow()));
        assertThat(result.index(), CoreMatchers.is(source.getIndex()));
        assertThat(result.createdDate(), CoreMatchers.is(source.getCreatedDate()));
        assertThat(result.modifiedDate(), CoreMatchers.is(source.getModifiedDate()));
    }

    @Test
    void testToBONullSource() {
        assertThat(mapper.toBO(null), CoreMatchers.nullValue());
    }

}
