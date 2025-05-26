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

        // Получаем пересечения по лайкам пользователя user_id с другими пользователями.
        // key(кол-во пересечений), value(user_id)
        Map<Integer, Integer> commonLikesCount = filmStorage.getCommonLikes(userId);

        if (commonLikesCount.isEmpty()) {
            log.debug("Пересечения по лайкам у пользователя с ID: {}, отсутствуют", userId);
            return List.of();
        }

        // Находим максимальное количество пересечений
        int maxCommonCount = Collections.max(commonLikesCount.values());

        // Пользователей с одинаковым кол-вом пересечений может быть несколько. Получаем id пересекаемых пользователей.
        List<Integer> commonUsersByLikes = commonLikesCount.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCommonCount)
                .map(Map.Entry::getKey)
                .toList();
        log.debug("Список пересекаемых пользователей получен.");

        // Получаем все фильмы которым пользователь ставил лайки.
        List<Integer> filmsLikedByUsers = filmStorage.findFilmsLikedByUser(userId);

        // Пробегаемся по пересекаемым пользователям, получаем списки лайкнутых ими фильмов,
        // сравниваем с фильмами пользователя, если лайка нет, то получаем фильм из таблицы по id и записываем в итоговый список.
        List<Film> recommendationFilms = commonUsersByLikes.stream()
                .flatMap(id -> filmStorage.findFilmsLikedByUser(id).stream())
                .filter(filmId -> !filmsLikedByUsers.contains(filmId))
                .map(filmStorage::getFilmById)
                .toList();
        log.debug("Список рекомендуемых фильмов сформирован.");
        return recommendationFilms;
    }
}
