package security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase de utilidad criptográfica para el procesamiento seguro de credenciales.
 *
 * Cumple con el requerimiento técnico de seguridad (RNF-02), asegurando que ninguna
 * contraseña se almacene o compare en texto plano dentro de la base de datos.
 *
 * @author Vicente Soto García
 * @version 1.0
 */
public class HashUtil {

    /**
     * Transforma una cadena de texto plano en su representación hexadecimal
     * utilizando el algoritmo de resumen criptográfico SHA-1 (160 bits / 40 caracteres).
     *
     * @param input Cadena original (contraseña ingresada por el usuario).
     * @return Cadena hexadecimal en minúsculas correspondiente al hash SHA-1, o null si la entrada es nula.
     */
    public static String sha1(String input) {
        if (input == null) {
            return null;
        }
        try {
            // Obtiene la instancia del motor criptográfico SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // Convierte la entrada a bytes UTF-8 y calcula el resumen (hash)
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convierte el arreglo de bytes resultante a formato hexadecimal legible
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error interno: Algoritmo SHA-1 no disponible en el entorno Java.", e);
        }
    }
}