package com.course.libraryapp.exposure.mapper;

import com.course.libraryapp.exposure.model.BookRepresentation;
import com.course.libraryapp.exposure.model.GenreEnumRepresentation;
import com.course.libraryapp.persistance.model.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    //    @Mapping(target = "id", ignore = true)
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
