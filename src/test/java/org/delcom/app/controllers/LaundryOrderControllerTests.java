package org.delcom.app.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.LaundryOrder;
import org.delcom.app.entities.User;
import org.delcom.app.services.LaundryOrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.http.ResponseEntity;

public class LaundryOrderControllerTests {
    @Test
    @DisplayName("Pengujian untuk controller LaundryOrder")
    void testLaundryOrderController() throws Exception {
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
            "Pisahkan warna"
        );
        order.setId(orderId);

        // Membuat mock LaundryOrderService
        LaundryOrderService laundryOrderService = Mockito.mock(LaundryOrderService.class);

        // Atur perilaku mock
        when(laundryOrderService.createLaundryOrder(
            any(UUID.class), any(String.class), any(String.class), 
            any(String.class), any(Double.class), any(Double.class),
            any(String.class), any(String.class)
        )).thenReturn(order);

        // Membuat instance controller
        LaundryOrderController laundryOrderController = new LaundryOrderController(laundryOrderService);
        assert (laundryOrderController != null);

        laundryOrderController.authContext = new AuthContext();
        User authUser = new User("Test User", "testuser@example.com");
        authUser.setId(userId);

        // Menguji method createLaundryOrder
        {
            // Data tidak valid
            {
                List<LaundryOrder> invalidOrders = List.of(
                    // Customer name null
                    new LaundryOrder(userId, null, "08123456789", "Cuci Kering", 5.0, 25000.0, "Pending", "Test"),
                    // Customer name empty
                    new LaundryOrder(userId, "", "08123456789", "Cuci Kering", 5.0, 25000.0, "Pending", "Test"),
                    // Phone number null
                    new LaundryOrder(userId, "John Doe", null, "Cuci Kering", 5.0, 25000.0, "Pending", "Test"),
                    // Phone number empty
                    new LaundryOrder(userId, "John Doe", "", "Cuci Kering", 5.0, 25000.0, "Pending", "Test"),
                    // Service type null
                    new LaundryOrder(userId, "John Doe", "08123456789", null, 5.0, 25000.0, "Pending", "Test"),
                    // Service type empty
                    new LaundryOrder(userId, "John Doe", "08123456789", "", 5.0, 25000.0, "Pending", "Test"),
                    // Weight null
                    new LaundryOrder(userId, "John Doe", "08123456789", "Cuci Kering", null, 25000.0, "Pending", "Test"),
                    // Weight <= 0
                    new LaundryOrder(userId, "John Doe", "08123456789", "Cuci Kering", 0.0, 25000.0, "Pending", "Test"),
                    // Price null
                    new LaundryOrder(userId, "John Doe", "08123456789", "Cuci Kering", 5.0, null, "Pending", "Test"),
                    // Price < 0
                    new LaundryOrder(userId, "John Doe", "08123456789", "Cuci Kering", 5.0, -1000.0, "Pending", "Test"),
                    // Status null
                    new LaundryOrder(userId, "John Doe", "08123456789", "Cuci Kering", 5.0, 25000.0, null, "Test"),
                    // Status empty
                    new LaundryOrder(userId, "John Doe", "08123456789", "Cuci Kering", 5.0, 25000.0, "", "Test")
                );

                ResponseEntity<ApiResponse<Map<String, UUID>>> result;
                for (LaundryOrder itemOrder : invalidOrders) {
                    result = laundryOrderController.createLaundryOrder(itemOrder);
                    assert (result != null);
                    assert (result.getStatusCode().is4xxClientError());
                    assert (result.getBody().getStatus().equals("fail"));
                }
            }

            // Tidak terautentikasi untuk menambahkan order
            {
                laundryOrderController.authContext.setAuthUser(null);

                var result = laundryOrderController.createLaundryOrder(order);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil menambahkan order
            {
                laundryOrderController.authContext.setAuthUser(authUser);
                var result = laundryOrderController.createLaundryOrder(order);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getAllLaundryOrders
        {
            // Tidak terautentikasi untuk getAllLaundryOrders
            {
                laundryOrderController.authContext.setAuthUser(null);

                var result = laundryOrderController.getAllLaundryOrders(null);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Menguji getAllLaundryOrders dengan search null
            {
                laundryOrderController.authContext.setAuthUser(authUser);

                List<LaundryOrder> dummyResponse = List.of(order);
                when(laundryOrderService.getAllLaundryOrders(any(UUID.class), any(String.class)))
                    .thenReturn(dummyResponse);
                var result = laundryOrderController.getAllLaundryOrders(null);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getLaundryOrderById
        {
            // Tidak terautentikasi untuk getLaundryOrderById
            {
                laundryOrderController.authContext.setAuthUser(null);

                var result = laundryOrderController.getLaundryOrderById(orderId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            laundryOrderController.authContext.setAuthUser(authUser);

            // Menguji getLaundryOrderById dengan ID yang ada
            {
                when(laundryOrderService.getLaundryOrderById(any(UUID.class), any(UUID.class)))
                    .thenReturn(order);
                var result = laundryOrderController.getLaundryOrderById(orderId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().get("order").getId().equals(orderId));
            }

            // Menguji getLaundryOrderById dengan ID yang tidak ada
            {
                when(laundryOrderService.getLaundryOrderById(any(UUID.class), any(UUID.class)))
                    .thenReturn(null);
                var result = laundryOrderController.getLaundryOrderById(nonexistentOrderId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }
        }

        // Menguji method updateLaundryOrder
        {
            // Data tidak valid (similar validation as create)
            {
                List<LaundryOrder> invalidOrders = List.of(
                    new LaundryOrder(userId, null, "08123456789", "Cuci Kering", 5.0, 25000.0, "Pending", "Test"),
                    new LaundryOrder(userId, "", "08123456789", "Cuci Kering", 5.0, 25000.0, "Pending", "Test"),
                    new LaundryOrder(userId, "John", null, "Cuci Kering", 5.0, 25000.0, "Pending", "Test"),
                    new LaundryOrder(userId, "John", "", "Cuci Kering", 5.0, 25000.0, "Pending", "Test"),
                    new LaundryOrder(userId, "John", "08123", null, 5.0, 25000.0, "Pending", "Test"),
                    new LaundryOrder(userId, "John", "08123", "", 5.0, 25000.0, "Pending", "Test"),
                    new LaundryOrder(userId, "John", "08123", "Cuci Kering", null, 25000.0, "Pending", "Test"),
                    new LaundryOrder(userId, "John", "08123", "Cuci Kering", 0.0, 25000.0, "Pending", "Test"),
                    new LaundryOrder(userId, "John", "08123", "Cuci Kering", 5.0, null, "Pending", "Test"),
                    new LaundryOrder(userId, "John", "08123", "Cuci Kering", 5.0, -100.0, "Pending", "Test"),
                    new LaundryOrder(userId, "John", "08123", "Cuci Kering", 5.0, 25000.0, null, "Test"),
                    new LaundryOrder(userId, "John", "08123", "Cuci Kering", 5.0, 25000.0, "", "Test")
                );

                for (LaundryOrder itemOrder : invalidOrders) {
                    var result = laundryOrderController.updateLaundryOrder(orderId, itemOrder);
                    assert (result != null);
                    assert (result.getStatusCode().is4xxClientError());
                    assert (result.getBody().getStatus().equals("fail"));
                }
            }

            // Tidak terautentikasi untuk updateLaundryOrder
            {
                laundryOrderController.authContext.setAuthUser(null);

                var result = laundryOrderController.updateLaundryOrder(orderId, order);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            laundryOrderController.authContext.setAuthUser(authUser);

            // Memperbarui order dengan ID tidak ada
            {
                when(laundryOrderService.updateLaundryOrder(
                    any(UUID.class), any(UUID.class), any(String.class), 
                    any(String.class), any(String.class), any(Double.class),
                    any(Double.class), any(String.class), any(String.class)
                )).thenReturn(null);
                
                LaundryOrder updatedOrder = new LaundryOrder(
                    userId, "Jane Doe", "08198765432", "Cuci Kering",
                    3.0, 15000.0, "Selesai", "Updated"
                );
                updatedOrder.setId(nonexistentOrderId);

                var result = laundryOrderController.updateLaundryOrder(nonexistentOrderId, updatedOrder);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Memperbarui order dengan ID ada
            {
                LaundryOrder updatedOrder = new LaundryOrder(
                    userId, "Jane Doe", "08198765432", "Cuci Kering",
                    3.0, 15000.0, "Selesai", "Updated"
                );
                updatedOrder.setId(orderId);
                
                when(laundryOrderService.updateLaundryOrder(
                    any(UUID.class), any(UUID.class), any(String.class),
                    any(String.class), any(String.class), any(Double.class),
                    any(Double.class), any(String.class), any(String.class)
                )).thenReturn(updatedOrder);

                var result = laundryOrderController.updateLaundryOrder(orderId, updatedOrder);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method deleteLaundryOrder
        {
            // Tidak terautentikasi untuk deleteLaundryOrder
            {
                laundryOrderController.authContext.setAuthUser(null);

                var result = laundryOrderController.deleteLaundryOrder(orderId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            laundryOrderController.authContext.setAuthUser(authUser);

            // Menguji deleteLaundryOrder dengan ID yang tidak ada
            {
                when(laundryOrderService.deleteLaundryOrder(any(UUID.class), any(UUID.class)))
                    .thenReturn(false);
                var result = laundryOrderController.deleteLaundryOrder(nonexistentOrderId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Menguji deleteLaundryOrder dengan ID yang ada
            {
                when(laundryOrderService.deleteLaundryOrder(any(UUID.class), any(UUID.class)))
                    .thenReturn(true);
                var result = laundryOrderController.deleteLaundryOrder(orderId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getStatistics
        {
            // Tidak terautentikasi
            {
                laundryOrderController.authContext.setAuthUser(null);

                var result = laundryOrderController.getStatistics();
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil mendapatkan statistik
            {
                laundryOrderController.authContext.setAuthUser(authUser);

                Map<String, Object> mockStats = new HashMap<>();
                mockStats.put("totalOrders", 10);
                mockStats.put("totalRevenue", 100000.0);

                when(laundryOrderService.getStatistics(any(UUID.class))).thenReturn(mockStats);

                var result = laundryOrderController.getStatistics();
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().containsKey("totalOrders"));
            }
        }
    }
}