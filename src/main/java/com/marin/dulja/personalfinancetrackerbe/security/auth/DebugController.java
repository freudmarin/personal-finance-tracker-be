package com.marin.dulja.personalfinancetrackerbe.security.auth;

import com.marin.dulja.personalfinancetrackerbe.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private static final Logger log = LoggerFactory.getLogger(DebugController.class);

    @GetMapping("/whoami")
    public Map<String, Object> whoAmI(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("DEBUG /whoami called");
        Map<String, Object> response = new HashMap<>();

        if (userDetails != null) {
            response.put("authenticated", true);
            response.put("userId", userDetails.getUserId().toString());
            response.put("email", userDetails.getUsername());
            response.put("authorities", userDetails.getAuthorities().toString());
            log.info("DEBUG - User authenticated: userId={}, email={}",
                    userDetails.getUserId(), userDetails.getUsername());
        } else {
            response.put("authenticated", false);
            log.warn("DEBUG - No authenticated user found");
        }

        return response;
    }
}

