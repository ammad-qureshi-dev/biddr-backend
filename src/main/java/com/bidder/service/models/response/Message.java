/* (C) 2026 
bidder.app */
package com.bidder.service.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
	private MessageType type;
	private MessageCode code;
	private String content;

	public static Message generateMessage(MessageType type, MessageCode code) {
		return Message.builder().type(type).code(code).content(code.getMessage()).build();
	}
}
