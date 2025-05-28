package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.Review.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@AllArgsConstructor
public class ReviewController {
    @Autowired
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@RequestBody Review review) {
        log.info("Получен POST-запрос к эндпоинту: '/reviews' на добавление отзыва");
        return reviewService.create(review);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review update(@RequestBody Review newReview) {
        log.info("Получен PUT-запрос к эндпоинту: '/reviews' на редактирование отзыва");
        return reviewService.update(newReview);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable int id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/reviews/{id}' на удаление отзыва");
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review getReviewById(@PathVariable int id) {
        log.info("Получен GET-запрос к эндпоинту: '/reviews/{id}' на получение отзыва");
        return reviewService.getReviewById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Review> getReviewByFilmId(@RequestParam(required = false) Integer filmId,
                                          @RequestParam(required = false) Integer count) {
        if (filmId == null && count == null) {
            return reviewService.getAllReviews();
        }
        if (filmId == null) {
            return reviewService.getReviewLimit(count);
        }
        if (count == null) {
            count = 10;
        }
        return reviewService.getReviewByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void userLikesReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен PUT-запрос к эндпоинту: '/reviews/{id}/dislike/{userId}' " +
                +userId + "- пользователь лайкает отзыв " + id);
        reviewService.userLikesReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void userDislikesReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен PUT-запрос к эндпоинту: '/reviews/{id}/dislike/{userId}' " +
                +userId + "- пользователь дизлайкает отзыв " + id);
        reviewService.userDislikesReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUsersLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/reviews/{id}/like/{userId}' - пользователь удаляет лайк отзыву");
        reviewService.deleteUsersLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUsersDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/reviews/{id}/like/{userId}' - пользователь удаляет дизлайк отзыву");
        reviewService.deleteUsersDislike(id, userId);
    }
}
