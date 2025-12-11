package org.delcom.app.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.delcom.app.entities.LaundryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LaundryOrderRepository extends JpaRepository<LaundryOrder, UUID> {
    
    // Pencarian berdasarkan keyword (nama customer, nomor telepon, atau notes)
    @Query("SELECT l FROM LaundryOrder l WHERE " +
           "(LOWER(l.customerName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(l.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(l.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND l.userId = :userId ORDER BY l.createdAt DESC")
    List<LaundryOrder> findByKeyword(UUID userId, String keyword);

    // Mengambil semua order berdasarkan userId
    @Query("SELECT l FROM LaundryOrder l WHERE l.userId = :userId ORDER BY l.createdAt DESC")
    List<LaundryOrder> findAllByUserId(UUID userId);

    // Mengambil order berdasarkan userId dan id
    @Query("SELECT l FROM LaundryOrder l WHERE l.id = :id AND l.userId = :userId")
    Optional<LaundryOrder> findByUserIdAndId(UUID userId, UUID id);

    // Query tambahan untuk fitur chart/statistik
    @Query("SELECT l.status, COUNT(l) FROM LaundryOrder l WHERE l.userId = :userId GROUP BY l.status")
    List<Object[]> countByStatus(UUID userId);

    @Query("SELECT l.serviceType, COUNT(l) FROM LaundryOrder l WHERE l.userId = :userId GROUP BY l.serviceType")
    List<Object[]> countByServiceType(UUID userId);

    @Query("SELECT SUM(l.price) FROM LaundryOrder l WHERE l.userId = :userId AND l.status = 'Selesai'")
    Double getTotalRevenue(UUID userId);
}