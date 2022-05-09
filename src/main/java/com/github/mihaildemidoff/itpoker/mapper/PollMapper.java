package com.github.mihaildemidoff.itpoker.mapper;

import com.github.mihaildemidoff.itpoker.model.bo.PollBO;
import com.github.mihaildemidoff.itpoker.model.entity.PollEntity;
import org.mapstruct.Mapper;

@Mapper(config = ServiceMapperConfig.class)
public interface PollMapper {
    PollBO toBO(PollEntity source);
}
