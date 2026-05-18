package com.kadoozin.encurtador_url.controller;

import com.kadoozin.encurtador_url.database.enums.RedirectStatus;
import com.kadoozin.encurtador_url.dto.request.UrlRequest;
import com.kadoozin.encurtador_url.dto.response.UrlResponse;
import com.kadoozin.encurtador_url.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping(value = "/encurtar-url")
    public ResponseEntity<UrlResponse> encurtarUrl(@RequestBody UrlRequest request, HttpServletRequest servletRequest) {
        var urlResponse = urlService.encurtarUrl(request, servletRequest.getRequestURL().toString());
        if (urlResponse.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(urlResponse.get());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Void> redirect(@PathVariable("id") String id) {
        var redirectResult = urlService.redirect(id);
        if (redirectResult.status() == RedirectStatus.NOT_FOUND) {
            return ResponseEntity.notFound().build();
        }
        if (redirectResult.status() == RedirectStatus.GONE) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectResult.location());
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }
}
