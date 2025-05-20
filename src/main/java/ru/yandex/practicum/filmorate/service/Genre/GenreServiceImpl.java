package ru.yandex.practicum.filmorate.service.Genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Genre.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAllGenre() {
        return genreStorage.getAllGenre();
    }

    public Genre getGenre(int id) {
        if (id <= 0) {
            throw new NotFoundException("ID жанра отсутствует");
        } else {
            return genreStorage.getGenreById(id);
        }
    }
}
