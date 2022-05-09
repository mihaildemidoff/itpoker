package com.github.mihaildemidoff.itpoker.mapper;

import com.github.mihaildemidoff.itpoker.model.bo.VoteBO;
import com.github.mihaildemidoff.itpoker.model.entity.VoteEntity;
import org.mapstruct.Mapper;

@Mapper(config = ServiceMapperConfig.class)
public interface VoteMapper {
    VoteBO toBO(VoteEntity source);
}
