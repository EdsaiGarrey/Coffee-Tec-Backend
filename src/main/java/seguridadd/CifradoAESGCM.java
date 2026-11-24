
package seguridadd;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class CifradoAESGCM {

    // Configuración AES-GCM
    private static final String PREFIJO = "v1:";
    private static final int TAMANO_IV = 12;
    private static final int TAMANO_TAG = 128;

    private static final SecureRandom ALEATORIO =
            new SecureRandom();

    private CifradoAESGCM() {
    }

    // Obtener la clave desde una variable de entorno
    private static SecretKeySpec obtenerClave() {

        String valor = System.getenv("COFFEETEC_AES_KEY");

        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalStateException(
                "Falta configurar COFFEETEC_AES_KEY"
            );
        }

        byte[] clave;

        try {
            clave = Base64.getDecoder().decode(valor.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                "La clave AES no tiene formato Base64", e
            );
        }

        // AES-256 necesita 32 bytes
        if (clave.length != 32) {
            throw new IllegalStateException(
                "La clave AES debe contener 32 bytes"
            );
        }

        return new SecretKeySpec(clave, "AES");
    }

    // CIFRAR INFORMACIÓN
    public static String cifrar(String texto) {

        if (texto == null) {
            return null;
        }

        try {

            // Generar un IV aleatorio para cada cifrado
            byte[] iv = new byte[TAMANO_IV];
            ALEATORIO.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(
                "AES/GCM/NoPadding"
            );

            cipher.init(
                Cipher.ENCRYPT_MODE,
                obtenerClave(),
                new GCMParameterSpec(TAMANO_TAG, iv)
            );

            byte[] contenido = cipher.doFinal(
                texto.getBytes(StandardCharsets.UTF_8)
            );

            // Guardar IV + contenido cifrado
            byte[] resultado =
                new byte[iv.length + contenido.length];

            System.arraycopy(
                iv, 0, resultado, 0, iv.length
            );

            System.arraycopy(
                contenido, 0, resultado,
                iv.length, contenido.length
            );

            // Base64 solamente representa los bytes cifrados
            return PREFIJO +
                Base64.getEncoder().encodeToString(resultado);

        } catch (GeneralSecurityException e) {

            throw new IllegalStateException(
                "Error al cifrar información", e
            );
        }
    }

    // DESCIFRAR INFORMACIÓN
    public static String descifrar(String textoCifrado) {

        if (textoCifrado == null) {
            return null;
        }

        // Permite leer registros antiguos que todavía
        // no hayan sido cifrados.
        if (!textoCifrado.startsWith(PREFIJO)) {
            return textoCifrado;
        }

        try {

            byte[] datos = Base64.getDecoder().decode(
                textoCifrado.substring(PREFIJO.length())
            );

            if (datos.length < TAMANO_IV + 16) {
                throw new IllegalArgumentException(
                    "Datos cifrados incompletos"
                );
            }

            // Separar IV y contenido cifrado
            byte[] iv = Arrays.copyOfRange(
                datos, 0, TAMANO_IV
            );

            byte[] contenido = Arrays.copyOfRange(
                datos, TAMANO_IV, datos.length
            );

            Cipher cipher = Cipher.getInstance(
                "AES/GCM/NoPadding"
            );

            cipher.init(
                Cipher.DECRYPT_MODE,
                obtenerClave(),
                new GCMParameterSpec(TAMANO_TAG, iv)
            );

            byte[] original = cipher.doFinal(contenido);

            return new String(
                original,
                StandardCharsets.UTF_8
            );

        } catch (GeneralSecurityException |
                 IllegalArgumentException e) {

            throw new IllegalStateException(
                "No se pudo descifrar. Verifica la clave y los datos.",
                e
            );
        }
    }
}

