package com.casemgr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.entity.Favourite;
import com.casemgr.response.FavouriteResponse;
import com.casemgr.service.impl.FavouriteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Favorites Management", description = "APIs for managing user favorites, bookmarks, and preferred items")
@RestController
@RequestMapping("/api/favourities")
@CrossOrigin
@RequiredArgsConstructor
public class FavouriteController {
	private final FavouriteService favouriteService;
	
	/**
	 * Get current user's favorites.
	 * @return User's favorite items and preferences
	 */
	@GetMapping
	@Operation(
		summary = "Get My Favorites",
		description = "Retrieve the current authenticated user's favorite items, bookmarks, and preferences. " +
					  "This includes saved projects, preferred service providers, and other bookmarked content."
	)
	public ResponseEntity<FavouriteResponse> getMyFavourities() {
		Favourite favourite = favouriteService.getMyFavourite();
		return ResponseEntity.ok(new FavouriteResponse(favourite));
	}
	
	

}
