/* (C) 2026 
bidder.app */
package com.bidder.service.mappers;

public interface Mapper<T> {

	Object entityToResponse(T entity);
	T responseToEntity(Object entity);
}
