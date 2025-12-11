package org.delcom.app.dto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.web.multipart.MultipartFile;

class CoverLaundryFormTests {

    private CoverLaundryForm coverLaundryForm;
    private MultipartFile mockMultipartFile;

    @BeforeEach
    void setup() {
        coverLaundryForm = new CoverLaundryForm();
        mockMultipartFile = mock(MultipartFile.class);
    }

    @Test
    @DisplayName("Constructor default membuat objek kosong")
    void constructor_default_membuat_objek_kosong() {
        // Act
        CoverLaundryForm form = new CoverLaundryForm();

        // Assert
        assertNull(form.getId());
        assertNull(form.getCoverFile());
    }

    @Test
    @DisplayName("Setter dan Getter untuk ID bekerja dengan benar")
    void setter_dan_getter_untuk_id_bekerja_dengan_benar() {
        // Arrange
        UUID expectedId = UUID.randomUUID();

        // Act
        coverLaundryForm.setId(expectedId);
        UUID actualId = coverLaundryForm.getId();

        // Assert
        assertEquals(expectedId, actualId);
    }

    @Test
    @DisplayName("Setter dan Getter untuk coverFile bekerja dengan benar")
    void setter_dan_getter_untuk_coverFile_bekerja_dengan_benar() {
        // Act
        coverLaundryForm.setCoverFile(mockMultipartFile);
        MultipartFile actualFile = coverLaundryForm.getCoverFile();

        // Assert
        assertEquals(mockMultipartFile, actualFile);
    }

