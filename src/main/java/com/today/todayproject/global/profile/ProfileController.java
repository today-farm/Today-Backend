package com.today.todayproject.global.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final Environment env;

    private static final String PROFILE_1 = "real1";
    private static final String PROFILE_2 = "real2";
    private static final String DEFAULT_PROFILE = "default";

    @GetMapping("/profile")
    public String getProfile() {
        List<String> profiles = Arrays.asList(env.getActiveProfiles());
        List<String> profileNames = Arrays.asList(PROFILE_1, PROFILE_2);
        String defaultProfile = "";
        if (profiles.isEmpty()) {
            defaultProfile = DEFAULT_PROFILE;
        }
        if (!profiles.isEmpty()) {
            defaultProfile = profiles.get(0);
        }

        return profiles.stream()
                .filter(profileNames::contains)
                .findAny()
                .orElse(defaultProfile);
    }
}

