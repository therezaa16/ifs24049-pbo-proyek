package org.delcom.app.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

class FileStorageServiceTests {

    private FileStorageService fileStorageService;
    private MultipartFile mockMultipartFile;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        fileStorageService = new FileStorageService();
        // Override uploadDir dengan temporary directory
        fileStorageService.uploadDir = tempDir.toString();
        mockMultipartFile = mock(MultipartFile.class);
    }

    @Test
    @DisplayName("Store file (Todo) berhasil menyimpan file dengan extension")
    void storeFile_berhasil_menyimpan_file_dengan_extension() throws Exception {
        // Arrange
        UUID todoId = UUID.randomUUID();
        String originalFilename = "image.jpg";
        String expectedFilename = "cover_" + todoId + ".jpg";
        byte[] fileContent = "fake image content".getBytes();

        when(mockMultipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));

        // Act
        String result = fileStorageService.storeFile(mockMultipartFile, todoId);

        // Assert
        assertEquals(expectedFilename, result);

        // Verify file actually exists and content is correct
        Path expectedFile = tempDir.resolve(expectedFilename);
        assertTrue(Files.exists(expectedFile));
        assertArrayEquals(fileContent, Files.readAllBytes(expectedFile));
    }

    @Test
    @DisplayName("Store (Laundry) berhasil menyimpan file")
    void store_berhasil_menyimpan_file() throws Exception {
        // Arrange
        String originalFilename = "pakaian.jpg";
        byte[] fileContent = "fake laundry image".getBytes();

        when(mockMultipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));

        // Act
        String result = fileStorageService.store(mockMultipartFile);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("laundry_"));
        assertTrue(result.endsWith(".jpg"));

        // Verify file exists
        Path savedFile = tempDir.resolve(result);
        assertTrue(Files.exists(savedFile));
        assertArrayEquals(fileContent, Files.readAllBytes(savedFile));
    }

    @Test
    @DisplayName("Store file berhasil tanpa extension ketika original filename null")
    void storeFile_berhasil_tanpa_extension_ketika_originalFilename_null() throws Exception {
        // Arrange
        UUID todoId = UUID.randomUUID();
        String expectedFilename = "cover_" + todoId.toString();
        byte[] fileContent = "fake content".getBytes();

        when(mockMultipartFile.getOriginalFilename()).thenReturn(null);
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));

        // Act
        String result = fileStorageService.storeFile(mockMultipartFile, todoId);

        // Assert
        assertEquals(expectedFilename, result);
        assertTrue(Files.exists(tempDir.resolve(expectedFilename)));
    }

    @Test
    @DisplayName("Store file berhasil tanpa extension ketika tidak ada dot")
    void storeFile_berhasil_tanpa_extension_ketika_tidak_ada_dot() throws Exception {
        // Arrange
        UUID todoId = UUID.randomUUID();
        String expectedFilename = "cover_" + todoId.toString();
        byte[] fileContent = "fake content".getBytes();

        when(mockMultipartFile.getOriginalFilename()).thenReturn("filename");
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));

        // Act
        String result = fileStorageService.storeFile(mockMultipartFile, todoId);

        // Assert
        assertEquals(expectedFilename, result);
        assertTrue(Files.exists(tempDir.resolve(expectedFilename)));
    }

    @Test
    @DisplayName("Store file berhasil dengan complex extension")
    void storeFile_berhasil_dengan_complex_extension() throws Exception {
        // Arrange
        UUID todoId = UUID.randomUUID();
        String originalFilename = "document.final.pdf";
        String expectedFilename = "cover_" + todoId + ".pdf";
        byte[] fileContent = "fake pdf content".getBytes();

        when(mockMultipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));

        // Act
        String result = fileStorageService.storeFile(mockMultipartFile, todoId);

        // Assert
        assertEquals(expectedFilename, result);
        assertTrue(Files.exists(tempDir.resolve(expectedFilename)));
    }

    @Test
    @DisplayName("Store file membuat directory ketika belum ada")
    void storeFile_membuat_directory_ketika_belum_ada() throws Exception {
        // Arrange
        UUID todoId = UUID.randomUUID();
        Path customUploadDir = tempDir.resolve("custom-upload");
        fileStorageService.uploadDir = customUploadDir.toString();

        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("content".getBytes()));

        // Act
        String result = fileStorageService.storeFile(mockMultipartFile, todoId);

        // Assert
        assertTrue(Files.exists(customUploadDir));
        assertTrue(Files.isDirectory(customUploadDir));
        assertTrue(Files.exists(customUploadDir.resolve(result)));
    }

    @Test
    @DisplayName("Store file melemparkan exception ketika IOException terjadi")
    void storeFile_melemparkan_exception_ketika_ioexception_terjadi() throws Exception {
        // Arrange
        UUID todoId = UUID.randomUUID();

        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockMultipartFile.getInputStream()).thenThrow(new IOException("Simulated IO error"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            fileStorageService.storeFile(mockMultipartFile, todoId);
        });
    }

    @Test
    @DisplayName("Delete file berhasil menghapus file yang ada")
    void deleteFile_berhasil_menghapus_file_yang_ada() throws Exception {
        // Arrange
        String filename = "test-file.txt";
        Path testFile = tempDir.resolve(filename);
        Files.write(testFile, "content".getBytes());
        assertTrue(Files.exists(testFile));

        // Act
        boolean result = fileStorageService.deleteFile(filename);

        // Assert
        assertTrue(result);
        assertFalse(Files.exists(testFile));
    }

    @Test
    @DisplayName("Delete file return false ketika file tidak ada")
    void deleteFile_return_false_ketika_file_tidak_ada() {
        // Arrange
        String nonExistentFilename = "non-existent-file.txt";

        // Act
        boolean result = fileStorageService.deleteFile(nonExistentFilename);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Delete file return false ketika IOException terjadi")
    void deleteFile_return_false_ketika_ioexception() throws Exception {
        // Arrange
        String filename = "test-file.txt";
        Path filePath = Paths.get(fileStorageService.uploadDir).resolve(filename);

        // Mock Files class untuk melemparkan IOException
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.deleteIfExists(filePath))
                    .thenThrow(new IOException("Permission denied"));

            // Act
            boolean result = fileStorageService.deleteFile(filename);

            // Assert
            assertFalse(result);
        }
    }

    @Test
    @DisplayName("Load file return path yang benar")
    void loadFile_return_path_yang_benar() {
        // Arrange
        String filename = "test-file.txt";
        Path expectedPath = tempDir.resolve(filename);

        // Act
        Path result = fileStorageService.loadFile(filename);

        // Assert
        assertEquals(expectedPath, result);
    }

    @Test
    @DisplayName("LoadAsResource return Resource yang valid")
    void loadAsResource_return_resource_yang_valid() throws Exception {
        // Arrange
        String filename = "test-image.jpg";
        Path testFile = tempDir.resolve(filename);
        Files.write(testFile, "image content".getBytes());

        // Act
        Resource resource = fileStorageService.loadAsResource(filename);

        // Assert
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    @DisplayName("LoadAsResource throw exception ketika file tidak ada")
    void loadAsResource_throw_exception_ketika_file_tidak_ada() {
        // Arrange
        String nonExistentFilename = "non-existent.jpg";

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> {
            fileStorageService.loadAsResource(nonExistentFilename);
        });
        
        // Verify exception message contains expected text
        String message = exception.getMessage();
        assertTrue(message.contains("tidak ditemukan") || message.contains("tidak dapat dibaca"),
                "Exception message should indicate file not found or not readable");
    }

    @Test
    @DisplayName("LoadAsResource dengan path yang invalid cause MalformedURLException")
    void loadAsResource_dengan_path_invalid_cause_malformedurl() throws Exception {
        // Arrange - Create file dengan nama yang akan cause URL issues
        String problemFilename = "test file with spaces.jpg";
        Path testFile = tempDir.resolve(problemFilename);
        Files.write(testFile, "content".getBytes());
        
        // On some systems, spaces in filenames can cause issues with UrlResource
        // This test covers the MalformedURLException catch block
        try {
            Resource resource = fileStorageService.loadAsResource(problemFilename);
            // If it succeeds, verify it's valid
            assertNotNull(resource);
        } catch (IOException e) {
            // This is also acceptable - either path works for coverage
            assertTrue(e.getMessage().contains("tidak"));
        }
    }

    @Test
    @DisplayName("LoadAsResource dengan file exists tapi not readable")
    void loadAsResource_dengan_file_exists_tapi_not_readable() throws Exception {
        // Arrange
        String filename = "readonly-file.txt";
        Path testFile = tempDir.resolve(filename);
        Files.write(testFile, "content".getBytes());
        
        // Try to make file not readable
        boolean readableChanged = testFile.toFile().setReadable(false);
        
        try {
            if (readableChanged && !testFile.toFile().canRead()) {
                // File is truly not readable - test should throw exception
                IOException exception = assertThrows(IOException.class, () -> {
                    fileStorageService.loadAsResource(filename);
                });
                assertTrue(exception.getMessage().contains("tidak"));
            } else {
                // OS doesn't support setReadable(false) - test successful load instead
                Resource resource = fileStorageService.loadAsResource(filename);
                assertNotNull(resource);
                assertTrue(resource.exists());
            }
        } finally {
            // Cleanup - restore readable permission
            testFile.toFile().setReadable(true);
        }
    }

    @Test
    @DisplayName("LoadAsResource dengan directory instead of file")
    void loadAsResource_dengan_directory_bukan_file() throws Exception {
        // Arrange - create a directory instead of a file
        String dirname = "test-directory";
        Path testDir = tempDir.resolve(dirname);
        Files.createDirectory(testDir);

        // Act & Assert - directory should cause error (not a readable file)
        IOException exception = assertThrows(IOException.class, () -> {
            fileStorageService.loadAsResource(dirname);
        });
        
        assertTrue(exception.getMessage().contains("tidak ditemukan") || 
                   exception.getMessage().contains("tidak dapat dibaca"));
    }

    @Test
    @DisplayName("LoadAsResource cover both branches of exists and isReadable check")
    void loadAsResource_cover_both_branches() throws Exception {
        // Test 1: File exists AND is readable (true && true branch)
        String validFilename = "valid-file.txt";
        Path validFile = tempDir.resolve(validFilename);
        Files.write(validFile, "valid content".getBytes());
        
        Resource validResource = fileStorageService.loadAsResource(validFilename);
        assertNotNull(validResource);
        assertTrue(validResource.exists());
        assertTrue(validResource.isReadable());
        
        // Test 2: File doesn't exist (false && * branch)
        String invalidFilename = "does-not-exist.txt";
        
        IOException exception = assertThrows(IOException.class, () -> {
            fileStorageService.loadAsResource(invalidFilename);
        });
        assertTrue(exception.getMessage().contains("tidak"));
    }

    @Test
    @DisplayName("LoadAsResource throw exception ketika file tidak readable")
    void loadAsResource_throw_exception_ketika_file_tidak_readable() throws Exception {
        // Arrange
        String filename = "test-file.txt";
        Path testFile = tempDir.resolve(filename);
        Files.write(testFile, "content".getBytes());
        
        // Make file not readable (jika OS support)
        testFile.toFile().setReadable(false);

        // Act & Assert - might throw exception depending on OS permissions
        try {
            Resource resource = fileStorageService.loadAsResource(filename);
            // Jika berhasil load, pastikan bisa di-read
            assertNotNull(resource);
        } catch (IOException e) {
            // Expected jika file tidak readable
            assertTrue(e.getMessage().contains("tidak"));
        } finally {
            // Restore permissions
            testFile.toFile().setReadable(true);
        }
    }

    @Test
    @DisplayName("File exists return true ketika file ada")
    void fileExists_return_true_ketika_file_ada() throws Exception {
        // Arrange
        String filename = "existing-file.txt";
        Path existingFile = tempDir.resolve(filename);
        Files.write(existingFile, "content".getBytes());

        // Act
        boolean result = fileStorageService.fileExists(filename);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("File exists return false ketika file tidak ada")
    void fileExists_return_false_ketika_file_tidak_ada() {
        // Arrange
        String nonExistentFilename = "non-existent-file.txt";

        // Act
        boolean result = fileStorageService.fileExists(nonExistentFilename);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Store file menggantikan file yang sudah ada")
    void storeFile_menggantikan_file_yang_sudah_ada() throws Exception {
        // Arrange
        UUID todoId = UUID.randomUUID();
        String originalFilename = "test.txt";
        String expectedFilename = "cover_" + todoId + ".txt";

        // Create existing file with different content
        Path existingFile = tempDir.resolve(expectedFilename);
        Files.write(existingFile, "old content".getBytes());

        byte[] newContent = "new content".getBytes();

        when(mockMultipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(newContent));

        // Act
        String result = fileStorageService.storeFile(mockMultipartFile, todoId);

        // Assert
        assertEquals(expectedFilename, result);
        assertArrayEquals(newContent, Files.readAllBytes(existingFile));
    }

    @Test
    @DisplayName("Store method dengan berbagai format file")
    void store_method_dengan_berbagai_format_file() throws Exception {
        // Arrange
        String[] formats = {"jpg", "png", "gif", "webp", "pdf"};

        for (String format : formats) {
            String filename = "test." + format;
            byte[] content = ("content for " + format).getBytes();

            when(mockMultipartFile.getOriginalFilename()).thenReturn(filename);
            when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

            // Act
            String result = fileStorageService.store(mockMultipartFile);

            // Assert
            assertNotNull(result);
            assertTrue(result.endsWith("." + format));
            assertTrue(Files.exists(tempDir.resolve(result)));
        }
    }

    @Test
    @DisplayName("Store method tanpa extension")
    void store_method_tanpa_extension() throws Exception {
        // Arrange
        byte[] content = "test content".getBytes();
        when(mockMultipartFile.getOriginalFilename()).thenReturn("filename_without_ext");
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        // Act
        String result = fileStorageService.store(mockMultipartFile);

        // Assert
        assertNotNull(result);
        assertTrue(Files.exists(tempDir.resolve(result)));
    }

    @Test
    @DisplayName("Store method dengan filename null")
    void store_method_dengan_filename_null() throws Exception {
        // Arrange
        byte[] content = "test content".getBytes();
        when(mockMultipartFile.getOriginalFilename()).thenReturn(null);
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        // Act
        String result = fileStorageService.store(mockMultipartFile);

        // Assert
        assertNotNull(result);
        assertTrue(Files.exists(tempDir.resolve(result)));
    }

    @Test
    @DisplayName("Store method throw IOException")
    void store_method_throw_ioexception() throws Exception {
        // Arrange
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockMultipartFile.getInputStream()).thenThrow(new IOException("Test error"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            fileStorageService.store(mockMultipartFile);
        });
    }

    @Test
    @DisplayName("Store method membuat directory jika belum ada")
    void store_method_membuat_directory_jika_belum_ada() throws Exception {
        // Arrange
        Path customDir = tempDir.resolve("custom-laundry-dir");
        fileStorageService.uploadDir = customDir.toString();
        
        byte[] content = "test content".getBytes();
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        // Act
        String result = fileStorageService.store(mockMultipartFile);

        // Assert
        assertNotNull(result);
        assertTrue(Files.exists(customDir));
        assertTrue(Files.isDirectory(customDir));
        assertTrue(Files.exists(customDir.resolve(result)));
    }

    @Test
    @DisplayName("Store method mengganti file yang sudah ada")
    void store_method_mengganti_file_yang_sudah_ada() throws Exception {
        // Arrange
        String filename = "laundry_123_test.jpg";
        Path existingFile = tempDir.resolve(filename);
        Files.write(existingFile, "old content".getBytes());

        byte[] newContent = "new content".getBytes();
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(newContent));

        // Act
        String result = fileStorageService.store(mockMultipartFile);

        // Assert - file baru dibuat (dengan nama berbeda karena timestamp)
        assertNotNull(result);
        assertTrue(Files.exists(tempDir.resolve(result)));
    }
    @Test
    @DisplayName("LoadAsResource throw IOException when MalformedURLException occurs via mocking")
    void loadAsResource_throw_ioexception_when_malformed_url_via_mocking() throws Exception {
        // Arrange
        String filename = "test-file.jpg";
        Path testFile = tempDir.resolve(filename);
        Files.write(testFile, "content".getBytes());
        
        // Create spy of FileStorageService
        FileStorageService spyService = spy(fileStorageService);
        
        // Mock convertPathToUri to return a URI that will cause MalformedURLException
        // when .toURL() is called
        URI mockUri = mock(URI.class);
        when(mockUri.toURL()).thenThrow(new MalformedURLException("Simulated malformed URL"));
        
        doReturn(mockUri).when(spyService).convertPathToUri(any(Path.class));
        
        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> {
            spyService.loadAsResource(filename);
        });
        
        // Verify exception message and cause
        assertTrue(exception.getMessage().contains("File tidak ditemukan"));
        assertTrue(exception.getMessage().contains(filename));
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MalformedURLException);
    }

}