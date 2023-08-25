package com.newyearletter.newyearletter.service;

import com.newyearletter.newyearletter.domain.dto.rabbit.RabbitMyPageResponse;
import com.newyearletter.newyearletter.domain.dto.rabbit.RabbitResponse;
import com.newyearletter.newyearletter.domain.dto.user.UserDto;
import com.newyearletter.newyearletter.domain.dto.user.UserJoinRequest;
import com.newyearletter.newyearletter.domain.dto.user.UserLoginResponse;
import com.newyearletter.newyearletter.domain.entity.RefreshToken;
import com.newyearletter.newyearletter.domain.entity.User;
import com.newyearletter.newyearletter.exception.AppException;
import com.newyearletter.newyearletter.exception.ErrorCode;
import com.newyearletter.newyearletter.repository.RefreshTokenRepository;
import com.newyearletter.newyearletter.repository.UserRepository;
import com.newyearletter.newyearletter.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    @Value("${jwt.token.secret}")
    private String key;

    /**
     * 회원가입
     */
    public UserDto join(UserJoinRequest request) {
        //중복 id 확인
        userRepository.findByUserID(request.getUserID())
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.DUPLICATED_USER_ID, request.getUserID()+"은 중복된 아이디입니다.");
                });


        //랜덤 URL Token 생성
        String uuid = UUID.randomUUID().toString();

        User savedUser = userRepository.save(request.toEntity(uuid, encoder.encode(request.getPassword())));

        return UserDto.builder()
                .userID(savedUser.getUserID())
                .password(savedUser.getPassword())
                .nickName(savedUser.getNickName())
                .uuid(savedUser.getUuid())
                .build();
    }



    /**
     * 마이페이지 조회
     */
    public RabbitMyPageResponse mypage(String userId) {
        //user 확인
        User user = userRepository.findByUserID(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_ID_NOT_FOUND,"해당 User을 찾을 수 없습니다"));
        LocalDateTime currentDateTime = LocalDateTime.now();

        return new RabbitMyPageResponse(user.getNickName(), user.getMoney(), user.getCustom(), user.getWish(), user.getUuid(),currentDateTime);
    }

    /**
     * 친구 페이지 조회
     */
    public RabbitResponse friendPage(String uuid) {
        //url이 유무 확인
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new AppException(ErrorCode.URL_NOT_FOUND, "해당 URL을 찾을 수 없습니다."));

        return new RabbitResponse(user.getNickName(), user.getWish(), user.getMoney(), user.getCustom());
    }

    /**
     * SpringSecurity userID확인
     */
    public User getUserByUserSeq(Long userSeq) {
        return userRepository.findById(userSeq)
                .orElseThrow(() -> new AppException(ErrorCode.USER_ID_NOT_FOUND,""));
    }
}
