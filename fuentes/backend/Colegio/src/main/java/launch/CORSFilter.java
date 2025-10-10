package launch;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*"); // Permite solicitudes desde cualquier origen
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization"); // Encabezados permitidos
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true"); // Permitir cookies o credenciales
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD"); // Métodos HTTP permitidos
        responseContext.getHeaders().add("Access-Control-Max-Age", "1209600"); // Tiempo de vida en segundos de la configuración CORS
    }
}

