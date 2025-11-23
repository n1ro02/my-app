package com.example.shortlink.service;

import com.example.shortlink.model.ShortLink;
import com.example.shortlink.repository.ShortLinkRepository;
import java.security.SecureRandom;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShortLinkService {

  private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int GENERATED_LENGTH = 8;

  private final ShortLinkRepository repository;
  private final SecureRandom random = new SecureRandom();

  @Value("${shortener.base-url:http://localhost:8080}")
  private String baseUrl;

  public ShortLinkService(ShortLinkRepository repository) {
    this.repository = repository;
  }

  public List<ShortLink> listAll() {
    return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
  }

  public ShortLink create(String targetUrl, String customCode) {
    if (!StringUtils.hasText(targetUrl)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "目标地址不能为空");
    }

    final String code = chooseCode(customCode);
    if (repository.existsByCode(code)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "短码已存在，请更换其他短码");
    }

    ShortLink link = new ShortLink();
    link.setCode(code);
    link.setTargetUrl(targetUrl.trim());
    return repository.save(link);
  }

  public ShortLink resolve(String code) {
    return repository
        .findByCode(code)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "未找到对应的短链接"));
  }

  public String buildShortUrl(String code) {
    String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    return normalizedBase + "/r/" + code;
  }

  private String chooseCode(String customCode) {
    if (StringUtils.hasText(customCode)) {
      return customCode.trim();
    }
    String candidate;
    do {
      candidate = generateCode();
    } while (repository.existsByCode(candidate));
    return candidate;
  }

  private String generateCode() {
    StringBuilder builder = new StringBuilder(GENERATED_LENGTH);
    for (int i = 0; i < GENERATED_LENGTH; i++) {
      int index = random.nextInt(ALPHABET.length());
      builder.append(ALPHABET.charAt(index));
    }
    return builder.toString();
  }
}
