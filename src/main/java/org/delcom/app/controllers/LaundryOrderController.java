package org.delcom.app.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.LaundryOrder;
import org.delcom.app.entities.User;
import org.delcom.app.services.LaundryOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/laundry")
public class LaundryOrderController {
    private final LaundryOrderService laundryOrderService;

    @Autowired
    protected AuthContext authContext;

    public LaundryOrderController(LaundryOrderService laundryOrderService) {
        this.laundryOrderService = laundryOrderService;
    }

    // Menambahkan order laundry baru
    // -------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, UUID>>> createLaundryOrder(@RequestBody LaundryOrder reqOrder) {

        // Validasi input
        if (reqOrder.getCustomerName() == null || reqOrder.getCustomerName().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("fail", "Nama pelanggan tidak valid", null));
        } else if (reqOrder.getPhoneNumber() == null || reqOrder.getPhoneNumber().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("fail", "Nomor telepon tidak valid", null));
        } else if (reqOrder.getServiceType() == null || reqOrder.getServiceType().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("fail", "Jenis layanan tidak valid", null));
        } else if (reqOrder.getWeight() == null || reqOrder.getWeight() <= 0) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("fail", "Berat cucian tidak valid", null));
        } else if (reqOrder.getPrice() == null || reqOrder.getPrice() < 0) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("fail", "Harga tidak valid", null));
        } else if (reqOrder.getStatus() == null || reqOrder.getStatus().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("fail", "Status tidak valid", null));
        }

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403)
                .body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        LaundryOrder newOrder = laundryOrderService.createLaundryOrder(
            authUser.getId(), 
            reqOrder.getCustomerName(),
            reqOrder.getPhoneNumber(),
            reqOrder.getServiceType(),
            reqOrder.getWeight(),
            reqOrder.getPrice(),
            reqOrder.getStatus(),
            reqOrder.getNotes()
        );
        
        return ResponseEntity.ok(new ApiResponse<>(
            "success",
            "Order laundry berhasil dibuat",
            Map.of("id", newOrder.getId())
        ));
    }

    // Mendapatkan semua order dengan opsi pencarian
    // -------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<LaundryOrder>>>> getAllLaundryOrders(
            @RequestParam(required = false) String search) {
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403)
                .body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<LaundryOrder> orders = laundryOrderService.getAllLaundryOrders(authUser.getId(), search);
        return ResponseEntity.ok(new ApiResponse<>(
            "success",
            "Daftar order laundry berhasil diambil",
            Map.of("orders", orders)
        ));
    }

    // Mendapatkan order berdasarkan ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, LaundryOrder>>> getLaundryOrderById(@PathVariable UUID id) {
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403)
                .body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        LaundryOrder order = laundryOrderService.getLaundryOrderById(authUser.getId(), id);
        if (order == null) {
            return ResponseEntity.status(404)
                .body(new ApiResponse<>("fail", "Order laundry tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
            "success",
            "Data order laundry berhasil diambil",
            Map.of("order", order)
        ));
    }

    // Memperbarui order berdasarkan ID
    // -------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LaundryOrder>> updateLaundryOrder(
            @PathVariable UUID id, 
            @RequestBody LaundryOrder reqOrder) {

        // Validasi input
        if (reqOrder.getCustomerName() == null || reqOrder.getCustomerName().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("fail", "Nama pelanggan tidak valid", null));
        } else if (reqOrder.getPhoneNumber() == null || reqOrder.getPhoneNumber().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("fail", "Nomor telepon tidak valid", null));
        } else if (reqOrder.getServiceType() == null || reqOrder.getServiceType().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("fail", "Jenis layanan tidak valid", null));
        } else if (reqOrder.getWeight() == null || reqOrder.getWeight() <= 0) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("fail", "Berat cucian tidak valid", null));
        } else if (reqOrder.getPrice() == null || reqOrder.getPrice() < 0) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("fail", "Harga tidak valid", null));
        } else if (reqOrder.getStatus() == null || reqOrder.getStatus().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("fail", "Status tidak valid", null));
        }

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403)
                .body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        LaundryOrder updatedOrder = laundryOrderService.updateLaundryOrder(
            authUser.getId(), 
            id,
            reqOrder.getCustomerName(),
            reqOrder.getPhoneNumber(),
            reqOrder.getServiceType(),
            reqOrder.getWeight(),
            reqOrder.getPrice(),
            reqOrder.getStatus(),
            reqOrder.getNotes()
        );
        
        if (updatedOrder == null) {
            return ResponseEntity.status(404)
                .body(new ApiResponse<>("fail", "Order laundry tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
            "success", 
            "Data order laundry berhasil diperbarui", 
            null
        ));
    }

    // Menghapus order berdasarkan ID
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteLaundryOrder(@PathVariable UUID id) {
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403)
                .body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        boolean status = laundryOrderService.deleteLaundryOrder(authUser.getId(), id);
        if (!status) {
            return ResponseEntity.status(404)
                .body(new ApiResponse<>("fail", "Order laundry tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
            "success",
            "Order laundry berhasil dihapus",
            null
        ));
    }

    // Mendapatkan statistik untuk chart
    // -------------------------------
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403)
                .body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        Map<String, Object> stats = laundryOrderService.getStatistics(authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(
            "success",
            "Statistik berhasil diambil",
            stats
        ));
    }
}