    @Test
    @DisplayName("isEmpty return true ketika coverFile null")
    void isEmpty_return_true_ketika_coverFile_null() {
        // Arrange
        coverLaundryForm.setCoverFile(null);

        // Act
        boolean result = coverLaundryForm.isEmpty();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isEmpty return true ketika coverFile empty")
    void isEmpty_return_true_ketika_coverFile_empty() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(true);
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverLaundryForm.isEmpty();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isEmpty return false ketika coverFile tidak empty")
    void isEmpty_return_false_ketika_coverFile_tidak_empty() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverLaundryForm.isEmpty();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("getOriginalFilename return null ketika coverFile null")
    void getOriginalFilename_return_null_ketika_coverFile_null() {
        // Arrange
        coverLaundryForm.setCoverFile(null);

        // Act
        String result = coverLaundryForm.getOriginalFilename();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getOriginalFilename return filename ketika coverFile ada")
    void getOriginalFilename_return_filename_ketika_coverFile_ada() {
        // Arrange
        String expectedFilename = "laundry-photo.jpg";
        when(mockMultipartFile.getOriginalFilename()).thenReturn(expectedFilename);
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        String result = coverLaundryForm.getOriginalFilename();

        // Assert
        assertEquals(expectedFilename, result);
    }

    @Test
    @DisplayName("isValidImage return false ketika coverFile null")
    void isValidImage_return_false_ketika_coverFile_null() {
        // Arrange
        coverLaundryForm.setCoverFile(null);

        // Act
        boolean result = coverLaundryForm.isValidImage();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isValidImage return false ketika coverFile empty")
    void isValidImage_return_false_ketika_coverFile_empty() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(true);
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverLaundryForm.isValidImage();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isValidImage return false ketika contentType null")
    void isValidImage_return_false_ketika_contentType_null() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn(null);
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverLaundryForm.isValidImage();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isValidImage return true untuk image/jpeg")
    void isValidImage_return_true_untuk_image_jpeg() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/jpeg");
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverLaundryForm.isValidImage();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isValidImage return true untuk image/png")
    void isValidImage_return_true_untuk_image_png() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/png");
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverLaundryForm.isValidImage();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isValidImage return true untuk image/gif")
    void isValidImage_return_true_untuk_image_gif() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/gif");
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverLaundryForm.isValidImage();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isValidImage return true untuk image/webp")
    void isValidImage_return_true_untuk_image_webp() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/webp");
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverLaundryForm.isValidImage();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isValidImage return false untuk content type non-image")
    void isValidImage_return_false_untuk_content_type_non_image() {
        // Arrange
        String[] invalidContentTypes = {
                "text/plain",
                "application/pdf",
                "application/octet-stream",
                "video/mp4",
                "audio/mpeg",
                "image/svg+xml", // SVG tidak didukung
                "image/bmp" // BMP tidak didukung
        };

        for (String contentType : invalidContentTypes) {
            when(mockMultipartFile.isEmpty()).thenReturn(false);
            when(mockMultipartFile.getContentType()).thenReturn(contentType);
            coverLaundryForm.setCoverFile(mockMultipartFile);

            // Act
            boolean result = coverLaundryForm.isValidImage();

            // Assert
            assertFalse(result, "Should return false for content type: " + contentType);
        }
    }

    @Test
    @DisplayName("isSizeValid return false ketika coverFile null")
    void isSizeValid_return_false_ketika_coverFile_null() {
        // Arrange
        coverLaundryForm.setCoverFile(null);
        long maxSize = 5 * 1024 * 1024; // 5MB

        // Act
        boolean result = coverLaundryForm.isSizeValid(maxSize);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isSizeValid return true ketika file size sama dengan maxSize")
    void isSizeValid_return_true_ketika_file_size_sama_dengan_maxSize() {
        // Arrange
        long maxSize = 5 * 1024 * 1024; // 5MB
        when(mockMultipartFile.getSize()).thenReturn(maxSize);
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverLaundryForm.isSizeValid(maxSize);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isSizeValid return true ketika file size kurang dari maxSize")
    void isSizeValid_return_true_ketika_file_size_kurang_dari_maxSize() {
        // Arrange
        long maxSize = 5 * 1024 * 1024; // 5MB
        long fileSize = 2 * 1024 * 1024; // 2MB
        when(mockMultipartFile.getSize()).thenReturn(fileSize);
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverLaundryForm.isSizeValid(maxSize);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isSizeValid return false ketika file size lebih dari maxSize")
    void isSizeValid_return_false_ketika_file_size_lebih_dari_maxSize() {
        // Arrange
        long maxSize = 5 * 1024 * 1024; // 5MB
        long fileSize = 6 * 1024 * 1024; // 6MB
        when(mockMultipartFile.getSize()).thenReturn(fileSize);
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverLaundryForm.isSizeValid(maxSize);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isSizeValid return true untuk file size 0 dengan maxSize 0")
    void isSizeValid_return_true_untuk_file_size_0_dengan_maxSize_0() {
        // Arrange
        when(mockMultipartFile.getSize()).thenReturn(0L);
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverLaundryForm.isSizeValid(0L);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Integration test - form valid untuk foto pakaian JPEG ukuran normal")
    void integration_test_form_valid_untuk_foto_pakaian_JPEG_ukuran_normal() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/jpeg");
        when(mockMultipartFile.getSize()).thenReturn(2 * 1024 * 1024L); // 2MB
        when(mockMultipartFile.getOriginalFilename()).thenReturn("pakaian-kotor.jpg");

        coverLaundryForm.setId(id);
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Assert semua kondisi
        assertFalse(coverLaundryForm.isEmpty());
        assertEquals("pakaian-kotor.jpg", coverLaundryForm.getOriginalFilename());
        assertTrue(coverLaundryForm.isValidImage());
        assertTrue(coverLaundryForm.isSizeValid(5 * 1024 * 1024)); // 5MB max
        assertEquals(id, coverLaundryForm.getId());
    }

    @Test
    @DisplayName("Integration test - form valid untuk foto PNG")
    void integration_test_form_valid_untuk_foto_PNG() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/png");
        when(mockMultipartFile.getSize()).thenReturn(3 * 1024 * 1024L); // 3MB
        when(mockMultipartFile.getOriginalFilename()).thenReturn("laundry-items.png");

        coverLaundryForm.setId(id);
        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Assert
        assertFalse(coverLaundryForm.isEmpty());
        assertEquals("laundry-items.png", coverLaundryForm.getOriginalFilename());
        assertTrue(coverLaundryForm.isValidImage());
        assertTrue(coverLaundryForm.isSizeValid(5 * 1024 * 1024)); // 5MB max
    }

    @Test
    @DisplayName("Integration test - form invalid untuk file terlalu besar")
    void integration_test_form_invalid_untuk_file_terlalu_besar() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/jpeg");
        when(mockMultipartFile.getSize()).thenReturn(10 * 1024 * 1024L); // 10MB
        when(mockMultipartFile.getOriginalFilename()).thenReturn("large-photo.jpg");

        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Assert
        assertFalse(coverLaundryForm.isEmpty());
        assertTrue(coverLaundryForm.isValidImage()); // Masih valid sebagai image
        assertFalse(coverLaundryForm.isSizeValid(5 * 1024 * 1024)); // Tapi size melebihi 5MB
    }

    @Test
    @DisplayName("Integration test - form invalid untuk file PDF")
    void integration_test_form_invalid_untuk_file_PDF() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("application/pdf");
        when(mockMultipartFile.getSize()).thenReturn(1 * 1024 * 1024L); // 1MB
        when(mockMultipartFile.getOriginalFilename()).thenReturn("document.pdf");

        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Assert
        assertFalse(coverLaundryForm.isEmpty());
        assertFalse(coverLaundryForm.isValidImage()); // Tidak valid sebagai image
        assertTrue(coverLaundryForm.isSizeValid(5 * 1024 * 1024)); // Size valid tapi format tidak
    }

    @Test
    @DisplayName("Edge case - contentType case insensitive")
    void edge_case_contentType_case_insensitive() {
        // Arrange
        String[] caseVariations = {
                "IMAGE/JPEG",
                "Image/Jpeg",
                "image/JPEG",
                "IMAGE/jpeg"
        };

        for (String contentType : caseVariations) {
            when(mockMultipartFile.isEmpty()).thenReturn(false);
            when(mockMultipartFile.getContentType()).thenReturn(contentType);
            coverLaundryForm.setCoverFile(mockMultipartFile);

            // Act
            boolean result = coverLaundryForm.isValidImage();

            // Assert
            assertFalse(result, "Should return false for case variation: " + contentType);
        }
    }

    @Test
    @DisplayName("Foto pakaian dengan berbagai nama file")
    void foto_pakaian_dengan_berbagai_nama_file() {
        // Arrange
        String[] filenames = {
                "pakaian-kotor.jpg",
                "baju_customer_1.png",
                "laundry-order-123.jpeg",
                "IMG_20241209_143000.jpg",
                "foto bukti.jpg"
        };

        for (String filename : filenames) {
            when(mockMultipartFile.getOriginalFilename()).thenReturn(filename);
            coverLaundryForm.setCoverFile(mockMultipartFile);

            // Assert
            assertEquals(filename, coverLaundryForm.getOriginalFilename());
        }
    }

    @Test
    @DisplayName("Validasi ukuran file 5MB sesuai requirement")
    void validasi_ukuran_file_5MB_sesuai_requirement() {
        // Arrange
        long maxSize = 5 * 1024 * 1024; // 5MB sesuai requirement
        long validSize = 4 * 1024 * 1024; // 4MB - valid
        long invalidSize = 6 * 1024 * 1024; // 6MB - invalid

        // Test valid size
        when(mockMultipartFile.getSize()).thenReturn(validSize);
        coverLaundryForm.setCoverFile(mockMultipartFile);
        assertTrue(coverLaundryForm.isSizeValid(maxSize));

        // Test invalid size
        when(mockMultipartFile.getSize()).thenReturn(invalidSize);
        coverLaundryForm.setCoverFile(mockMultipartFile);
        assertFalse(coverLaundryForm.isSizeValid(maxSize));

        // Test exact max size
        when(mockMultipartFile.getSize()).thenReturn(maxSize);
        coverLaundryForm.setCoverFile(mockMultipartFile);
        assertTrue(coverLaundryForm.isSizeValid(maxSize));
    }

    @Test
    @DisplayName("WebP format didukung untuk foto modern")
    void webp_format_didukung_untuk_foto_modern() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/webp");
        when(mockMultipartFile.getSize()).thenReturn(1 * 1024 * 1024L);
        when(mockMultipartFile.getOriginalFilename()).thenReturn("modern-photo.webp");

        coverLaundryForm.setCoverFile(mockMultipartFile);

        // Assert
        assertTrue(coverLaundryForm.isValidImage());
        assertEquals("modern-photo.webp", coverLaundryForm.getOriginalFilename());
    }

    @Test
    @DisplayName("Semua format image yang didukung")
    void semua_format_image_yang_didukung() {
        // Arrange
        String[] supportedFormats = {
                "image/jpeg",
                "image/png",
                "image/gif",
                "image/webp"
        };

        for (String format : supportedFormats) {
            when(mockMultipartFile.isEmpty()).thenReturn(false);
            when(mockMultipartFile.getContentType()).thenReturn(format);
            coverLaundryForm.setCoverFile(mockMultipartFile);

            // Assert
            assertTrue(coverLaundryForm.isValidImage(), "Format " + format + " should be supported");
        }
    }
}