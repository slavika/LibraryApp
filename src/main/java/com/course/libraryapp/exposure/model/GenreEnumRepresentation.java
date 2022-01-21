package com.course.libraryapp.exposure.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Optional;

public enum GenreEnumRepresentation {

    FANTASY("fantasy"),
    SCI_FI("sci-fi"),
    THRILLER_HORROR("thriller/horror"),
    POWIESC_PRZYGODOWA("powieść przygodowa"),
    LIT_POPULARNO_NAUKOWA("literatura popularno-naukowa"),
    POEZJA("poezja");

    @JsonValue
    private final String genre;

    GenreEnumRepresentation(String genre) {
        this.genre = genre;
    }

    public static GenreEnumRepresentation of(String genre) {
        Optional<GenreEnumRepresentation> optionalGenreEnumRep = Arrays.stream(GenreEnumRepresentation.values())
                .filter(genreEnumRep -> genreEnumRep.genre.equals(genre)).findFirst();
        return optionalGenreEnumRep.orElse(null);
    }

    public String getGenreName(){
        return genre;
    }
}
