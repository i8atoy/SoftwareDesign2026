package com.softdesign.tourney.repository;

import com.softdesign.tourney.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findUserByUserName(String userName);
}
