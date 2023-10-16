package dev.xgeorget.database.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import dev.xgeorget.database.domain.dto.BookDto;
import dev.xgeorget.database.domain.entities.BookEntity;
import dev.xgeorget.database.mappers.Mapper;

@Component
public class BookMapperImpl implements Mapper<BookEntity, BookDto> {

    private final ModelMapper modelMapper;

    public BookMapperImpl(final ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public BookEntity mapFrom(final BookDto bookDto) {
        return this.modelMapper.map(bookDto, BookEntity.class);
    }

    @Override
    public BookDto mapTo(final BookEntity bookEntity) {
        return this.modelMapper.map(bookEntity, BookDto.class);
    }

}
