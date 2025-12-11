package org.delcom.app.views;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.dto.CoverLaundryForm;
import org.delcom.app.dto.LaundryOrderForm;
import org.delcom.app.entities.LaundryOrder;
import org.delcom.app.entities.User;
import org.delcom.app.services.FileStorageService;
import org.delcom.app.services.LaundryOrderService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/laundry")
public class LaundryViewController {

    @Autowired
    private LaundryOrderService laundryOrderService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private AuthContext authContext;

    // Halaman utama - Daftar semua order
    @GetMapping
    public String index(
            @RequestParam(required = false) String search,
            Model model) {

        if (!authContext.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        try {
            User authUser = authContext.getAuthUser();
            List<LaundryOrder> orders = laundryOrderService.getAllLaundryOrders(authUser.getId(), search);

            // Get statistics for charts
            Map<String, Object> stats = laundryOrderService.getStatistics(authUser.getId());

            model.addAttribute("auth", authUser);
            model.addAttribute("laundryOrders", orders);
            model.addAttribute("laundryOrderForm", new LaundryOrderForm());
            model.addAttribute("coverLaundryForm", new CoverLaundryForm());
            model.addAttribute("search", search != null ? search : "");

            // Add statistics to model with null checks
            model.addAttribute("totalOrders", stats.getOrDefault("totalOrders", 0));
            model.addAttribute("pendingOrders", stats.getOrDefault("pendingOrders", 0));
            model.addAttribute("ongoingOrders", stats.getOrDefault("ongoingOrders", 0));
            model.addAttribute("totalRevenue", stats.getOrDefault("totalRevenue", 0.0));
            model.addAttribute("statusChart", stats.getOrDefault("statusChart", new java.util.HashMap<>()));
            model.addAttribute("serviceChart", stats.getOrDefault("serviceChart", new java.util.HashMap<>()));

            return ConstUtil.TEMPLATE_PAGES_LAUNDRY_HOME;
        } catch (Exception e) {
            // Log error untuk debugging
            System.err.println("Error in LaundryViewController.index: " + e.getMessage());
            e.printStackTrace();
            
            // Redirect ke home jika error
            return "redirect:/";
        }
    }

    // Detail order
    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        if (!authContext.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        try {
            User authUser = authContext.getAuthUser();
            LaundryOrder order = laundryOrderService.getLaundryOrderById(authUser.getId(), id);

            if (order == null) {
                return "redirect:/";
            }

            model.addAttribute("auth", authUser);
            model.addAttribute("order", order);
            model.addAttribute("coverLaundryForm", new CoverLaundryForm());

            return ConstUtil.TEMPLATE_PAGES_LAUNDRY_DETAIL;
        } catch (Exception e) {
            System.err.println("Error in LaundryViewController.detail: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/";
        }
    }

    // Tambah order baru
    @PostMapping("/add")
    public String add(
            @ModelAttribute LaundryOrderForm form,
            RedirectAttributes redirectAttributes) {

        if (!authContext.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        User authUser = authContext.getAuthUser();

        // Validasi
        if (form.getCustomerName() == null || form.getCustomerName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nama pelanggan tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addLaundryModalOpen", true);
            return "redirect:/laundry";
        }

        if (form.getPhoneNumber() == null || form.getPhoneNumber().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nomor telepon tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addLaundryModalOpen", true);
            return "redirect:/laundry";
        }

        if (form.getServiceType() == null || form.getServiceType().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Jenis layanan harus dipilih");
            redirectAttributes.addFlashAttribute("addLaundryModalOpen", true);
            return "redirect:/laundry";
        }

        if (form.getWeight() == null || form.getWeight() <= 0) {
            redirectAttributes.addFlashAttribute("error", "Berat cucian harus lebih dari 0");
            redirectAttributes.addFlashAttribute("addLaundryModalOpen", true);
            return "redirect:/laundry";
        }

        if (form.getPrice() == null || form.getPrice() < 0) {
            redirectAttributes.addFlashAttribute("error", "Harga tidak valid");
            redirectAttributes.addFlashAttribute("addLaundryModalOpen", true);
            return "redirect:/laundry";
        }

        if (form.getStatus() == null || form.getStatus().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Status harus dipilih");
            redirectAttributes.addFlashAttribute("addLaundryModalOpen", true);
            return "redirect:/laundry";
        }

        try {
            // Create order
            LaundryOrder created = laundryOrderService.createLaundryOrder(
                    authUser.getId(),
                    form.getCustomerName().trim(),
                    form.getPhoneNumber().trim(),
                    form.getServiceType().trim(),
                    form.getWeight(),
                    form.getPrice(),
                    form.getStatus().trim(),
                    form.getNotes() != null ? form.getNotes().trim() : null);

            if (created != null) {
                redirectAttributes.addFlashAttribute("success", "Order laundry berhasil ditambahkan!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Gagal menambahkan order");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }

        // Redirect ke home (/) instead of /laundry
        return "redirect:/";
    }

    // Edit order
    @PostMapping("/edit")
    public String edit(
            @ModelAttribute LaundryOrderForm form,
            RedirectAttributes redirectAttributes) {

        if (!authContext.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        User authUser = authContext.getAuthUser();

        // Validasi
        if (form.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID order tidak valid");
            return "redirect:/laundry";
        }

        if (form.getCustomerName() == null || form.getCustomerName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nama pelanggan tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editLaundryModalOpen", true);
            redirectAttributes.addFlashAttribute("editLaundryModalId", form.getId());
            return "redirect:/laundry";
        }

        if (form.getPhoneNumber() == null || form.getPhoneNumber().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nomor telepon tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editLaundryModalOpen", true);
            redirectAttributes.addFlashAttribute("editLaundryModalId", form.getId());
            return "redirect:/laundry";
        }

        if (form.getServiceType() == null || form.getServiceType().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Jenis layanan harus dipilih");
            redirectAttributes.addFlashAttribute("editLaundryModalOpen", true);
            redirectAttributes.addFlashAttribute("editLaundryModalId", form.getId());
            return "redirect:/laundry";
        }

        if (form.getWeight() == null || form.getWeight() <= 0) {
            redirectAttributes.addFlashAttribute("error", "Berat harus lebih dari 0");
            redirectAttributes.addFlashAttribute("editLaundryModalOpen", true);
            redirectAttributes.addFlashAttribute("editLaundryModalId", form.getId());
            return "redirect:/laundry";
        }

        if (form.getPrice() == null || form.getPrice() < 0) {
            redirectAttributes.addFlashAttribute("error", "Harga tidak valid");
            redirectAttributes.addFlashAttribute("editLaundryModalOpen", true);
            redirectAttributes.addFlashAttribute("editLaundryModalId", form.getId());
            return "redirect:/laundry";
        }

        if (form.getStatus() == null || form.getStatus().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Status harus dipilih");
            redirectAttributes.addFlashAttribute("editLaundryModalOpen", true);
            redirectAttributes.addFlashAttribute("editLaundryModalId", form.getId());
            return "redirect:/laundry";
        }

        try {
            // Update order
            LaundryOrder updated = laundryOrderService.updateLaundryOrder(
                    authUser.getId(),
                    form.getId(),
                    form.getCustomerName().trim(),
                    form.getPhoneNumber().trim(),
                    form.getServiceType().trim(),
                    form.getWeight(),
                    form.getPrice(),
                    form.getStatus().trim(),
                    form.getNotes() != null ? form.getNotes().trim() : null);

            if (updated == null) {
                redirectAttributes.addFlashAttribute("error", "Order tidak ditemukan");
            } else {
                redirectAttributes.addFlashAttribute("success", "Order berhasil diperbarui!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }

        return "redirect:/";
    }

    // Hapus order
    @PostMapping("/delete")
    public String delete(
            @ModelAttribute LaundryOrderForm form,
            RedirectAttributes redirectAttributes) {

        if (!authContext.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        User authUser = authContext.getAuthUser();

        if (form.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID order tidak valid");
            return "redirect:/laundry";
        }

        // Get order untuk validasi nama
        LaundryOrder order = laundryOrderService.getLaundryOrderById(authUser.getId(), form.getId());

        if (order == null) {
            redirectAttributes.addFlashAttribute("error", "Order tidak ditemukan");
            return "redirect:/laundry";
        }

        // Validasi konfirmasi nama
        if (form.getConfirmCustomerName() == null || 
            !order.getCustomerName().equalsIgnoreCase(form.getConfirmCustomerName().trim())) {
            redirectAttributes.addFlashAttribute("error", "Nama pelanggan tidak sesuai! Ketik: " + order.getCustomerName());
            redirectAttributes.addFlashAttribute("deleteLaundryModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteLaundryModalId", form.getId());
            return "redirect:/laundry";
        }

        try {
            // Delete order
            boolean deleted = laundryOrderService.deleteLaundryOrder(authUser.getId(), form.getId());

            if (!deleted) {
                redirectAttributes.addFlashAttribute("error", "Gagal menghapus order");
            } else {
                redirectAttributes.addFlashAttribute("success", "Order berhasil dihapus!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }

        return "redirect:/";
    }

    // Upload/Update cover foto
    @PostMapping("/edit-cover")
    public String editCover(
            @ModelAttribute CoverLaundryForm form,
            RedirectAttributes redirectAttributes) {

        if (!authContext.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        User authUser = authContext.getAuthUser();

        if (form.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID order tidak valid");
            return "redirect:/";
        }

        // Validasi file
        MultipartFile file = form.getCoverFile();
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "File foto tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editCoverLaundryModalOpen", true);
            return "redirect:/laundry/" + form.getId();
        }

        // Validasi format
        if (!form.isValidImage()) {
            redirectAttributes.addFlashAttribute("error", "Format file harus JPG, PNG, WEBP, atau GIF");
            redirectAttributes.addFlashAttribute("editCoverLaundryModalOpen", true);
            return "redirect:/laundry/" + form.getId();
        }

        // Validasi ukuran (max 5MB)
        if (!form.isSizeValid(5 * 1024 * 1024)) {
            redirectAttributes.addFlashAttribute("error", "Ukuran file maksimal 5MB");
            redirectAttributes.addFlashAttribute("editCoverLaundryModalOpen", true);
            return "redirect:/laundry/" + form.getId();
        }

        try {
            // Cek apakah order exists dan milik user ini
            LaundryOrder existingOrder = laundryOrderService.getLaundryOrderById(authUser.getId(), form.getId());
            if (existingOrder == null) {
                redirectAttributes.addFlashAttribute("error", "Order tidak ditemukan");
                return "redirect:/";
            }

            // Save file
            String filename = fileStorageService.store(file);
            
            System.out.println("=== Upload Cover Debug ===");
            System.out.println("File uploaded: " + filename);
            System.out.println("Order ID: " + form.getId());

            // Update order
            LaundryOrder updated = laundryOrderService.updateCover(form.getId(), filename);

            if (updated == null) {
                // Hapus file yang baru diupload jika order tidak ditemukan
                fileStorageService.deleteFile(filename);
                redirectAttributes.addFlashAttribute("error", "Gagal update order");
                return "redirect:/";
            }

            System.out.println("✅ Cover updated successfully: " + updated.getCover());
            
            redirectAttributes.addFlashAttribute("success", "Foto berhasil diupload!");
            return "redirect:/laundry/" + form.getId();

        } catch (IOException e) {
            System.err.println("❌ IOException: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Gagal mengupload foto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editCoverLaundryModalOpen", true);
            return "redirect:/laundry/" + form.getId();
        } catch (Exception e) {
            System.err.println("❌ Exception: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editCoverLaundryModalOpen", true);
            return "redirect:/laundry/" + form.getId();
        }
    }

    // Serve cover image
    @GetMapping("/cover/{filename:.+}")
    public ResponseEntity<Resource> serveCover(@PathVariable String filename) {
        try {
            Resource resource = fileStorageService.loadAsResource(filename);

            String contentType = "image/jpeg";
            if (filename.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (filename.toLowerCase().endsWith(".webp")) {
                contentType = "image/webp";
            } else if (filename.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}