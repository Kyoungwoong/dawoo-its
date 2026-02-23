package com.example.common.dto;

import com.example.common.UserCount;
import java.util.List;

public record UsersConfig(int version, List<UserCount> users) {
}
