package com.example.shortlink.web;

import com.example.shortlink.model.ShortLink;
import com.example.shortlink.service.ShortLinkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/links")
@CrossOrigin(origins = "http://localhost:5173")
public class ShortLinkController {

  private final ShortLinkService service;

  public ShortLinkController(ShortLinkService service) {
    this.service = service;
  }

  @PostMapping
  public ShortLinkResponse create(@Valid @RequestBody CreateShortLinkRequest request) {
    ShortLink link = service.create(request.targetUrl(), request.customCode());
    return ShortLinkResponse.from(link, service.buildShortUrl(link.getCode()));
  }

  @GetMapping
  public List<ShortLinkResponse> list() {
    return service.listAll().stream()
        .map(link -> ShortLinkResponse.from(link, service.buildShortUrl(link.getCode())))
        .toList();
  }

  public record CreateShortLinkRequest(@NotBlank String targetUrl, String customCode) {}

  public record ShortLinkResponse(
      Long id, String code, String targetUrl, LocalDateTime createdAt, String shortUrl) {
    public static ShortLinkResponse from(ShortLink link, String shortUrl) {
      return new ShortLinkResponse(
          link.getId(), link.getCode(), link.getTargetUrl(), link.getCreatedAt(), shortUrl);
    }
  }
}
