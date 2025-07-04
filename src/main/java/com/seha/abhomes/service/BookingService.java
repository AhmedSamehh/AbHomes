package com.seha.abhomes.service;

import com.seha.abhomes.model.Apartment;
import com.seha.abhomes.model.Booking;
import com.seha.abhomes.model.User;
import com.seha.abhomes.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ApartmentService apartmentService;

    @Autowired
    public BookingService(BookingRepository bookingRepository, ApartmentService apartmentService) {
        this.bookingRepository = bookingRepository;
        this.apartmentService = apartmentService;
    }

    public Booking createBooking(Booking booking) {
        validateBookingDates(booking);
        validateApartmentAvailability(booking);
        calculateTotalPrice(booking);
        booking.setStatus(Booking.BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getUserBookings(User user) {
        return bookingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    private void validateBookingDates(Booking booking) {
        if (booking.getCheckInDate().isAfter(booking.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }
        if (booking.getCheckInDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Check-in date must be in the future");
        }
    }

    private void validateApartmentAvailability(Booking booking) {
        boolean isOverlapping = bookingRepository
                .existsByApartmentIdAndStatusAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        booking.getApartment().getId(),
                        Booking.BookingStatus.CONFIRMED,
                        booking.getCheckOutDate(),
                        booking.getCheckInDate());

        if (isOverlapping) {
            throw new RuntimeException("Apartment is not available for the selected dates");
        }
    }

    private void calculateTotalPrice(Booking booking) {
        Apartment apartment = apartmentService.getApartmentById(booking.getApartment().getId())
                .orElseThrow(() -> new RuntimeException("Apartment not found"));

        long numberOfNights = ChronoUnit.DAYS.between(
                booking.getCheckInDate().toLocalDate(),
                booking.getCheckOutDate().toLocalDate());

        BigDecimal totalPrice = apartment.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));
        booking.setTotalPrice(totalPrice);
    }

    public List<Booking> getApartmentBookings(Long apartmentId, Booking.BookingStatus status) {
        return bookingRepository.findByApartmentIdAndStatus(apartmentId, status);
    }

    public List<Booking> getBookingsByDateRange(LocalDateTime start, LocalDateTime end, Booking.BookingStatus status) {
        return bookingRepository.findByCheckInDateBetweenAndStatus(start, end, status);
    }
}