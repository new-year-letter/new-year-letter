package com.newyearletter.newyearletter.service;

import com.newyearletter.newyearletter.domain.dto.auth.AuthLogoutRequest;
import com.newyearletter.newyearletter.domain.dto.auth.AuthLogoutResponse;
import com.newyearletter.newyearletter.domain.dto.user.UserLoginResponse;
import com.newyearletter.newyearletter.domain.dto.user.UserLogoutRequest;
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

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String key;

    /**
     * 로그인
     */
    public UserLoginResponse login(String userID, String password) {
        //userID 확인
        User user = userRepository.findByUserID(userID)
                .orElseThrow(()-> new AppException(ErrorCode.USER_ID_NOT_FOUND, userID+"이 없습니다."));
        //password 확인
        if(!encoder.matches(password,user.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD,"password가 일치하지 않습니다.");
        }
        //access token 생성
        String accessToken = JwtTokenUtil.createAccessToken(userID, user.getSeq(), key);
        log.info("accessToken: {}",accessToken);

        //refresh token 생성
        String refreshToken = JwtTokenUtil.createRefreshToken(key);
        log.info("refreshToken: {}",refreshToken);

        //db에 refreshToken 저장
        RefreshToken saveRefreshToken = RefreshToken.builder()
                .userSeq(user.getSeq())
                .refreshToken((refreshToken))
                .build();
        refreshTokenRepository.save(saveRefreshToken);

        return new UserLoginResponse(accessToken, refreshToken);
    }

    /**
     * 로그아웃 로직
     * @param userID
     * @return message
     */
    public AuthLogoutResponse logout(String userID) {
        //로그인한 유저 확인
        Optional<User> user = userRepository.findByUserID(userID);

        //refeshToken DB에서 해당 userID를 가진 토큰 삭제
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserSeq(user.get().getSeq());
        if(refreshToken.isPresent()){
            //token 삭제
            refreshTokenRepository.deleteById(refreshToken.get().getId());
        }else{
            throw new AppException(ErrorCode.INVALID_PERMISSION,ErrorCode.INVALID_PERMISSION.getMessage());
        }
    return new AuthLogoutResponse("success logout");
    }


}
