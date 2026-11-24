
package seguridadd;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class GenerarClaveAES {

    public static void main(String[] args) throws Exception {

        // Crear el generador AES
        KeyGenerator generador =
                KeyGenerator.getInstance("AES");

        // Configurar AES-256
        generador.init(256);

        // Generar la clave aleatoria
        SecretKey clave = generador.generateKey();

        // Obtener los bytes de la clave
        byte[] bytesClave = clave.getEncoded();

        // Convertir a Base64
        String claveBase64 = Base64.getEncoder()
                .encodeToString(bytesClave);

        // Mostrar resultado
        System.out.println("CLAVE AES GENERADA:");
        System.out.println(claveBase64);

        System.out.println("Tamaño: "
                + bytesClave.length + " bytes");
    }
}
