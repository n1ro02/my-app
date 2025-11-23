package com.example.shortlink.web;

import com.example.shortlink.model.ShortLink;
import com.example.shortlink.service.ShortLinkService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RedirectController {

  private final ShortLinkService service;

  public RedirectController(ShortLinkService service) {
    this.service = service;
  }

  @GetMapping("/r/{code}")
  public ResponseEntity<Void> redirect(@PathVariable String code) {
    ShortLink link = service.resolve(code);
    return ResponseEntity.status(302).location(URI.create(link.getTargetUrl())).build();
  }
}
