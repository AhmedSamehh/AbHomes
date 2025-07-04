package com.seha.abhomes.repository;

import com.seha.abhomes.model.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    List<Apartment> findByPricePerNightLessThanEqual(Double maxPrice);
    
    List<Apartment> findByBedroomsGreaterThanEqual(Integer minBedrooms);
    
    @Query("SELECT a FROM Apartment a WHERE a.id NOT IN " +
           "(SELECT b.apartment.id FROM Booking b " +
           "WHERE b.status = 'CONFIRMED' " +
           "AND ((b.checkInDate <= ?2 AND b.checkOutDate >= ?1) " +
           "OR (b.checkInDate <= ?1 AND b.checkOutDate >= ?1)))")
    List<Apartment> findAvailableApartments(LocalDateTime checkIn, LocalDateTime checkOut);
}