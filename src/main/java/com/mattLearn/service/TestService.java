package com.mattLearn.service;

import org.springframework.stereotype.Service;

/**
 * modified by Matt on Jan 12th 2021
 */
@Service
public class TestService {
    public String getMessage(int userId) {
        return "Hello Message:" + String.valueOf(userId);
    }
}
