package net.chess_platform.user_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.chess_platform.user_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

}
