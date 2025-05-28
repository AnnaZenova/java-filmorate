package ru.yandex.practicum.filmorate.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Film.FilmStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationServiceImp implements RecommendationService {
    private final FilmStorage filmStorage;

    @Override
    public List<Film> getRecommendationsFilms(int userId) {
        log.info("Рекомендация фильмов для пользователя с ID:{}", userId);
        Map<Integer, Integer> commonLikesCount = filmStorage.getCommonLikes(userId);
        if (commonLikesCount.isEmpty()) {
            log.debug("Пересечения по лайкам у пользователя с ID: {}, отсутствуют", userId);
            return List.of();
        }
        int maxCommonCount = Collections.max(commonLikesCount.values());
        List<Integer> commonUsersByLikes = commonLikesCount.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCommonCount)
                .map(Map.Entry::getKey)
                .toList();
        log.debug("Список пересекаемых пользователей получен.");
        List<Integer> filmsLikedByUsers = filmStorage.findFilmsLikedByUser(userId);
        List<Film> recommendationFilms = commonUsersByLikes.stream()
                .flatMap(id -> filmStorage.findFilmsLikedByUser(id).stream())
                .filter(filmId -> !filmsLikedByUsers.contains(filmId))
                .map(filmStorage::getFilmById)
                .toList();
        log.debug("Список рекомендуемых фильмов сформирован.");
        return recommendationFilms;
    }
}
