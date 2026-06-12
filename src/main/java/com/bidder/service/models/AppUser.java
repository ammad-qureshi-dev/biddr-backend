/* (C) 2026 
bidder.app */
package com.bidder.service.models;

import java.util.List;
import java.util.UUID;

import com.bidder.service.utils.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = Constants.Database.SCHEMA, name = "app_user")
@EqualsAndHashCode(callSuper = true)
public class AppUser extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	// Entity Relationships
	@OneToMany(mappedBy = "bidder", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Bid> bids;

	@NotNull private String fullName;

	@NotNull private String password;

	private String email;

	// ToDo: to be broken down
	private String phoneNumber;
}
