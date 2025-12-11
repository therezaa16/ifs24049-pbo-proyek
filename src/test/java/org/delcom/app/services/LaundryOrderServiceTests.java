package org.delcom.app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.entities.LaundryOrder;
import org.delcom.app.repositories.LaundryOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class LaundryOrderServiceTests {
    @Test
    @DisplayName("Pengujian untuk service LaundryOrder")
    void testLaundryOrderService() throws Exception {
        // Buat random UUID
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID nonexistentOrderId = UUID.randomUUID();

        // Membuat dummy data
        LaundryOrder order = new LaundryOrder(
            userId, 
            "John Doe", 
            "08123456789",
            "Cuci Setrika",
            5.0,
            35000.0,
            "Pending",
            "Pisahkan warna putih"
        );
        order.setId(orderId);

        // Membuat mock LaundryOrderRepository
        LaundryOrderRepository laundryOrderRepository = Mockito.mock(LaundryOrderRepository.class);

        // Atur perilaku mock
        when(laundryOrderRepository.save(any(LaundryOrder.class))).thenReturn(order);
        when(laundryOrderRepository.findByKeyword(userId, "John")).thenReturn(List.of(order));
        when(laundryOrderRepository.findAllByUserId(userId)).thenReturn(List.of(order));
        when(laundryOrderRepository.findByUserIdAndId(userId, orderId)).thenReturn(java.util.Optional.of(order));
        when(laundryOrderRepository.findByUserIdAndId(userId, nonexistentOrderId)).thenReturn(java.util.Optional.empty());
        when(laundryOrderRepository.existsById(orderId)).thenReturn(true);
        when(laundryOrderRepository.existsById(nonexistentOrderId)).thenReturn(false);
        doNothing().when(laundryOrderRepository).deleteById(any(UUID.class));
        when(laundryOrderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(laundryOrderRepository.findById(nonexistentOrderId)).thenReturn(java.util.Optional.empty());

        // Mock untuk statistics
        List<Object[]> statusData = new ArrayList<>();
        statusData.add(new Object[]{"Pending", 2L});
        statusData.add(new Object[]{"Proses", 1L});
        when(laundryOrderRepository.countByStatus(userId)).thenReturn(statusData);

        List<Object[]> serviceData = new ArrayList<>();
        serviceData.add(new Object[]{"Cuci Setrika", 3L});
        when(laundryOrderRepository.countByServiceType(userId)).thenReturn(serviceData);

        when(laundryOrderRepository.getTotalRevenue(userId)).thenReturn(100000.0);

        // Buat mock untuk FileStorageService
        FileStorageService fileStorageService = Mockito.mock(FileStorageService.class);

        // Membuat instance service
        LaundryOrderService laundryOrderService = new LaundryOrderService(laundryOrderRepository, fileStorageService);
        assert (laundryOrderService != null);

        // Menguji create laundry order
        {
            LaundryOrder createdOrder = laundryOrderService.createLaundryOrder(
                userId, 
                order.getCustomerName(),
                order.getPhoneNumber(),
                order.getServiceType(),
                order.getWeight(),
                order.getPrice(),
                order.getStatus(),
                order.getNotes()
            );
            assert (createdOrder != null);
            assert (createdOrder.getId().equals(orderId));
            assert (createdOrder.getCustomerName().equals(order.getCustomerName()));
            assert (createdOrder.getPhoneNumber().equals(order.getPhoneNumber()));
        }

        // Menguji getAllLaundryOrders
        {
            var orders = laundryOrderService.getAllLaundryOrders(userId, null);
            assert (orders.size() == 1);
        }

        // Menguji getAllLaundryOrders dengan pencarian
        {
            var orders = laundryOrderService.getAllLaundryOrders(userId, "John");
            assert (orders.size() == 1);

            orders = laundryOrderService.getAllLaundryOrders(userId, "     ");
            assert (orders.size() == 1);
        }

        // Menguji getLaundryOrderById
        {
            LaundryOrder fetchedOrder = laundryOrderService.getLaundryOrderById(userId, orderId);
            assert (fetchedOrder != null);
            assert (fetchedOrder.getId().equals(orderId));
            assert (fetchedOrder.getCustomerName().equals(order.getCustomerName()));
        }

        // Menguji getLaundryOrderById dengan ID yang tidak ada
        {
            LaundryOrder fetchedOrder = laundryOrderService.getLaundryOrderById(userId, nonexistentOrderId);
            assert (fetchedOrder == null);
        }

        // Menguji updateLaundryOrder
        {
            String updatedName = "Jane Doe";
            String updatedPhone = "08198765432";
            String updatedService = "Cuci Kering";
            Double updatedWeight = 3.0;
            Double updatedPrice = 15000.0;
            String updatedStatus = "Selesai";
            String updatedNotes = "Tanpa pewangi";

            LaundryOrder updatedOrder = laundryOrderService.updateLaundryOrder(
                userId, orderId, updatedName, updatedPhone, updatedService,
                updatedWeight, updatedPrice, updatedStatus, updatedNotes
            );
            assert (updatedOrder != null);
            assert (updatedOrder.getCustomerName().equals(updatedName));
            assert (updatedOrder.getPhoneNumber().equals(updatedPhone));
            assert (updatedOrder.getServiceType().equals(updatedService));
            assert (updatedOrder.getWeight().equals(updatedWeight));
            assert (updatedOrder.getPrice().equals(updatedPrice));
            assert (updatedOrder.getStatus().equals(updatedStatus));
            assert (updatedOrder.getNotes().equals(updatedNotes));
        }

        // Menguji update LaundryOrder dengan ID yang tidak ada
        {
            LaundryOrder updatedOrder = laundryOrderService.updateLaundryOrder(
                userId, nonexistentOrderId, "Test", "123", "Cuci Kering",
                1.0, 5000.0, "Pending", "Test"
            );
            assert (updatedOrder == null);
        }

        // Menguji deleteLaundryOrder
        {
            boolean deleted = laundryOrderService.deleteLaundryOrder(userId, orderId);
            assert (deleted == true);
        }

        // Menguji deleteLaundryOrder dengan order yang punya cover
        {
            order.setCover("test-cover.png");
            when(laundryOrderRepository.findByUserIdAndId(userId, orderId))
                .thenReturn(java.util.Optional.of(order));
            when(fileStorageService.deleteFile("test-cover.png")).thenReturn(true);
            
            boolean deleted = laundryOrderService.deleteLaundryOrder(userId, orderId);
            assert (deleted == true);
        }

        // Menguji deleteLaundryOrder dengan ID yang tidak ada
        {
            boolean deleted = laundryOrderService.deleteLaundryOrder(userId, nonexistentOrderId);
            assert (deleted == false);
        }

        // Menguji method updateCover dengan order kosong
        {
            orderId = UUID.randomUUID();
            when(laundryOrderRepository.findById(orderId)).thenReturn(java.util.Optional.empty());
            LaundryOrder updatedOrder = laundryOrderService.updateCover(orderId, "cover1.png");
            assert (updatedOrder == null);
        }

        // Menguji method updateCover dengan sebelumnya ada cover
        {
            // Data
            String newCoverFilename = "cover2.png";

            // Mock
            when(laundryOrderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));
            when(fileStorageService.deleteFile("cover1.png")).thenReturn(true);
            when(laundryOrderRepository.save(any(LaundryOrder.class))).thenReturn(order);

            order.setCover("cover1.png");
            LaundryOrder updatedOrder = laundryOrderService.updateCover(orderId, newCoverFilename);
            assert (updatedOrder != null);
            assert (updatedOrder.getCover().equals(newCoverFilename));
        }

        // Menguji method updateCover dengan sebelumnya belum ada cover
        {
            // Data
            String newCoverFilename = "cover2.png";

            // Mock
            when(laundryOrderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));
            when(fileStorageService.deleteFile("cover1.png")).thenReturn(true);
            when(laundryOrderRepository.save(any(LaundryOrder.class))).thenReturn(order);

            order.setCover(null);
            LaundryOrder updatedOrder = laundryOrderService.updateCover(orderId, newCoverFilename);
            assert (updatedOrder != null);
            assert (updatedOrder.getCover().equals(newCoverFilename));
        }

        // Menguji getStatistics
        {
            Map<String, Object> stats = laundryOrderService.getStatistics(userId);
            assert (stats != null);
            assert (stats.containsKey("statusChart"));
            assert (stats.containsKey("serviceChart"));
            assert (stats.containsKey("totalRevenue"));
            assert (stats.containsKey("totalOrders"));
            assert (stats.containsKey("pendingOrders"));
            assert (stats.containsKey("ongoingOrders"));

            // Verify statistics values
            assert (stats.get("totalRevenue").equals(100000.0));
            assert (stats.get("totalOrders").equals(1));
        }

        // Menguji getStatistics dengan totalRevenue null
        {
            when(laundryOrderRepository.getTotalRevenue(userId)).thenReturn(null);
            
            Map<String, Object> stats = laundryOrderService.getStatistics(userId);
            assert (stats != null);
            assert (stats.get("totalRevenue").equals(0.0)); // Should default to 0.0
        }
    }
}