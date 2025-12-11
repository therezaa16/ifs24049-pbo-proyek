package org.delcom.app.entities;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LaundryOrderTests {
    @Test
    @DisplayName("Pengujian untuk entity LaundryOrder")
    void testLaundryOrder() throws Exception {
        // Buat random UUID
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        // Test constructor dengan semua parameter
        {
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

            assert (order != null);
            assert (order.getUserId().equals(userId));
            assert (order.getCustomerName().equals("John Doe"));
            assert (order.getPhoneNumber().equals("08123456789"));
            assert (order.getServiceType().equals("Cuci Setrika"));
            assert (order.getWeight().equals(5.0));
            assert (order.getPrice().equals(35000.0));
            assert (order.getStatus().equals("Pending"));
            assert (order.getNotes().equals("Pisahkan warna putih"));
        }

        // Test constructor tanpa parameter
        {
            LaundryOrder order = new LaundryOrder();
            assert (order != null);
        }

        // Test semua setter dan getter
        {
            LaundryOrder order = new LaundryOrder();

            // Set ID
            order.setId(orderId);
            assert (order.getId().equals(orderId));

            // Set User ID
            order.setUserId(userId);
            assert (order.getUserId().equals(userId));

            // Set Customer Name
            order.setCustomerName("Jane Doe");
            assert (order.getCustomerName().equals("Jane Doe"));

            // Set Phone Number
            order.setPhoneNumber("08198765432");
            assert (order.getPhoneNumber().equals("08198765432"));

            // Set Service Type
            order.setServiceType("Cuci Kering");
            assert (order.getServiceType().equals("Cuci Kering"));

            // Set Weight
            order.setWeight(3.5);
            assert (order.getWeight().equals(3.5));

            // Set Price
            order.setPrice(17500.0);
            assert (order.getPrice().equals(17500.0));

            // Set Status
            order.setStatus("Proses");
            assert (order.getStatus().equals("Proses"));

            // Set Cover
            order.setCover("cover123.png");
            assert (order.getCover().equals("cover123.png"));

            // Set Notes
            order.setNotes("Tanpa pewangi");
            assert (order.getNotes().equals("Tanpa pewangi"));
        }

        // Test semua service types
        {
            String[] serviceTypes = {"Cuci Kering", "Cuci Setrika", "Setrika Saja"};
            for (String serviceType : serviceTypes) {
                LaundryOrder order = new LaundryOrder(
                    userId,
                    "Test Customer",
                    "08123456789",
                    serviceType,
                    5.0,
                    25000.0,
                    "Pending",
                    "Test"
                );
                assert (order.getServiceType().equals(serviceType));
            }
        }

        // Test semua statuses
        {
            String[] statuses = {"Pending", "Proses", "Selesai", "Diambil"};
            for (String status : statuses) {
                LaundryOrder order = new LaundryOrder(
                    userId,
                    "Test Customer",
                    "08123456789",
                    "Cuci Kering",
                    5.0,
                    25000.0,
                    status,
                    "Test"
                );
                assert (order.getStatus().equals(status));
            }
        }

        // Test dengan notes null
        {
            LaundryOrder order = new LaundryOrder(
                userId,
                "Test Customer",
                "08123456789",
                "Cuci Kering",
                5.0,
                25000.0,
                "Pending",
                null
            );
            assert (order.getNotes() == null);
        }

        // Test dengan cover null
        {
            LaundryOrder order = new LaundryOrder();
            assert (order.getCover() == null);
        }

        // Test weight dengan nilai desimal
        {
            LaundryOrder order = new LaundryOrder(
                userId,
                "Test Customer",
                "08123456789",
                "Cuci Setrika",
                2.5,
                17500.0,
                "Pending",
                "Test"
            );
            assert (order.getWeight().equals(2.5));
        }

        // Test price dengan nilai besar
        {
            LaundryOrder order = new LaundryOrder(
                userId,
                "Test Customer",
                "08123456789",
                "Cuci Setrika",
                100.0,
                700000.0,
                "Pending",
                "Bulk order"
            );
            assert (order.getPrice().equals(700000.0));
        }

        // Test phone number dengan format berbeda
        {
            String[] phoneFormats = {
                "08123456789",
                "+6281234567890",
                "081234567890",
                "021-1234567"
            };
            
            for (String phone : phoneFormats) {
                LaundryOrder order = new LaundryOrder(
                    userId,
                    "Test Customer",
                    phone,
                    "Cuci Kering",
                    5.0,
                    25000.0,
                    "Pending",
                    "Test"
                );
                assert (order.getPhoneNumber().equals(phone));
            }
        }

        // Test dengan notes panjang
        {
            String longNotes = "Pisahkan warna putih dan gelap. " +
                             "Jangan gunakan pewangi. " +
                             "Setrika dengan suhu sedang. " +
                             "Lipat rapi setelah selesai.";
            
            LaundryOrder order = new LaundryOrder(
                userId,
                "Test Customer",
                "08123456789",
                "Cuci Setrika",
                10.0,
                70000.0,
                "Pending",
                longNotes
            );
            assert (order.getNotes().equals(longNotes));
        }

        // Test createdAt dan updatedAt dengan reflection untuk trigger @PrePersist
        {
            LaundryOrder order = new LaundryOrder(
                userId,
                "Test Customer",
                "08123456789",
                "Cuci Kering",
                5.0,
                25000.0,
                "Pending",
                "Test"
            );
            
            // Trigger @PrePersist manually dengan reflection
            try {
                var onCreateMethod = LaundryOrder.class.getDeclaredMethod("onCreate");
                onCreateMethod.setAccessible(true);
                onCreateMethod.invoke(order);
                
                // Setelah onCreate dipanggil, createdAt dan updatedAt harus terisi
                assert (order.getCreatedAt() != null);
                assert (order.getUpdatedAt() != null);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke onCreate", e);
            }
        }

        // Test onUpdate method dengan reflection
        {
            LaundryOrder order = new LaundryOrder(
                userId,
                "Test Customer",
                "08123456789",
                "Cuci Kering",
                5.0,
                25000.0,
                "Pending",
                "Test"
            );
            
            // Trigger @PrePersist dulu
            try {
                var onCreateMethod = LaundryOrder.class.getDeclaredMethod("onCreate");
                onCreateMethod.setAccessible(true);
                onCreateMethod.invoke(order);
                
                var firstUpdatedAt = order.getUpdatedAt();
                
                // Sleep sebentar agar timestamp berbeda
                Thread.sleep(10);
                
                // Trigger @PreUpdate
                var onUpdateMethod = LaundryOrder.class.getDeclaredMethod("onUpdate");
                onUpdateMethod.setAccessible(true);
                onUpdateMethod.invoke(order);
                
                // updatedAt harus berubah
                assert (order.getUpdatedAt() != null);
                assert (order.getUpdatedAt().isAfter(firstUpdatedAt) || 
                        order.getUpdatedAt().isEqual(firstUpdatedAt));
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke lifecycle methods", e);
            }
        }
    }
}