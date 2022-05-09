package com.github.mihaildemidoff.itpoker.mapper;

import com.github.mihaildemidoff.itpoker.model.bo.DeckBO;
import com.github.mihaildemidoff.itpoker.model.entity.DeckEntity;
import org.mapstruct.Mapper;

@Mapper(config = ServiceMapperConfig.class)
public interface DeckMapper {
    DeckBO toBO(DeckEntity source);
}
