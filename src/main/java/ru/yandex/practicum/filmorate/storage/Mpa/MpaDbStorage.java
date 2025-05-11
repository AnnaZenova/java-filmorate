package ru.yandex.practicum.filmorate.storage.Mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MpaDbStorage implements MpaStorage {

    public final static int MPA_MIN_ID = 1;
    public final static int MPA_MAX_ID = 5;

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa ORDER BY mpa_id");
        List<Mpa> listMpa = new ArrayList<>();
        while (mpaRows.next()) {
            listMpa.add(new Mpa(mpaRows.getInt(1), mpaRows.getString(2)));
        }
        log.info("Получен список всех рейтингов");
        return listMpa;
    }

    @Override
    public Mpa getMpa(Integer mpaId) {
        if (mpaId < MPA_MIN_ID || mpaId > MPA_MAX_ID) {
            throw new NotFoundException("Рейтинга с таким ID нет");
        }
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE mpa_id = ?", mpaId);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(mpaRows.getInt("mpa_id"), mpaRows.getString("mpa_name"));
            log.info("Получен рейтинг с ID={}", mpaId);
            return mpa;
        } else {
            throw new NotFoundException("Не найден рейтинг с ID=" + mpaId);
        }
    }
}
