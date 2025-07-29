package com.example.expensetrackapp.auth.services;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import io.github.mihkels.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

public class JwtService {
	public static final Logger logger = LoggerFactory.getLogger(JwtService.class);

	// Take JWT_SECRET from file .env
	private static final Dotenv dotenv = Dotenv.configure().directory("C:\\Users\\Admin\\git\\repository\\expensetrackapp").load();
	private static final String SECRET_STRING = dotenv.get("JWT_SECRET");
	public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(SECRET_STRING));

	// Effect time for 15 minutes
	private static final long EXPIRATION_TIME_ACCESS = 10_000;
	public static final long EXPIRATION_TIME_REFRESH = 432_000_000;

	public String generateAccessToken(String user_id , String username) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("user_id", user_id); // Thêm thông tin vai trò vào token

		return Jwts.builder().claims(claims).subject(username)
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_ACCESS))
				.issuedAt(new Date(System.currentTimeMillis())).signWith(SECRET_KEY).compact();
	}
	
	public String generateRefreshToken(String user_id, String accessToken) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("user_id", user_id); // Thêm thông tin vai trò vào token

		return Jwts.builder().claims(claims).subject(accessToken)
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_REFRESH))
				.issuedAt(new Date(System.currentTimeMillis())).signWith(SECRET_KEY).compact();
	}

	public Claims extractAllClaims(String token) {
		try {
			return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
		} catch (Exception e) {
			logger.warn("JWT validation failed: {}", e.getMessage());
			return null; // Trả về null nếu token không hợp lệ
		}
	}

	public String extractUsername(String token) {
		Claims claims = extractAllClaims(token);

		return (claims != null) ? claims.getSubject() : null;
	}

	public boolean isTokenValid(String token) {
		Claims claims = extractAllClaims(token);
		long timeExpiration = claims.getExpiration().getTime() - new Date().getTime();

		return timeExpiration > 0 ? true : false;
	}

}
