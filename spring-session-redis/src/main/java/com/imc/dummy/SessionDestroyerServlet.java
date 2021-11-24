package com.imc.dummy;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/destroy")
public class SessionDestroyerServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		req.getSession().invalidate();
		resp.sendRedirect(req.getContextPath() + "/");
	}
	
	private static final long serialVersionUID = 2878267318695777395L;

}