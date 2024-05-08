package com.gft.home.broker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gft.home.broker.resource.Asset;
import com.gft.home.broker.services.AssetServices;

@Controller
public class HomeController {

    @Autowired
    private AssetServices homeService;

    @GetMapping("/home")
    public String home(Model model) {
        List<Asset> listAssetsPanel = homeService.getAssetPanel();
        model.addAttribute("listAssetsPanel", listAssetsPanel);
        return "home";
    }
}
