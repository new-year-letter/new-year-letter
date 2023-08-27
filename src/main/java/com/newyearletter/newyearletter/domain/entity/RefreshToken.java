package com.newyearletter.newyearletter.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long userSeq;
    private String refreshToken;

    public RefreshToken updateToken(String newRefreshToken) {
        return RefreshToken.builder()
                .id(this.id)
                .userSeq(this.userSeq)
                .refreshToken(newRefreshToken)
                .build();
    }

    public RefreshToken createToken(Long userSeq, String refreshToken){
        return RefreshToken.builder()
                .userSeq(userSeq)
                .refreshToken(refreshToken)
                .build();
    }
}
