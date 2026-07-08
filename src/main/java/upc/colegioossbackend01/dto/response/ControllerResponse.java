package upc.colegioossbackend01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ControllerResponse {

    private boolean success;
    private String message;
    private Object data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static ControllerResponse ok(Object data, String message) {
        return ControllerResponse.builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static ControllerResponse error(String message) {
        return ControllerResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}