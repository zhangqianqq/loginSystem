import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin123");
        System.out.println("=== BCrypt Password Hash ===");
        System.out.println("Plain: admin123");
        System.out.println("Hash:   " + hash);
        System.out.println();
        System.out.println("MySQL Update SQL:");
        System.out.println("UPDATE users");
        System.out.println("SET password = '" + hash + "'");
        System.out.println("WHERE username = 'admin';");
    }
}
