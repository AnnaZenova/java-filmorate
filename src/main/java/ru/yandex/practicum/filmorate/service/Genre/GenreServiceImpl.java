package ru.yandex.practicum.filmorate.service.Genre;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Genre.GenreStorage;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    public GenreServiceImpl(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenre() {
        return genreStorage.getAllGenre();
    }

    public Genre getGenre(int id) throws AccountNotFoundException {
        if (id <= 0) {
            throw new AccountNotFoundException();
        }
        return genreStorage.getGenreById(id);
    }
}
