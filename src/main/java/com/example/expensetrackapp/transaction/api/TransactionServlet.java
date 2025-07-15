package com.example.expensetrackapp.transaction.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.transaction.model.AddTransactionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/transaction")
public class TransactionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(TransactionServlet.class);
	private ObjectMapper objectMapper;

	@Override
	public void init() throws ServletException {
		super.init();
		objectMapper = new ObjectMapper();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

		PrintWriter out = response.getWriter();
		AddTransactionRequest addTransactionRequest = objectMapper.readValue(request.getReader(),
				AddTransactionRequest.class);
		if (addTransactionRequest.getUser_id() == null || addTransactionRequest.getTitle() == null
				|| addTransactionRequest.getAmount() == 0 || addTransactionRequest.getNote() == null
				|| addTransactionRequest.getType() == null || addTransactionRequest.getWalletId() == null
				|| addTransactionRequest.getCategoryId() == null) {
			out.print("{\"success\": false, \"message\": \"Expense info are required.\"}");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		String currentUserId = addTransactionRequest.getUser_id();
//		if ()

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Trả lại đầy đủ header CORS
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
		response.setHeader("Access-Control-Allow-Credentials", "true"); // nếu dùng cookie

		response.setStatus(HttpServletResponse.SC_OK);
	}

}
