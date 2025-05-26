package ru.yandex.practicum.filmorate.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Film.FilmStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationServiceImp implements RecommendationService {
    private final FilmStorage filmStorage;

    @Override
    public List<Film> getRecommendations(int userId) {
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

        // Пользователей с одинаковым кол-вом пересечений может быть несколько.
        List<Integer> commonUsersByLikes =  commonLikesCount.entrySet().stream()
                .filter(entry -> entry.getKey() == maxCommonCount)
                .map(Map.Entry::getValue)
                .toList();

        // Находим все фильмы пересекающихся пользователей, которые не были лайкнуты у самого пользователя.
        List<Integer> filmsLikedByUsers = filmStorage.findFilmsLikedByUser(userId);

        


        return List.of();
    }
}
