package com.seha.abhomes.repository;

import com.seha.abhomes.model.Booking;
import com.seha.abhomes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    
    List<Booking> findByUserOrderByCreatedAtDesc(User user);
    
    List<Booking> findByApartmentIdAndStatus(
        Long apartmentId,
        Booking.BookingStatus status
    );
    
    List<Booking> findByCheckInDateBetweenAndStatus(
        LocalDateTime start,
        LocalDateTime end,
        Booking.BookingStatus status
    );
    
    boolean existsByApartmentIdAndStatusAndCheckInDateLessThanAndCheckOutDateGreaterThan(
        Long apartmentId,
        Booking.BookingStatus status,
        LocalDateTime checkOut,
        LocalDateTime checkIn
    );
}