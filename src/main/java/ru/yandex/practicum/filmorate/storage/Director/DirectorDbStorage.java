package ru.yandex.practicum.filmorate.storage.Director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("SELECT * FROM directors ORDER BY director_id");
        List<Director> listDirector = new ArrayList<>();
        while (resultSet.next()) {
            listDirector.add(Director.builder()
                    .directorId(resultSet.getInt(1))
                    .directorName(resultSet.getString(2))
                    .build());
        }
        log.info("Получен список всех жанров");
        return listDirector;
    }

    @Override
    public Director create(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO directors (director_name) VALUES (?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getDirectorName());
            return ps;
        }, keyHolder);
        Integer directorId = keyHolder.getKeyAs(Integer.class);

        director.setDirectorId(directorId);
        log.info("Добавлен новый фильм с ID={}", director.getDirectorId());
        return director;
    }

    @Override
    public Director update(Director director) {
        getDirectorById(director.getDirectorId());
        String sql = "UPDATE directors SET director_name = ? WHERE director_id = ?";
        jdbcTemplate.update(sql, director.getDirectorName(), director.getDirectorId());

        log.info("Добавлен новый режиссёр с ID={}", director.getDirectorId());
        return director;
    }

    @Override
    public Director getDirectorById(Integer directorId) {
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet("SELECT * FROM directors WHERE director_id = ?", directorId);
        if (resultSet.next()) {
            Director director = Director.builder()
                    .directorId(resultSet.getInt(1))
                    .directorName(resultSet.getString(2))
                    .build();
            log.info("Получена информация о режиссёре с ID={}", directorId);
            return director;
        }
        log.info("Режиссёр не найден с ID={}", directorId);
        throw new NotFoundException("Нет такого режиссёра !");
    }

    @Override
    public void delete(Integer directorId) {
        String sql = "DELETE from directors WHERE director_id = ?";
        jdbcTemplate.update(sql, directorId);
    }
}
