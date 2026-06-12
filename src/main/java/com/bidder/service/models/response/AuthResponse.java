/* (C) 2026 
bidder.app */
package com.bidder.service.models.response;

import java.util.UUID;

public record AuthResponse(String token, UUID userId) {
}
