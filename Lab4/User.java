import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private UUID id;
    private String email;
    private String passwordHash;
    private String name;
    private String avatarUrl;
    private UserRole role;
    private boolean emailVerified;
    private List<Enrollment> enrollments;
    private List<QuizAttempt> quizAttempts;
    private List<Booking> bookings;
    private List<Comment> comments;
    private LocalDateTime createdAt;

    public enum UserRole {
        STUDENT,
        TEACHER,
        ADMIN
    }

    private User(String email, String passwordHash, String name, UserRole role) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
        this.emailVerified = false;
        this.createdAt = LocalDateTime.now();
        this.avatarUrl = "";
        this.enrollments = new ArrayList<>();
        this.quizAttempts = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public static User register(String email, String password, UserRole role) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        String passwordHash = hashPassword(password);
        User newUser = new User(email.trim().toLowerCase(), passwordHash, extractName(email), role);

        System.out.println("[EduConnect] User registered successfully:");
        System.out.println("  ID    : " + newUser.id);
        System.out.println("  Email : " + newUser.email);
        System.out.println("  Role  : " + newUser.role);
        System.out.println("  Created: " + newUser.createdAt);

        return newUser;
    }

    public String login(String email, String password) {
        if (!this.email.equalsIgnoreCase(email)) {
            throw new SecurityException("No account found for: " + email);
        }
        if (!this.passwordHash.equals(hashPassword(password))) {
            throw new SecurityException("Incorrect password.");
        }
        if (!this.emailVerified) {
            throw new SecurityException("Email not verified. Please check your inbox.");
        }

        String token = "TOKEN-" + UUID.randomUUID();
        System.out.println("[EduConnect] Login successful for " + this.name + ". Token: " + token);
        return token;
    }

    public String loginWithOAuth(String provider) {
        String token = "OAUTH-" + provider.toUpperCase() + "-" + UUID.randomUUID();
        System.out.println("[EduConnect] OAuth login via " + provider + ". Token: " + token);
        return token;
    }

    /**
     *
     * @param email
     */
    public void resetPassword(String email) {
        if (!this.email.equalsIgnoreCase(email)) {
            System.out.println("[EduConnect] No account found for " + email + " (reset silently ignored).");
            return;
        }
        String resetToken = UUID.randomUUID().toString();
        System.out.println("[EduConnect] Password reset email sent to: " + email);
        System.out.println("  Reset token (for demo): " + resetToken);
    }

    public void verifyEmail(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Verification token cannot be empty.");
        }
        this.emailVerified = true;

        System.out.println("[EduConnect] Email verified for: " + this.email);
    }

    /**
     *
     * @param raw
     * @return
     */

    private static String hashPassword(String raw) {
        return "HASH[" + raw.length() + "]-" + raw.hashCode();
    }

    /**
     *
     * @param email
     * @return
     */
    private static String extractName(String email) {
        String local = email.split("@")[0].replace(".", " ").replace("_", " ");
        StringBuilder sb = new StringBuilder();
        for (String word : local.split(" ")) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', name='" + name
                + "', role=" + role + ", verified=" + emailVerified + "}";
    }

    static class Enrollment { }

    static class Booking {
    }

    static class Comment {
    }


}
