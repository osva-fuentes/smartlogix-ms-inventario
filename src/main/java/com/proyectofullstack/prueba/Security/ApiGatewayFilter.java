package com.proyectofullstack.prueba.Security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@Slf4j // Usamos logs para demostrar en consola que el Gateway está funcionando
public class ApiGatewayFilter implements Filter {

    // Esta es la "contraseña secreta" que el Gateway exigirá
    private static final String TOKEN_SECRETO = "SmartLogix-Token-2024";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Extraemos la ruta a la que intentan entrar
        String path = httpRequest.getRequestURI();

        // Permitimos el paso libre al método OPTIONS (Necesario para el CORS de React)
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // Si intentan entrar a nuestras APIs protegidas
        if (path.startsWith("/api/inventario")) {
            log.info("[API GATEWAY] Petición interceptada hacia: {}", path);

            // Revisamos si traen el carnet (Token)
            String authHeader = httpRequest.getHeader("Authorization");

            if (authHeader == null || !authHeader.equals(TOKEN_SECRETO)) {
                log.warn("[API GATEWAY] Acceso DENEGADO. Token inválido o ausente.");

                // Si no traen token o es falso, los rebotamos con un error 401 (Unauthorized)
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Acceso denegado por API Gateway: Token invalido");
                return;
            }
            log.info("[API GATEWAY] Acceso PERMITIDO. Token válido.");
        }

        // Si todo está bien, los dejamos pasar a su destino original (Controller)
        chain.doFilter(request, response);
    }
}
