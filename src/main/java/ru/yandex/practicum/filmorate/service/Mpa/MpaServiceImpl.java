package ru.yandex.practicum.filmorate.service.Mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Mpa.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Override
    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    @Override
    public Mpa getMpa(int mpaId) throws NotFoundException {
        if (mpaId <= 0) {
            throw new NotFoundException("Mpa not found");
        }
        return mpaStorage.getMpa(mpaId);
    }
}