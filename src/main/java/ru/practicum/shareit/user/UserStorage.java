package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserStorage extends JpaRepository<User, Long> {

    User save(User user);

    List<User> findByEmailContainingIgnoreCase(String emailSearch);

    Optional<User> findById(Long userId);

    List<User> findAll();

    void deleteById(Long id);
}
