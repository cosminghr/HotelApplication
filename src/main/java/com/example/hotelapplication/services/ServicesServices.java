package com.example.hotelapplication.services;

import com.example.hotelapplication.dtos.ServicesDTO;
import com.example.hotelapplication.dtos.builders.ServicesBuilder;
import com.example.hotelapplication.entities.Services;
import com.example.hotelapplication.repositories.ServicesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing operations related to Services entities.
 */
@Service
public class ServicesServices {

    private final ServicesRepository servicesRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesServices.class);

    /**
     * Constructor for ServicesServices.
     *
     * @param servicesRepository The repository for Services entities.
     */
    public ServicesServices(ServicesRepository servicesRepository) {
        this.servicesRepository = servicesRepository;
    }

    /**
     * Retrieves a list of ServicesDTO objects representing all services in the database.
     *
     * @return List of ServicesDTO objects.
     */
    public List<ServicesDTO> findAllServices() {
        List<Services> servicesList = servicesRepository.findAll();
        return servicesList.stream()
                .map(ServicesBuilder::etoservicesDTO)
                .collect(Collectors.toList());
    }

    /**
     * Inserts a new service into the database.
     *
     * @param servicesDTO The ServicesDTO object to be inserted.
     * @return The generated UUID for the newly inserted service.
     */
    public UUID insertService(ServicesDTO servicesDTO) {
        Services services = ServicesBuilder.stoEnitity(servicesDTO);
        services = servicesRepository.save(services);
        LOGGER.debug("Service with id {} was inserted in db", services.getServiceId());
        return services.getServiceId();
    }

    /**
     * Retrieves a service by its ID.
     *
     * @param id The UUID of the service to retrieve.
     * @return The ServicesDTO object if found, null otherwise.
     */
    public ServicesDTO findServiceById(UUID id) {
        Optional<Services> optionalServices = servicesRepository.findById(id);
        if (optionalServices.isPresent()) {
            LOGGER.info("Service with id {} was found in db", id);
            return ServicesBuilder.etoservicesDTO(optionalServices.get());
        } else {
            LOGGER.error("Service with id {} was not found in db", id);
            return null;
        }
    }

    /**
     * Updates an existing service in the database based on the provided ServicesDTO.
     *
     * @param servicesDTO The updated ServicesDTO object.
     * @return The updated ServicesDTO object if the update is successful, null otherwise.
     */
    public ServicesDTO updateService(ServicesDTO servicesDTO) {
        Optional<Services> optionalServices = servicesRepository.findById(servicesDTO.getServiceId());
        if (optionalServices.isPresent()) {
            Services existingServices = optionalServices.get();
            existingServices.setServiceName(servicesDTO.getServiceName());
            existingServices.setServiceDescription(servicesDTO.getServiceDescription());
            existingServices.setServiceStatus(servicesDTO.getServiceStatus());
            existingServices.setServiceType(servicesDTO.getServiceType());
            existingServices.setServiceCost(servicesDTO.getServiceCost());

            Services updatedServices = servicesRepository.save(existingServices);
            LOGGER.debug("Service with id {} was updated in db.", existingServices.getServiceId());
            return ServicesBuilder.etoservicesDTO(updatedServices);
        } else {
            LOGGER.warn("Service with id {} not found. Update operation aborted.", servicesDTO.getServiceId());
            return null;
        }
    }

    /**
     * Deletes a service from the database based on the provided UUID.
     *
     * @param id The UUID of the service to be deleted.
     */
    public void deleteService(UUID id) {
        Optional<Services> optionalServices = servicesRepository.findById(id);
        if (optionalServices.isPresent()) {
            servicesRepository.deleteById(id);
            LOGGER.info("Services with id {} deleted successfully.", id);
        } else {
            LOGGER.info("Services with id {} not found. Delete operation aborted.", id);
        }
    }
}
