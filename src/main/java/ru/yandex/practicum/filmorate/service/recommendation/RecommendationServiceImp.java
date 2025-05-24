package ru.yandex.practicum.filmorate.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Film.FilmStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationServiceImp implements RecommendationService {
    private final FilmStorage filmStorage;

    @Override
    public List<Film> getRecommendations(int userId) {
        log.info("Рекомендация фильмов для пользователя с ID:{}", userId);

        // Получаем список фильмов, которым пользователь поставил лайк.
        Set<Integer> filmsLikedByUser = filmStorage.findFilmsLikedByUser(userId);

        if (filmsLikedByUser.isEmpty()) {
            log.debug("Пользователь с ID: {} не ставил лайк.", userId);
            return List.of();
        }

        // Получаем пересечений по лайкам пользователя user_id с другими пользователями
        Map<Integer, Integer> commonLikesCount = filmStorage.getCommonLikes(userId);

        if(commonLikesCount.isEmpty()) {
            log.debug("Лайки пользователя ни с кем не совпадают(пересекаются) ID: {}", userId);
            return List.of();
        }

        // Находим максимальное количество пересечений
        Collection<Integer> countLikes = commonLikesCount.values();
        int maxCount = Collections.max(countLikes);


        return List.of();
    }
}
