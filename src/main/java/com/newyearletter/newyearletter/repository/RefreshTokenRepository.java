package com.newyearletter.newyearletter.repository;

import com.newyearletter.newyearletter.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    //user정보로 refreshToken을 찾기 위함
    Optional<RefreshToken> findByUserSeq(Long userSeq);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
