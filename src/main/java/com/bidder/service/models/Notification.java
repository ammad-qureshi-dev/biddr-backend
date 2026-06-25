/* (C) 2026 
bidder.app */
package com.bidder.service.models;

import java.util.UUID;

import com.bidder.service.utils.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

// ToDo: delete notification after 30days

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = Constants.Database.SCHEMA, name = "notification")
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"recipient"})
public class Notification extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	private AppUser recipient;

	@NotNull @Length(max = 64) private String title;

	@Length(max = 256) private String content;

	@Enumerated(EnumType.STRING)
	private NotificationStatus status;

	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@Enumerated(EnumType.STRING)
	private ContactMethod sentVia;
}
