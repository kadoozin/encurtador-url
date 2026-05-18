package com.kadoozin.encurtador_url.service;

import com.kadoozin.encurtador_url.database.entities.Url;
import com.kadoozin.encurtador_url.database.enums.RedirectStatus;
import com.kadoozin.encurtador_url.database.repository.UrlRepository;
import com.kadoozin.encurtador_url.dto.request.UrlRequest;
import com.kadoozin.encurtador_url.dto.response.UrlResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlService {
    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public Optional<UrlResponse> encurtarUrl(UrlRequest request, String requestUrl) {
        if (request == null || request.url() == null || request.url().isBlank()) {
            return Optional.empty();
        }

        URI destinationUri;
        try {
            destinationUri = URI.create(request.url());
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }

        if (destinationUri.getScheme() == null || destinationUri.getHost() == null) {
            return Optional.empty();
        }

        if (!"http".equalsIgnoreCase(destinationUri.getScheme()) && !"https".equalsIgnoreCase(destinationUri.getScheme())) {
            return Optional.empty();
        }

        String id;
        do {
            id = RandomStringUtils.randomAlphanumeric(5, 10);
        } while (urlRepository.existsById(id));

        urlRepository.save(new Url(id, request.url(), LocalDateTime.now().plusMinutes(1)));

        var redirectUrl = requestUrl.replace("encurtar-url", id);

        return Optional.of(new UrlResponse(redirectUrl));
    }

    public RedirectResult redirect(String id) {
        var url = urlRepository.findById(id);

        if (url.isEmpty()) {
            return new RedirectResult(RedirectStatus.NOT_FOUND, null);
        }

        if (url.get().getExpiresAt() == null || url.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            return new RedirectResult(RedirectStatus.GONE, null);
        }

        return new RedirectResult(RedirectStatus.FOUND, URI.create(url.get().getFullUrl()));
    }

    public record RedirectResult(RedirectStatus status, URI location) {
    }

}
