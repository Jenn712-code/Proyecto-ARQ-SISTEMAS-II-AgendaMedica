package seguridad;
import io.smallrye.jwt.build.Jwt;

import java.time.Duration;
import java.util.Set;

public class TokenUtils {
    private TokenUtils() {
    }

    /**
     * Genera un token JWT firmado usando la clave privada configurada en application.properties.
     *
     * @param correo  Correo del usuario (UPN en el JWT)
     * @param roles   Conjunto de roles o grupos (por ejemplo, "paciente", "admin")
     * @param nombre  Nombre del usuario (se agrega como claim opcional)
     * @return Token JWT firmado
     */
    public static String generateToken(String correo, Set<String> roles, String nombre) {
        return Jwt.issuer("miapp-issuer")        // debe coincidir con mp.jwt.verify.issuer
                .upn(correo)                               // identificador del usuario
                .groups(roles)                             // roles del usuario
                .claim("nombre", nombre)               // claim personalizado opcional
                .expiresIn(Duration.ofHours(1))            // tiempo de expiración
                .sign();                                   // firma el token con privateKey.pem
    }
}
