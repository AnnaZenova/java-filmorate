package ru.yandex.practicum.filmorate.service.Genre;
import ru.yandex.practicum.filmorate.model.Genre;
import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

public interface GenreService {
    List<Genre> getAllGenre();

    Genre getGenre(int id) throws AccountNotFoundException;
}
