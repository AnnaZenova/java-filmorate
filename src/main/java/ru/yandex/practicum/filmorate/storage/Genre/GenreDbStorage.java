package ru.yandex.practicum.filmorate.storage.Genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    public static final int genreMINID = 1;
    public static final int genreMAXID = 6;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenre() {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genres ORDER BY genre_id");
        List<Genre> listGenre = new ArrayList<>();
        while (genreRows.next()) {
            listGenre.add(Genre.builder().genreId(genreRows.getInt(1)).genreName(genreRows.getString(2)).build());
        }
        log.info("Получен список всех жанров");
        return listGenre;
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        if (genreId < genreMINID || genreId > genreMAXID) {
            throw new NotFoundException("Жанра с таким ID нет");
        }
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE genre_id = ?", genreId);
        if (genreRows.next()) {
            Genre genre = Genre.builder().genreId(genreRows.getInt(1)).genreName(genreRows.getString(2)).build();
            log.info("Получен жанр с ID={}", genreId);
            return genre;
        }
        log.info("Фильм не найден с ID={}", genreId);
        return null;
    }
}