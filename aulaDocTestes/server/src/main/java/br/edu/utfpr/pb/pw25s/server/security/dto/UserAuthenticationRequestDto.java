package br.edu.utfpr.pb.pw25s.server.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAuthenticationRequestDto {

    private Long id;

    private String username;

    private String password;

}
