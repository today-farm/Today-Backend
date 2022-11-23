package com.today.todayproject.global.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.jwt.service.JwtService;
import com.today.todayproject.global.login.dto.LoginSuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJWTProviderHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) 
            throws IOException, ServletException {

        String email = extractUsername(authentication); // 인증 정보에서 Username(email) 추출
        String accessToken = jwtService.createAccessToken(email); // JwtServiceImpl의 createAccessToken을 사용하여 AccessToken 발급
        String refreshToken = jwtService.createRefreshToken(); // JwtServiceImpl의 createRefreshToken을 사용하여 RefreshToken 발급

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 헤더에 accessToken, refreshToken을 실어서 요청

        // DB에 refreshToken 저장
        userRepository.findByEmail(email).ifPresent(
                user -> {
                    user.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(user);
                }
        );

        try {
            LoginSuccessResponseDto loginSuccessResponseDto = new LoginSuccessResponseDto(extractUserId(authentication));
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(objectMapper.writeValueAsString(loginSuccessResponseDto));
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }

        log.info( "로그인에 성공합니다. eamil: {}" ,email);
        log.info( "AccessToken 을 발급합니다. AccessToken: {}" ,accessToken);
        log.info( "RefreshToken 을 발급합니다. RefreshToken: {}" ,refreshToken);
        log.info("Access Token 만료 기간 : {}", accessTokenExpiration);
    }


    private String extractUsername(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    private Long extractUserId(Authentication authentication) throws BaseException {
        String userEmail = extractUsername(authentication);
        User findUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_EMAIL));
        return findUser.getId();
    }
}
