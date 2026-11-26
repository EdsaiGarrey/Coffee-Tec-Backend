package mx.coffeetec.servlet.base;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/api/*")
public class PublicReadOnlyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String ruta = req.getServletPath();
        boolean lectura = "GET".equalsIgnoreCase(req.getMethod());
        boolean productos = ruta.equals("/api/productos");

        if (lectura && productos) {
            chain.doFilter(request, response);
            return;
        }

        resp.sendError(403, "Acceso administrativo no habilitado");
    }
}
