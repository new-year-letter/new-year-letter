package com.newyearletter.newyearletter.configuration;

import com.newyearletter.newyearletter.domain.entity.User;
import com.newyearletter.newyearletter.exception.AppException;
import com.newyearletter.newyearletter.exception.ErrorCode;
import com.newyearletter.newyearletter.repository.RefreshTokenRepository;
import com.newyearletter.newyearletter.service.UserService;
import com.newyearletter.newyearletter.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Value("${jwt.token.secret}")
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        log.info("authorizationHeader:{}",authorizationHeader);

        //토큰이 없는 경우
        if(authorizationHeader == null) {
            request.setAttribute("exception", ErrorCode.INVALID_PERMISSION.name());
            filterChain.doFilter(request, response);
            return;
        }

        //bearer로 시작하는 토큰이 아닌 경우
        if(!authorizationHeader.startsWith("Bearer ")) {
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN.name());
            filterChain.doFilter(request, response);
            return;
        }

        //bearer 이후 문자열 token 분리 성공 실패
        String accessToken;

        try {
            accessToken = authorizationHeader.split(" ")[1];
            log.info("accessToken:{}",accessToken);
            //access token 유효성 검사
            if (!JwtTokenUtil.isValid(accessToken, secretKey).equals("OK")) {
                log.info("Exception Expired Token");
                request.setAttribute("exception", ErrorCode.INVALID_TOKEN.name());
//                request.setAttribute("exception", isValidToken);
                filterChain.doFilter(request, response);
                return;
            };

            // Token에서 UserSeq꺼내기 (JwtTokenUtil에서 Claim에서 꺼냄)
            Long userSeq = JwtTokenUtil.getUserSeq(accessToken, secretKey);
//            log.info("userSeq:{}", userSeq);
//            String userName = JwtTokenUtil.getUserName(accessToken, secretKey);
//            log.info("userName:{}", userName);
            User user = userService.getUserByUserSeq(userSeq);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserID(), null
                    , List.of(new SimpleGrantedAuthority("USER")));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //권한 부여
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request,response);


        } catch (Exception e) {
            log.info(e.getMessage());
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN.name());
            filterChain.doFilter(request, response);
        }

    }
}
