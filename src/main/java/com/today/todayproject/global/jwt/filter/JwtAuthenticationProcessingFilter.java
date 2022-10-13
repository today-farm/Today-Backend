package com.today.todayproject.global.jwt.filter;

import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Jwt 인증 필터
 * 1. 둘 다 유효한 경우 -> AccessToken 재발급, 인증은 진행하지 않음.
 * 2. RefreshToken은 유효하고, AccessToken은 없거나 유효하지 않은 경우 -> AccessToken 재발급 : 응답 헤더에 재발급
 * 3. RefreshToken은 없거나 유효하지 않고, AccessToken은 유효한 경우 -> 인증은 성공되나, RefreshToken을 재발급하지는 않음
 * 4. RefreshToken과 AccessToken 모두 없거나 유효하지 않은 경우 -> 인증에 실패합니다. 403을 제공
 */
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private final String NO_CHECK_URL = "/login"; // "/login"으로 들어오는 요청은 Filter 작동 X

    /**
     * 1. 리프레시 토큰이 오는 경우 -> 유효하면 AccessToken 재발급후, 필터 진행 X, 바로 튕기기
     *
     * 2. 리프레시 토큰은 없고 AccessToken만 있는 경우 -> 유저정보 저장후 필터 계속 진행
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;//안해주면 아래로 내려가서 계속 필터를 진행해버림
        }

        // RefreshToken이 없거나 유효하지 않다면 null을 반환
        String refreshToken = jwtService.extractRefreshToken(request).filter(jwtService::isTokenValid).orElse(null);

        // refreshToken이 유효하다면 해당 refreshToken을 가진 유저정보를 찾아오고, 존재한다면 AccessToken을 재발급(checkRefreshTokenAndReIssueAccessToken() 메소드)
        if(refreshToken != null){
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return; // 바로 return 하는 이유는, refreshToken을 보낸 경우에는 인증 처리를 하지않기 위해서 AccessToken 재발급 후 필터에서 튕기기
        }

        // refreshToken이 없다면 AccessToken을 검사하는 로직을 수행
        checkAccessTokenAndAuthentication(request, response, filterChain);

    }


    /**
     * 액세스 토큰 체크 & 인증 메소드
     *
     * request에서 extractAccessToken()으로 액세스 토큰 추출 후, isTokenValid()로 유효한 토큰인지 검증
     * 그 후 액세스 토큰에서 extractEmail로 Email을 추출한 후 findByEmail()로 해당 이메일을 사용하는 유저 객체 반환
     * 그 유저 객체를 saveAuthentication()으로 인증 처리
     */
    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        jwtService.extractAccessToken(request).filter(jwtService::isTokenValid).ifPresent(

                accessToken -> jwtService.extractEmail(accessToken).ifPresent(

                        email -> userRepository.findByEmail(email).ifPresent(

                                this::saveAuthentication
                        )
                )
        );

        filterChain.doFilter(request,response);
    }

    /**
     * 인증 허가 메소드
     *
     * 여기서의 User는 우리가 만든 회원이 아니라, userDetails의 User (파라미터의 User는 인증을 허가할 우리가 만든 유저 객체)
     * UsernamePasswordAuthenticationToken은 SecurityContextHolder에 등록될 Authentication 객체 (인증 객체)
     *
     * UsernamePasswordAuthenticationToken 파라미터
     * 1. 위에서 만든 UserDetails 객체(유저 정보),
     * 2. credential(보통 비밀번호, 인증 시에는 보통 null로 제거),
     * 3. Collection < ? extends GrantedAuthority> authorities
     *
     * 1번 파라미터는 UserDetails를 builder()로 만들어서 주입
     * 2번 파라미터는 null
     * 3번 파라미터는 NullAuthoritiesMapper를 사용하여 GrantedAuthoritiesMapper 객체를 생성하고 UserDetails의 getAuthorities()를 이용
     *
     * SecurityContextHolder.createEmptyContext()로 SecurityContext 객체를 생성한 후
     * setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
     * 그 후 인증 허가 처리된 객체를 setContext()를 통해 SecurityContextHolder 에 담기
     */
    private void saveAuthentication(com.today.todayproject.domain.user.User myUser) {
        UserDetails user = User.builder()
                .username(myUser.getEmail())
                .password(myUser.getPassword())
                .roles(myUser.getRole().name())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,authoritiesMapper.mapAuthorities(user.getAuthorities()));


        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    /**
     * 리프레시 토큰 체크 & 액세스 토큰 재발급 메소드
     *
     * DB에서 파라미터의 리프레시 토큰을 찾고, 해당하는 리프레시 토큰이 있다면
     * createAccessToken으로 AccessToken 생성 후 sendAccessToken으로 헤더에 보내기
     */
    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        userRepository.findByRefreshToken(refreshToken).ifPresent(
                user -> jwtService.sendAccessToken(response, jwtService.createAccessToken(user.getEmail()))
        );


    }
}
