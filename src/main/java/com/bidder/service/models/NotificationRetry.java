/* (C) 2026 
bidder.app */
package com.bidder.service.models;

import java.util.UUID;

import com.bidder.service.utils.Constants;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = Constants.Database.SCHEMA, name = "notification_retry")
@EqualsAndHashCode(callSuper = true)
public class NotificationRetry extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@OneToOne
	private Notification notification;
}
