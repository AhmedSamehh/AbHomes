package com.seha.abhomes.service;

import com.seha.abhomes.model.Apartment;
import com.seha.abhomes.repository.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;

    @Autowired
    public ApartmentService(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
    }

    public List<Apartment> getAllApartments() {
        return apartmentRepository.findAll();
    }

    public Optional<Apartment> getApartmentById(Long id) {
        return apartmentRepository.findById(id);
    }

    public Apartment createApartment(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }

    public Apartment updateApartment(Long id, Apartment apartmentDetails) {
        Apartment apartment = apartmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));

        apartment.setName(apartmentDetails.getName());
        apartment.setDescription(apartmentDetails.getDescription());
        apartment.setAddress(apartmentDetails.getAddress());
        apartment.setBedrooms(apartmentDetails.getBedrooms());
        apartment.setBathrooms(apartmentDetails.getBathrooms());
        apartment.setPricePerNight(apartmentDetails.getPricePerNight());
        apartment.setSquareMeters(apartmentDetails.getSquareMeters());
        apartment.setAmenities(apartmentDetails.getAmenities());
        apartment.setImageUrls(apartmentDetails.getImageUrls());

        return apartmentRepository.save(apartment);
    }

    public void deleteApartment(Long id) {
        Apartment apartment = apartmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));
        apartmentRepository.delete(apartment);
    }

    public List<Apartment> findAvailableApartments(LocalDateTime checkIn, LocalDateTime checkOut) {
        return apartmentRepository.findAvailableApartments(checkIn, checkOut);
    }

    public List<Apartment> findApartmentsByMaxPrice(Double maxPrice) {
        return apartmentRepository.findByPricePerNightLessThanEqual(maxPrice);
    }

    public List<Apartment> findApartmentsByMinBedrooms(Integer minBedrooms) {
        return apartmentRepository.findByBedroomsGreaterThanEqual(minBedrooms);
    }
}