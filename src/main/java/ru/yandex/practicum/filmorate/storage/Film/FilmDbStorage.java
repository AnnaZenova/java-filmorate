package ru.yandex.practicum.filmorate.storage.Film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Mpa.MpaDbStorage;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private static final LocalDate RELEASE_DATE_MIN_DATE = LocalDate.of(1895, 12, 28);

    private MpaDbStorage mpaDbStorage;

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
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            saveFilmDirectors(filmId, film.getDirectors());
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
        String sql = "UPDATE films SET " +
                "film_name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ?, " +
                "mpa_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getMpaId() : null,
                film.getId());

        saveFilmGenres(film.getId(), film.getGenres());
        saveFilmDirectors(film.getId(), film.getDirectors());
        log.info("Обновлен фильм с ID={}", film.getId());
        return getFilmById(film.getId()); // Возвращаем обновленный фильм из БД
    }

    @Override
    public void delete(int filmId) {
        getFilmById(filmId);
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
        try {
            String sql = "SELECT f.*, m.mpa_name FROM films f LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "WHERE f.film_id = ?";
            Film film = jdbcTemplate.queryForObject(sql, this::mapRowToFilm, filmId);
            if (film == null) {
                throw new NotFoundException("Фильм с ID=" + filmId + " не найден");
            }
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с ID=" + filmId + " не найден");
        }
    }

    @Override
    public void putLikeToFilm(int filmId, int userId) {
        String sql = "INSERT INTO likes_vs_film (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Фильму с ID={} поставил лайк пользователь с ID={}", filmId, userId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByYear(Integer directorId) {
        String sql = "SELECT f.*, m.mpa_name " +
                "FROM films AS f " +
                "JOIN director_vs_film AS df ON f.film_id = df.film_id " +
                "JOIN directors AS d ON df.director_id = d.director_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE d.director_id = ? " +
                "ORDER BY EXTRACT(YEAR FROM f.release_date)";
        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByLikes(Integer directorId) {
        String sql = "SELECT f.*, m.mpa_name " +
                "FROM films AS f " +
                "JOIN director_vs_film AS df ON f.film_id = df.film_id " +
                "JOIN directors AS d ON df.director_id = d.director_id " +
                "LEFT JOIN likes_vs_film as lf ON f.film_id = lf.film_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE d.director_id = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(lf.film_id) DESC";
        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getFilmsWithQueryAndDirectorName(String query) {
        String sql = "SELECT f.*, m.mpa_name " +
                "FROM films AS f " +
                "JOIN director_vs_film AS df ON f.film_id = df.film_id " +
                "JOIN directors AS d ON df.director_id = d.director_id " +
                "LEFT JOIN likes_vs_film as lf ON f.film_id = lf.film_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE LOWER(d.director_name) LIKE LOWER(?) " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(lf.film_id) DESC";
        return jdbcTemplate.query(sql, this::mapRowToFilm, query);
    }

    @Override
    public List<Film> getFilmsWithQueryAndFilmName(String query) {
        String sql = "SELECT f.*, m.mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN likes_vs_film as lf ON f.film_id = lf.film_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE LOWER(f.film_name) LIKE LOWER(?) " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(lf.film_id) DESC";
        return jdbcTemplate.query(sql, this::mapRowToFilm, query);
    }

    @Override
    public List<Film> getFilmsWithQueryAndFilmPlusDirector(String query) {
        String sql = "SELECT f.*, m.mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN director_vs_film AS df ON f.film_id = df.film_id " +
                "LEFT JOIN directors AS d ON df.director_id = d.director_id " +
                "LEFT JOIN likes_vs_film as lf ON f.film_id = lf.film_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE LOWER(d.director_name) LIKE LOWER(?) OR LOWER(f.film_name) LIKE LOWER(?) " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(lf.film_id) DESC";
        return jdbcTemplate.query(sql, this::mapRowToFilm, query, query);
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
        film.setDirectors(getFilmDirectors(film.getId()));
        return film;
    }

    private List<Genre> getFilmGenres(int filmId) {
        String sql = "SELECT g.genre_id, g.genre_name FROM genre_vs_film fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    private List<Director> getFilmDirectors(int filmId) {
        String sql = "SELECT d.director_id, d.director_name FROM director_vs_film dg " +
                "JOIN directors d ON dg.director_id = d.director_id " +
                "WHERE dg.film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToDirector, filmId);
    }

    private Set<Integer> getFilmLikes(int filmId) {
        String sql = "SELECT user_id FROM likes_vs_film WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class, filmId));
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getInt("director_id"), rs.getString("director_name"));
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

    private void saveFilmDirectors(int filmId, Set<Director> directors) {
        jdbcTemplate.update("DELETE FROM director_vs_film WHERE film_id = ?", filmId);

        if (directors == null || directors.isEmpty()) {
            return;
        }

        List<Director> validDirectors = directors.stream()
                .filter(g -> g != null && g.getDirectorId() != null)
                .collect(Collectors.toList());

        if (validDirectors.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO director_vs_film (film_id, director_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Director director = validDirectors.get(i);
                ps.setInt(1, filmId);
                ps.setInt(2, director.getDirectorId());
            }

            @Override
            public int getBatchSize() {
                return validDirectors.size();
            }
        });
    }

    // Получаем уникальный список фильмов которые "лайкал" пользователь.
    @Override
    public List<Integer> findFilmsLikedByUser(int userId) {
        log.debug("Получение списка фильмов, которым поставил лайки пользователь с ID: {}", userId);
        String sql = "SELECT film_id FROM likes_vs_film WHERE user_id = ?";
        List<Integer> filmIds = jdbcTemplate.queryForList(sql, Integer.class, userId);
        return new ArrayList<>(filmIds);
    }

    @Override
    public Map<Integer, Integer> getCommonLikes(int userId) {
        log.debug("Получение таблицы с id пользователя и количеством пересечений.");

        /** В запросе склеиваем две таблицы лайков по film_id. Убираем одинаковые user_id в обеих колонках после склейки.
         группируем по user_id второй итоговой колонки и подсчитываем кол-во. */
        String sql = "SELECT l2.user_id AS another_user, COUNT(*) AS count_common_likes " +
                "FROM likes_vs_film AS l1 " +
                "JOIN likes_vs_film AS l2 ON l1.film_id = l2.film_id " +
                "WHERE l1.user_id = ? AND l2.user_id != ? " +
                "GROUP BY l2.user_id";

        // Заполняем таблицу Ключ: user_id пересекающихся по лайкам пользователей. Значение: Кол-во пересечений, с user_id
        Map<Integer, Integer> commonLikes = jdbcTemplate.query(sql,
                rs -> {
                    Map<Integer, Integer> result = new HashMap<>();
                    while (rs.next()) {
                        int anotherUserId = rs.getInt("another_user");
                        int countCommonLikes = rs.getInt("count_common_likes");
                        result.put(anotherUserId, countCommonLikes);
                    }
                    return result;
                }, userId, userId);

        return commonLikes;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        log.info("Получение списка популярных фильмов.");
        String sql =
                "SELECT f.*, mr.mpa_name " +
                        "FROM films AS f " +
                        "JOIN mpa AS mr ON f.mpa_id = mr.mpa_id " +
                        "LEFT OUTER JOIN likes_vs_film l ON (f.film_id = l.film_id) " +
                        "GROUP BY f.film_id, mr.mpa_name " +
                        "ORDER BY COUNT(l.user_id) desc " +
                        "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    @Override
    public List<Film> getPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        log.info("Получение списка популярных фильмов по лайкам, году и жанру");
        String sql = "SELECT f.*, m.mpa_name FROM FILMS f " +
                "JOIN MPA m ON(f.MPA_ID = m.MPA_ID) " +
                "LEFT OUTER JOIN LIKES_VS_FILM l ON (l.FILM_ID = f.FILM_ID) " +
                "JOIN GENRE_VS_FILM fg ON f.FILM_ID = fg.FILM_ID " +
                "WHERE fg.GENRE_ID = ? AND EXTRACT(YEAR FROM f.RELEASE_DATE) = ? " +
                "GROUP BY f.FILM_ID, m.mpa_name " +
                "ORDER BY COUNT(l.USER_ID) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, year, count);
    }

    @Override
    public List<Film> getPopularFilmsByGenre(int count, Integer genreId) {
        log.info("Получение списка популярных фильмов по лайкам и жанру");
        String sql = "SELECT f.*, m.mpa_name FROM FILMS f " +
                "JOIN MPA m ON(f.MPA_ID = m.MPA_ID) " +
                "LEFT OUTER JOIN LIKES_VS_FILM l ON (l.FILM_ID = f.FILM_ID) " +
                "JOIN GENRE_VS_FILM fg ON f.FILM_ID = fg.FILM_ID " +
                "WHERE fg.GENRE_ID = ? " +
                "GROUP BY f.FILM_ID, m.mpa_name " +
                "ORDER BY COUNT(l.USER_ID AND fg.GENRE_ID) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, count);
    }

    @Override
    public List<Film> getPopularFilmsByYear(int count, Integer year) {
        log.info("Получение списка популярных фильмов по лайкам и году");

        String sql = "SELECT f.*, m.mpa_name FROM FILMS f " +
                "JOIN MPA m ON(f.MPA_ID = m.MPA_ID) " +
                "LEFT OUTER JOIN LIKES_VS_FILM l ON (l.FILM_ID = f.FILM_ID) " +
                "WHERE EXTRACT(YEAR FROM f.RELEASE_DATE) = ? " +
                "GROUP BY f.FILM_ID, m.mpa_name " +
                "ORDER BY COUNT(l.USER_ID) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, year, count);
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