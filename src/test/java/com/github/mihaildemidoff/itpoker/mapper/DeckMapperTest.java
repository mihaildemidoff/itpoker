package com.github.mihaildemidoff.itpoker.mapper;

import com.github.mihaildemidoff.itpoker.model.bo.DeckBO;
import com.github.mihaildemidoff.itpoker.model.common.DeckType;
import com.github.mihaildemidoff.itpoker.model.entity.DeckEntity;
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
class DeckMapperTest {

    @InjectMocks
    private ServiceDeckMapperImpl mapper;

    @Test
    void testToBO() {
        final DeckEntity source = DeckEntity.builder()
                .id(RandomUtils.nextLong())
                .type(DeckType.values()[RandomUtils.nextInt(0, DeckType.values().length)])
                .title(RandomStringUtils.randomAlphabetic(10))
                .description(RandomStringUtils.randomAlphabetic(10))
                .createdDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(RandomUtils.nextLong()), ZoneId.systemDefault()))
                .modifiedDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(RandomUtils.nextLong()), ZoneId.systemDefault()))
                .build();
        final DeckBO result = mapper.toBO(source);
        assertThat(result.id(), CoreMatchers.is(source.getId()));
        assertThat(result.type(), CoreMatchers.is(source.getType()));
        assertThat(result.title(), CoreMatchers.is(source.getTitle()));
        assertThat(result.description(), CoreMatchers.is(source.getDescription()));
        assertThat(result.createdDate(), CoreMatchers.is(source.getCreatedDate()));
        assertThat(result.modifiedDate(), CoreMatchers.is(source.getModifiedDate()));
    }

    @Test
    void testToBONullSource() {
        assertThat(mapper.toBO(null), CoreMatchers.nullValue());
    }

}
