package com.course.libraryapp.exposure.mapper;

import com.course.libraryapp.exposure.model.BookRepresentation;
import com.course.libraryapp.persistance.model.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(target = "id", ignore = true)
    BookEntity bookRepToEntity(BookRepresentation bookRepresentation);
    BookRepresentation entityToBookRep(BookEntity bookEntity);
}
