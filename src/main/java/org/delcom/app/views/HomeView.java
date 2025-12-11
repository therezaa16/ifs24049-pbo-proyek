package org.delcom.app.views;

import java.util.Map;

import org.delcom.app.dto.LaundryOrderForm;
import org.delcom.app.entities.User;
import org.delcom.app.services.LaundryOrderService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeView {

    private final LaundryOrderService laundryOrderService;

    public HomeView(LaundryOrderService laundryOrderService) {
        this.laundryOrderService = laundryOrderService;
    }

    @GetMapping
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if authentication is null or anonymous
        if (authentication == null || (authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }

        User authUser = (User) principal;
        model.addAttribute("auth", authUser);

        // Laundry Orders
        var laundryOrders = laundryOrderService.getAllLaundryOrders(authUser.getId(), "");
        model.addAttribute("laundryOrders", laundryOrders);

        // ✅ FIX: Ambil statistics dari service
        Map<String, Object> stats = laundryOrderService.getStatistics(authUser.getId());
        
        // Laundry Order Form
        model.addAttribute("laundryOrderForm", new LaundryOrderForm());
        
        // ✅ FIX: Kirim data statistics ke view
        model.addAttribute("totalRevenue", stats.get("totalRevenue"));
        model.addAttribute("ongoingOrders", stats.get("ongoingOrders"));
        model.addAttribute("totalOrders", stats.get("totalOrders"));
        model.addAttribute("pendingOrders", stats.get("pendingOrders"));
        model.addAttribute("statusChart", stats.get("statusChart"));
        model.addAttribute("serviceChart", stats.get("serviceChart"));
        model.addAttribute("search", "");

        return ConstUtil.TEMPLATE_PAGES_HOME;
    }
}