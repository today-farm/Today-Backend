package com.today.todayproject.global.jwt.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public interface JwtService {

    String createAccessToken(String email);

    String createRefreshToken();

    void updateRefreshToken(String email, String refreshToken);

    void deleteRefreshToken(String email);

    void sendAccessToken(HttpServletResponse response, String accessToken);

    void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken);

    Optional<String> extractAccessToken(HttpServletRequest request) throws IOException, ServletException;

    Optional<String> extractRefreshToken(HttpServletRequest request) throws IOException, ServletException;

    Optional<String> extractEmail(String accessToken);

    void setAccessTokenHeader(HttpServletResponse response, String accessToken);

    void setRefreshTokenHeader(HttpServletResponse response, String refreshToken);

    boolean isTokenValid(String token);

}
