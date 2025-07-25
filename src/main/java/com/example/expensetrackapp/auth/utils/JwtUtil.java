package com.example.expensetrackapp.auth.utils;

import javax.crypto.SecretKey;

import com.example.expensetrackapp.auth.services.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JwtUtil {
	private static JwtService jwtService;

	/**
	 * Tạo một refresh token mới từ user_id và user_name, sau đó giải mã token đó để
	 * trích xuất lại giá trị của user_id từ payload.
	 *
	 * ⚠️ Lưu ý: - Phương thức này không nhận token từ bên ngoài, mà tự tạo ra token
	 * trước rồi giải mã nó. - Nếu xảy ra lỗi khi parse token (ví dụ chữ ký sai), sẽ
	 * trả về token thô (jws).
	 *
	 * @param user_id   ID của người dùng, sẽ được đưa vào payload của JWT.
	 * @param user_name Tên người dùng, cũng sẽ được đưa vào payload.
	 * @return Giá trị user_id được trích xuất từ token (nếu thành công), hoặc trả
	 *         về token đã tạo nếu có lỗi xảy ra trong quá trình giải mã.
	 */
	public static String extractUserId(String token) {
		SecretKey key = JwtService.SECRET_KEY;
	
		try {
			Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

			return claims.get("user_id", String.class);
		} catch (JwtException e) {

		}

		return token;

	}
}
