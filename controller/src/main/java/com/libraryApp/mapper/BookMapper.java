package com.libraryApp.mapper;

import com.libraryApp.persistence.model.BookEntity;
import com.libraryApp.model.BookRepresentation;
import com.libraryApp.model.GenreEnumRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mapping(source = "genre", target = "genre", qualifiedByName = "enumToStringMapper")
    BookEntity bookRepToEntity(BookRepresentation bookRepresentation);

    @Mapping(source = "genre", target = "genre", qualifiedByName = "stringToEnumMapper")
    BookRepresentation entityToBookRep(BookEntity bookEntity);

    @Named("stringToEnumMapper")
    static GenreEnumRepresentation map(String genre) {
        return GenreEnumRepresentation.of(genre.toLowerCase());
    }

    @Named("enumToStringMapper")
    static String map(GenreEnumRepresentation genreEnum) {
        return genreEnum.getGenreName();
    }
}
