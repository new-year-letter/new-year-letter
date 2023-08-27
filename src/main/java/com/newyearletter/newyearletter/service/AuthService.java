package com.newyearletter.newyearletter.service;

import com.newyearletter.newyearletter.domain.dto.auth.AuthLogoutResponse;
import com.newyearletter.newyearletter.domain.dto.auth.AuthReissueRequest;
import com.newyearletter.newyearletter.domain.dto.auth.AuthReissueResponse;
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
        String newRefreshToken = JwtTokenUtil.createRefreshToken(key);
        log.info("refreshToken: {}",newRefreshToken);

        //refresh token 있는지 확인
        // token이 있는 경우
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserSeq(user.getSeq());
        if(refreshToken.isPresent()){
            refreshTokenRepository.save(refreshToken.get().updateToken(newRefreshToken));
        //token이 없는 경우
        }else{
            RefreshToken saveRefreshToken = new RefreshToken().createToken(user.getSeq(), newRefreshToken);
            refreshTokenRepository.save(saveRefreshToken);
        }

        return new UserLoginResponse(accessToken, newRefreshToken);
    }

    /**
     * 로그아웃 로직
     * @param userID
     * @return message
     */
    public AuthLogoutResponse logout(String userID) {
        //userID 확인
        User user = userRepository.findByUserID(userID)
                .orElseThrow(()-> new AppException(ErrorCode.USER_ID_NOT_FOUND, userID+"이 없습니다."));

        //refeshToken DB에서 해당 userID를 가진 토큰 삭제
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserSeq(user.getSeq());
        if(refreshToken.isPresent()){
            //token 삭제
            log.info("Delete RefreshToken: {}", refreshToken);
            refreshTokenRepository.deleteById(refreshToken.get().getId());
        }else{
            throw new AppException(ErrorCode.INVALID_PERMISSION,ErrorCode.INVALID_PERMISSION.getMessage());
        }
    return new AuthLogoutResponse("success logout");
    }


    public AuthReissueResponse reissue(AuthReissueRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        //refresh token 유효성 검사
        if(requestRefreshToken == null){
            throw new AppException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }
        String isValidToken = JwtTokenUtil.isValid(requestRefreshToken, key);
        if( !isValidToken.equals("OK")){
            throw new AppException(ErrorCode.EXPIRE_TOKEN, isValidToken);
        }

        //Access Token 재발급
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken());
        if(!refreshToken.isPresent()){
            throw new AppException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }
        Optional<User> user = userRepository.findById(refreshToken.get().getUserSeq());
        //access token 생성
        String accessToken = JwtTokenUtil.createAccessToken(user.get().getUserID(), user.get().getSeq(), key);

        return new AuthReissueResponse(accessToken);
    }
}
