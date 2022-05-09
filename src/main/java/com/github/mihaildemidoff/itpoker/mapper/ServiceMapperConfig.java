package com.github.mihaildemidoff.itpoker.mapper;

import org.mapstruct.MapperConfig;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@MapperConfig(componentModel = "spring", injectionStrategy = CONSTRUCTOR, implementationName = "Service<CLASS_NAME>Impl")
public interface ServiceMapperConfig {
}
