/* (C) 2026 
bidder.app */
package com.bidder.service.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.bidder.service.utils.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = Constants.Database.SCHEMA, name = "item")
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"auction", "bids", "highestBid"})
public class Item extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	// Entity relationships
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction")
	private Auction auction;

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Bid> bids;

	@OneToOne
	private Bid highestBid;

	@NotNull @Length(max = 128) private String title;

	@Length(max = 512) private String description;

	@DecimalMin("0.0")
	private BigDecimal minimumPrice;

	@DecimalMin("0.0")
	private BigDecimal priceSoldAt;

	@OneToOne
	private Bid acceptedBid;

	// For optimistic locking
	@Version
	private Long version;
}
