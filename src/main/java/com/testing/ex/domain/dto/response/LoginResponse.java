package com.testing.ex.domain.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
  private String token;
  private Long expiration;
}
