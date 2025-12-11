package org.delcom.app.dto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LaundryOrderFormTests {

    private LaundryOrderForm laundryOrderForm;

    @BeforeEach
    void setUp() {
        laundryOrderForm = new LaundryOrderForm();
    }

    @Test
    @DisplayName("Default constructor membuat objek dengan nilai default")
    void defaultConstructor_CreatesObjectWithDefaultValues() {
        assertNull(laundryOrderForm.getId());
        assertNull(laundryOrderForm.getCustomerName());
        assertNull(laundryOrderForm.getPhoneNumber());
        assertNull(laundryOrderForm.getServiceType());
        assertNull(laundryOrderForm.getWeight());
        assertNull(laundryOrderForm.getPrice());
        assertNull(laundryOrderForm.getStatus());
        assertNull(laundryOrderForm.getNotes());
        assertNull(laundryOrderForm.getConfirmCustomerName());
    }

    @Test
    @DisplayName("Setter dan Getter untuk id bekerja dengan benar")
    void setterAndGetter_Id_WorksCorrectly() {
        UUID id = UUID.randomUUID();
        laundryOrderForm.setId(id);
        assertEquals(id, laundryOrderForm.getId());
    }

    @Test
    @DisplayName("Setter dan Getter untuk customerName bekerja dengan benar")
    void setterAndGetter_CustomerName_WorksCorrectly() {
        String customerName = "John Doe";
        laundryOrderForm.setCustomerName(customerName);
        assertEquals(customerName, laundryOrderForm.getCustomerName());
    }

    @Test
    @DisplayName("Setter dan Getter untuk phoneNumber bekerja dengan benar")
    void setterAndGetter_PhoneNumber_WorksCorrectly() {
        String phoneNumber = "08123456789";
        laundryOrderForm.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, laundryOrderForm.getPhoneNumber());
    }

    @Test
    @DisplayName("Setter dan Getter untuk serviceType bekerja dengan benar")
    void setterAndGetter_ServiceType_WorksCorrectly() {
        String serviceType = "Cuci Setrika";
        laundryOrderForm.setServiceType(serviceType);
        assertEquals(serviceType, laundryOrderForm.getServiceType());
    }

    @Test
    @DisplayName("Setter dan Getter untuk weight bekerja dengan benar")
    void setterAndGetter_Weight_WorksCorrectly() {
        Double weight = 5.0;
        laundryOrderForm.setWeight(weight);
        assertEquals(weight, laundryOrderForm.getWeight());
    }

    @Test
    @DisplayName("Setter dan Getter untuk price bekerja dengan benar")
    void setterAndGetter_Price_WorksCorrectly() {
        Double price = 35000.0;
        laundryOrderForm.setPrice(price);
        assertEquals(price, laundryOrderForm.getPrice());
    }

    @Test
    @DisplayName("Setter dan Getter untuk status bekerja dengan benar")
    void setterAndGetter_Status_WorksCorrectly() {
        String status = "Pending";
        laundryOrderForm.setStatus(status);
        assertEquals(status, laundryOrderForm.getStatus());
    }

    @Test
    @DisplayName("Setter dan Getter untuk notes bekerja dengan benar")
    void setterAndGetter_Notes_WorksCorrectly() {
        String notes = "Pisahkan warna putih dan gelap";
        laundryOrderForm.setNotes(notes);
        assertEquals(notes, laundryOrderForm.getNotes());
    }

    @Test
    @DisplayName("Setter dan Getter untuk confirmCustomerName bekerja dengan benar")
    void setterAndGetter_ConfirmCustomerName_WorksCorrectly() {
        String confirmName = "John Doe";
        laundryOrderForm.setConfirmCustomerName(confirmName);
        assertEquals(confirmName, laundryOrderForm.getConfirmCustomerName());
    }

    @Test
    @DisplayName("Semua field dapat diset dan diget dengan nilai berbagai tipe")
    void allFields_CanBeSetAndGet_WithVariousValues() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String customerName = "Jane Doe";
        String phoneNumber = "08198765432";
        String serviceType = "Cuci Kering";
        Double weight = 3.5;
        Double price = 17500.0;
        String status = "Proses";
        String notes = "Tanpa pewangi";
        String confirmName = "Jane Doe";

        // Act
        laundryOrderForm.setId(id);
        laundryOrderForm.setCustomerName(customerName);
        laundryOrderForm.setPhoneNumber(phoneNumber);
        laundryOrderForm.setServiceType(serviceType);
        laundryOrderForm.setWeight(weight);
        laundryOrderForm.setPrice(price);
        laundryOrderForm.setStatus(status);
        laundryOrderForm.setNotes(notes);
        laundryOrderForm.setConfirmCustomerName(confirmName);

        // Assert
        assertEquals(id, laundryOrderForm.getId());
        assertEquals(customerName, laundryOrderForm.getCustomerName());
        assertEquals(phoneNumber, laundryOrderForm.getPhoneNumber());
        assertEquals(serviceType, laundryOrderForm.getServiceType());
        assertEquals(weight, laundryOrderForm.getWeight());
        assertEquals(price, laundryOrderForm.getPrice());
        assertEquals(status, laundryOrderForm.getStatus());
        assertEquals(notes, laundryOrderForm.getNotes());
        assertEquals(confirmName, laundryOrderForm.getConfirmCustomerName());
    }

    @Test
    @DisplayName("Field dapat diset dengan null values")
    void fields_CanBeSet_WithNullValues() {
        // Act
        laundryOrderForm.setId(null);
        laundryOrderForm.setCustomerName(null);
        laundryOrderForm.setPhoneNumber(null);
        laundryOrderForm.setServiceType(null);
        laundryOrderForm.setWeight(null);
        laundryOrderForm.setPrice(null);
        laundryOrderForm.setStatus(null);
        laundryOrderForm.setNotes(null);
        laundryOrderForm.setConfirmCustomerName(null);

        // Assert
        assertNull(laundryOrderForm.getId());
        assertNull(laundryOrderForm.getCustomerName());
        assertNull(laundryOrderForm.getPhoneNumber());
        assertNull(laundryOrderForm.getServiceType());
        assertNull(laundryOrderForm.getWeight());
        assertNull(laundryOrderForm.getPrice());
        assertNull(laundryOrderForm.getStatus());
        assertNull(laundryOrderForm.getNotes());
        assertNull(laundryOrderForm.getConfirmCustomerName());
    }

    @Test
    @DisplayName("Field dapat diset dengan empty strings")
    void fields_CanBeSet_WithEmptyStrings() {
        // Act
        laundryOrderForm.setCustomerName("");
        laundryOrderForm.setPhoneNumber("");
        laundryOrderForm.setServiceType("");
        laundryOrderForm.setStatus("");
        laundryOrderForm.setNotes("");
        laundryOrderForm.setConfirmCustomerName("");

        // Assert
        assertEquals("", laundryOrderForm.getCustomerName());
        assertEquals("", laundryOrderForm.getPhoneNumber());
        assertEquals("", laundryOrderForm.getServiceType());
        assertEquals("", laundryOrderForm.getStatus());
        assertEquals("", laundryOrderForm.getNotes());
        assertEquals("", laundryOrderForm.getConfirmCustomerName());
    }

    @Test
    @DisplayName("Field dapat diset dengan blank strings")
    void fields_CanBeSet_WithBlankStrings() {
        // Act
        laundryOrderForm.setCustomerName("   ");
        laundryOrderForm.setPhoneNumber("   ");
        laundryOrderForm.setServiceType("   ");
        laundryOrderForm.setStatus("   ");
        laundryOrderForm.setNotes("   ");
        laundryOrderForm.setConfirmCustomerName("   ");

        // Assert
        assertEquals("   ", laundryOrderForm.getCustomerName());
        assertEquals("   ", laundryOrderForm.getPhoneNumber());
        assertEquals("   ", laundryOrderForm.getServiceType());
        assertEquals("   ", laundryOrderForm.getStatus());
        assertEquals("   ", laundryOrderForm.getNotes());
        assertEquals("   ", laundryOrderForm.getConfirmCustomerName());
    }

    @Test
    @DisplayName("Weight dapat diset dengan nilai desimal")
    void weight_CanBeSet_WithDecimalValues() {
        // Arrange
        Double[] weights = {0.5, 1.5, 2.75, 3.333, 10.99};

        for (Double weight : weights) {
            // Act
            laundryOrderForm.setWeight(weight);

            // Assert
            assertEquals(weight, laundryOrderForm.getWeight());
        }
    }

    @Test
    @DisplayName("Price dapat diset dengan berbagai nilai")
    void price_CanBeSet_WithVariousValues() {
        // Arrange
        Double[] prices = {0.0, 5000.0, 15000.0, 100000.0, 999999.99};

        for (Double price : prices) {
            // Act
            laundryOrderForm.setPrice(price);

            // Assert
            assertEquals(price, laundryOrderForm.getPrice());
        }
    }

    @Test
    @DisplayName("ServiceType dapat diset dengan semua jenis layanan")
    void serviceType_CanBeSet_WithAllServiceTypes() {
        // Arrange
        String[] serviceTypes = {"Cuci Kering", "Cuci Setrika", "Setrika Saja"};

        for (String serviceType : serviceTypes) {
            // Act
            laundryOrderForm.setServiceType(serviceType);

            // Assert
            assertEquals(serviceType, laundryOrderForm.getServiceType());
        }
    }

    @Test
    @DisplayName("Status dapat diset dengan semua status yang valid")
    void status_CanBeSet_WithAllValidStatuses() {
        // Arrange
        String[] statuses = {"Pending", "Proses", "Selesai", "Diambil"};

        for (String status : statuses) {
            // Act
            laundryOrderForm.setStatus(status);

            // Assert
            assertEquals(status, laundryOrderForm.getStatus());
        }
    }

    @Test
    @DisplayName("PhoneNumber dapat diset dengan berbagai format")
    void phoneNumber_CanBeSet_WithVariousFormats() {
        // Arrange
        String[] phoneNumbers = {
            "08123456789",
            "+6281234567890",
            "081234567890",
            "021-1234567",
            "(021) 1234-5678"
        };

        for (String phoneNumber : phoneNumbers) {
            // Act
            laundryOrderForm.setPhoneNumber(phoneNumber);

            // Assert
            assertEquals(phoneNumber, laundryOrderForm.getPhoneNumber());
        }
    }

    @Test
    @DisplayName("Notes dapat diset dengan teks panjang")
    void notes_CanBeSet_WithLongText() {
        // Arrange
        String longNotes = "Pisahkan warna putih dan gelap. " +
                          "Jangan gunakan pewangi. " +
                          "Setrika dengan suhu sedang. " +
                          "Lipat rapi setelah selesai. " +
                          "Gunakan detergen khusus untuk pakaian bayi.";

        // Act
        laundryOrderForm.setNotes(longNotes);

        // Assert
        assertEquals(longNotes, laundryOrderForm.getNotes());
    }

    @Test
    @DisplayName("Multiple operations pada object yang sama")
    void multipleOperations_OnSameObject() {
        // First set of values
        UUID id1 = UUID.randomUUID();
        laundryOrderForm.setId(id1);
        laundryOrderForm.setCustomerName("First Customer");
        laundryOrderForm.setWeight(5.0);
        laundryOrderForm.setStatus("Pending");

        assertEquals(id1, laundryOrderForm.getId());
        assertEquals("First Customer", laundryOrderForm.getCustomerName());
        assertEquals(5.0, laundryOrderForm.getWeight());
        assertEquals("Pending", laundryOrderForm.getStatus());

        // Second set of values
        UUID id2 = UUID.randomUUID();
        laundryOrderForm.setId(id2);
        laundryOrderForm.setCustomerName("Second Customer");
        laundryOrderForm.setWeight(3.5);
        laundryOrderForm.setStatus("Selesai");

        assertEquals(id2, laundryOrderForm.getId());
        assertEquals("Second Customer", laundryOrderForm.getCustomerName());
        assertEquals(3.5, laundryOrderForm.getWeight());
        assertEquals("Selesai", laundryOrderForm.getStatus());
    }

    @Test
    @DisplayName("ConfirmCustomerName digunakan untuk validasi delete")
    void confirmCustomerName_UsedForDeleteValidation() {
        // Arrange
        String customerName = "John Doe";
        String confirmName = "John Doe";

        // Act
        laundryOrderForm.setCustomerName(customerName);
        laundryOrderForm.setConfirmCustomerName(confirmName);

        // Assert
        assertEquals(customerName, laundryOrderForm.getCustomerName());
        assertEquals(confirmName, laundryOrderForm.getConfirmCustomerName());
        assertEquals(laundryOrderForm.getCustomerName(), laundryOrderForm.getConfirmCustomerName());
    }

    @Test
    @DisplayName("Integration test - Form lengkap untuk order baru")
    void integrationTest_CompleteFormForNewOrder() {
        // Arrange & Act
        laundryOrderForm.setCustomerName("Alice Smith");
        laundryOrderForm.setPhoneNumber("08123456789");
        laundryOrderForm.setServiceType("Cuci Setrika");
        laundryOrderForm.setWeight(7.5);
        laundryOrderForm.setPrice(52500.0);
        laundryOrderForm.setStatus("Pending");
        laundryOrderForm.setNotes("Pisahkan warna");

        // Assert
        assertEquals("Alice Smith", laundryOrderForm.getCustomerName());
        assertEquals("08123456789", laundryOrderForm.getPhoneNumber());
        assertEquals("Cuci Setrika", laundryOrderForm.getServiceType());
        assertEquals(7.5, laundryOrderForm.getWeight());
        assertEquals(52500.0, laundryOrderForm.getPrice());
        assertEquals("Pending", laundryOrderForm.getStatus());
        assertEquals("Pisahkan warna", laundryOrderForm.getNotes());
        assertNull(laundryOrderForm.getId()); // ID belum di-set untuk order baru
    }

    @Test
    @DisplayName("Integration test - Form untuk edit order")
    void integrationTest_FormForEditOrder() {
        // Arrange & Act
        UUID existingId = UUID.randomUUID();
        laundryOrderForm.setId(existingId);
        laundryOrderForm.setCustomerName("Bob Johnson");
        laundryOrderForm.setPhoneNumber("08198765432");
        laundryOrderForm.setServiceType("Cuci Kering");
        laundryOrderForm.setWeight(4.0);
        laundryOrderForm.setPrice(20000.0);
        laundryOrderForm.setStatus("Selesai");
        laundryOrderForm.setNotes("Sudah selesai");

        // Assert
        assertEquals(existingId, laundryOrderForm.getId());
        assertEquals("Bob Johnson", laundryOrderForm.getCustomerName());
        assertEquals("08198765432", laundryOrderForm.getPhoneNumber());
        assertEquals("Cuci Kering", laundryOrderForm.getServiceType());
        assertEquals(4.0, laundryOrderForm.getWeight());
        assertEquals(20000.0, laundryOrderForm.getPrice());
        assertEquals("Selesai", laundryOrderForm.getStatus());
        assertEquals("Sudah selesai", laundryOrderForm.getNotes());
    }

    @Test
    @DisplayName("Integration test - Form untuk delete dengan konfirmasi")
    void integrationTest_FormForDeleteWithConfirmation() {
        // Arrange & Act
        UUID existingId = UUID.randomUUID();
        String customerName = "Charlie Brown";
        
        laundryOrderForm.setId(existingId);
        laundryOrderForm.setCustomerName(customerName);
        laundryOrderForm.setConfirmCustomerName(customerName);

        // Assert
        assertEquals(existingId, laundryOrderForm.getId());
        assertEquals(customerName, laundryOrderForm.getCustomerName());
        assertEquals(customerName, laundryOrderForm.getConfirmCustomerName());
    }

    @Test
    @DisplayName("Weight zero adalah valid")
    void weight_ZeroIsValid() {
        // Act
        laundryOrderForm.setWeight(0.0);

        // Assert
        assertEquals(0.0, laundryOrderForm.getWeight());
    }

    @Test
    @DisplayName("Price zero adalah valid")
    void price_ZeroIsValid() {
        // Act
        laundryOrderForm.setPrice(0.0);

        // Assert
        assertEquals(0.0, laundryOrderForm.getPrice());
    }
}