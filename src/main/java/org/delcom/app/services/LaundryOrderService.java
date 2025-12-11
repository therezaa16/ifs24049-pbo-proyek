package org.delcom.app.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.delcom.app.entities.LaundryOrder;
import org.delcom.app.repositories.LaundryOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LaundryOrderService {
    private final LaundryOrderRepository laundryOrderRepository;
    private final FileStorageService fileStorageService;

    public LaundryOrderService(LaundryOrderRepository laundryOrderRepository, 
                               FileStorageService fileStorageService) {
        this.laundryOrderRepository = laundryOrderRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Membuat order laundry baru
     */
    @Transactional
    public LaundryOrder createLaundryOrder(UUID userId, String customerName, String phoneNumber,
                                          String serviceType, Double weight, Double price,
                                          String status, String notes) {
        LaundryOrder order = new LaundryOrder(userId, customerName, phoneNumber, 
                                             serviceType, weight, price, status, notes);
        return laundryOrderRepository.save(order);
    }

    /**
     * Mengambil semua order dengan opsi pencarian
     */
    public List<LaundryOrder> getAllLaundryOrders(UUID userId, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return laundryOrderRepository.findByKeyword(userId, search);
        }
        return laundryOrderRepository.findAllByUserId(userId);
    }

    /**
     * Mengambil order berdasarkan ID
     */
    public LaundryOrder getLaundryOrderById(UUID userId, UUID id) {
        return laundryOrderRepository.findByUserIdAndId(userId, id).orElse(null);
    }

    /**
     * Memperbarui order laundry
     */
    @Transactional
    public LaundryOrder updateLaundryOrder(UUID userId, UUID id, String customerName, 
                                          String phoneNumber, String serviceType, 
                                          Double weight, Double price, String status, 
                                          String notes) {
        LaundryOrder order = laundryOrderRepository.findByUserIdAndId(userId, id).orElse(null);
        if (order != null) {
            order.setCustomerName(customerName);
            order.setPhoneNumber(phoneNumber);
            order.setServiceType(serviceType);
            order.setWeight(weight);
            order.setPrice(price);
            order.setStatus(status);
            order.setNotes(notes);
            return laundryOrderRepository.save(order);
        }
        return null;
    }

    /**
     * Menghapus order laundry
     */
    @Transactional
    public boolean deleteLaundryOrder(UUID userId, UUID id) {
        LaundryOrder order = laundryOrderRepository.findByUserIdAndId(userId, id).orElse(null);
        if (order == null) {
            return false;
        }

        // Hapus file cover jika ada
        if (order.getCover() != null) {
            fileStorageService.deleteFile(order.getCover());
        }

        laundryOrderRepository.deleteById(id);
        return true;
    }

    /**
     * Memperbarui cover image order
     */
    @Transactional
    public LaundryOrder updateCover(UUID orderId, String coverFilename) {
        Optional<LaundryOrder> orderOpt = laundryOrderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            LaundryOrder order = orderOpt.get();

            // Hapus file cover lama jika ada
            if (order.getCover() != null) {
                fileStorageService.deleteFile(order.getCover());
            }

            order.setCover(coverFilename);
            return laundryOrderRepository.save(order);
        }
        return null;
    }

    /**
     * Mendapatkan statistik untuk chart
     */
    public Map<String, Object> getStatistics(UUID userId) {
        Map<String, Object> stats = new HashMap<>();

        // Chart 1: Status distribution
        List<Object[]> statusData = laundryOrderRepository.countByStatus(userId);
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] row : statusData) {
            statusMap.put((String) row[0], (Long) row[1]);
        }
        stats.put("statusChart", statusMap);

        // Chart 2: Service type distribution
        List<Object[]> serviceData = laundryOrderRepository.countByServiceType(userId);
        Map<String, Long> serviceMap = new HashMap<>();
        for (Object[] row : serviceData) {
            serviceMap.put((String) row[0], (Long) row[1]);
        }
        stats.put("serviceChart", serviceMap);

        // Chart 3: Total revenue
        Double totalRevenue = laundryOrderRepository.getTotalRevenue(userId);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

        // Additional stats
        List<LaundryOrder> allOrders = laundryOrderRepository.findAllByUserId(userId);
        stats.put("totalOrders", allOrders.size());

        long pendingCount = allOrders.stream()
            .filter(o -> "Pending".equals(o.getStatus()))
            .count();
        stats.put("pendingOrders", pendingCount);

        long prosesCount = allOrders.stream()
            .filter(o -> "Proses".equals(o.getStatus()))
            .count();
        stats.put("ongoingOrders", prosesCount);

        return stats;
    }
}