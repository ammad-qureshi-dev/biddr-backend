/* (C) 2026 
bidder.app */
package com.bidder.service.repository;

import java.util.UUID;

import com.bidder.service.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, UUID> {

}
