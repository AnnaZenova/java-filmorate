package ru.yandex.practicum.filmorate.storage.Film;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.storage.Mpa.MpaDbStorage;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final LocalDate RELEASE_DATE_MIN_DATE = LocalDate.of(1895, 12, 28);

    MpaDbStorage mpaDbStorage;

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        isValidFilm(film);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO films (film_name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getMpaId() : null, Types.INTEGER);
            return ps;
        }, keyHolder);
        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveFilmGenres(filmId, film.getGenres());
        }

        log.info("Добавлен новый фильм с ID={}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!isFilmExists(film.getId())) {
            throw new NotFoundException("Фильм с ID=" + film.getId() + " не найден");
        }

        isValidFilm(film);
        getFilmById(film.getId());
        String sql = "UPDATE films SET film_name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getMpaId() : null,
                film.getId());

        saveFilmGenres(film.getId(), film.getGenres());
        log.info("Обновлен фильм с ID={}", film.getId());
        return getFilmById(film.getId()); // Возвращаем обновленный фильм из БД
    }

    @Override
    public void delete(int filmId) {
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", filmId);
        log.info("Удален фильм с ID={}", filmId);
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.*, m.mpa_name FROM films f LEFT JOIN mpa m ON f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(int filmId) {
        String sql = "SELECT f.*, m.mpa_name FROM films f LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, filmId);
        } catch (WrongDataException e) {
            log.info("Фильм не найден с ID={}", filmId);
            return null;
        }
    }

    @Override
    public void putLikeToFilm(int filmId, int userId) {
        String sql = "INSERT INTO likes_vs_film (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Фильму с ID={} поставил лайк пользователь с ID={}", filmId, userId);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(
                rs.getInt("film_id"),
                rs.getString("film_name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                rs.getObject("mpa_id") != null ?
                        new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")) : null
        );

        film.setGenres(getFilmGenres(film.getId()));
        film.setLikes(getFilmLikes(film.getId()));
        return film;
    }

    private List<Genre> getFilmGenres(int filmId) {
        String sql = "SELECT g.genre_id, g.genre_name FROM genre_vs_film fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    private Set<Integer> getFilmLikes(int filmId) {
        String sql = "SELECT user_id FROM likes_vs_film WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class, filmId));
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }

    private void saveFilmGenres(int filmId, Set<Genre> genres) {
        jdbcTemplate.update("DELETE FROM genre_vs_film WHERE film_id = ?", filmId);

        if (genres == null || genres.isEmpty()) {
            return;
        }

        List<Genre> validGenres = genres.stream()
                .filter(g -> g != null && g.getGenreId() != null)
                .collect(Collectors.toList());

        if (validGenres.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO genre_vs_film (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = validGenres.get(i);
                ps.setInt(1, filmId);
                ps.setInt(2, genre.getGenreId());
            }

            @Override
            public int getBatchSize() {
                return validGenres.size();
            }
        });
    }

    private boolean isValidFilm(Film film) {
        if (film.getReleaseDate().isBefore(RELEASE_DATE_MIN_DATE)) {
            throw new WrongDataException("Некорректная дата релиза фильма: " + film.getReleaseDate());
        }
        if (film.getMpa().getMpaId() < mpaDbStorage.MPA_MIN_ID || film.getMpa().getMpaId() > mpaDbStorage.MPA_MAX_ID) {
            throw new NotFoundException("MPA ID должен быть от 1 до 5");
        }
        for (Genre genre : film.getGenres()) {
            if (genre.getGenreId() < 1 || genre.getGenreId() > 6) {
                throw new NotFoundException("Фильма с таким жанром нет");
            }
        }
        return true;
    }

    private boolean isFilmExists(int filmId) {
        String sql = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId);
        return count != null && count > 0;
    }

    @Override
    public List<Film> findCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.*, m.mpa_name, " +
                "(SELECT COUNT(*) FROM likes_vs_film l WHERE l.film_id = f.film_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id IN (" +
                "   SELECT l1.film_id FROM likes_vs_film l1 WHERE l1.user_id = ? " +
                "   INTERSECT " +
                "   SELECT l2.film_id FROM likes_vs_film l2 WHERE l2.user_id = ? " +
                ") " +
                "ORDER BY likes_count DESC, f.film_name ASC";

        return jdbcTemplate.query(sql, this::mapRowToFilm, userId, friendId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Лайк от пользователя c ID=" + userId + " не найден!");
        }
        String sql = "DELETE FROM likes_vs_film WHERE film_id = ? AND user_id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, filmId, userId);
        if (rowsDeleted == 0) {
            throw new NotFoundException("Лайк/фильм не найден");
        }
        log.info("Удалён лайк пользователя {} у фильма {}", userId, filmId);
    }
}