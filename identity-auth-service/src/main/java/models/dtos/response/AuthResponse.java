package models.dtos.response;

import java.util.UUID;

public record AuthResponse(String token, UUID userId) {
}
