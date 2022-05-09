package com.github.mihaildemidoff.itpoker.mapper;

import com.github.mihaildemidoff.itpoker.model.bo.DeckOptionBO;
import com.github.mihaildemidoff.itpoker.model.entity.DeckOptionEntity;
import org.mapstruct.Mapper;

@Mapper(config = ServiceMapperConfig.class)
public interface DeckOptionMapper {
    DeckOptionBO toBO(DeckOptionEntity source);
}
