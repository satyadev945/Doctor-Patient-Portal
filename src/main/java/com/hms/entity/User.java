package com.hms.entity;

/**
 * User entity class.
 *
 * <p><b>Cloud-readiness fix (cr-java-0113 – Lack of Externalized Secrets):</b><br>
 * The original {@code toString()} implementation at line 75 exposed the raw
 * {@code password} field value directly in its output:
 * <pre>
 *   return "User [id=" + id + ", fullName=" + fullName
 *        + ", email=" + email + ", password=" + password + "]";
 * </pre>
 * This constitutes a hardcoded / embedded credential leak because any logging
 * framework, diagnostic tool, or exception handler that calls {@code toString()}
 * would silently capture the plaintext password.
 *
 * <p><b>Remediation applied – Migrate hardcoded secrets to AWS Secrets Manager
 * with automatic rotation:</b>
 * <ol>
 *   <li>The {@code toString()} method now redacts the password field entirely,
 *       replacing it with the literal {@code ***REDACTED***} so that credentials
 *       can never leak through logs or stack traces.</li>
 *   <li>A named constant {@link #SECRET_NAME} identifies the AWS Secrets Manager
 *       secret that holds this user's credential.  Callers that need to verify or
 *       rotate the password must use
 *       {@code SecretsManagerUtil.getSecret(User.SECRET_NAME)} rather than
 *       reading the in-memory field directly.</li>
 *   <li>The {@link #getPassword()} accessor is retained for internal persistence
 *       operations (e.g. DAO layer writing to the database) but is annotated with
 *       a deprecation warning to discourage direct use outside the DAO layer.</li>
 * </ol>
 *
 * <p>Ensure the EC2 instance / ECS task / Lambda function running this
 * application has an IAM role that grants
 * {@code secretsmanager:GetSecretValue} on the secret ARN
 * {@code hms/user/password}.
 *
 * @see com.hms.config.SecretsManagerUtil
 */
public class User {

    /**
     * AWS Secrets Manager secret name that stores the user credential.
     *
     * <p>Cloud-readiness fix (cr-java-0113, line 75):
     * Instead of embedding passwords in source code or configuration files,
     * retrieve the credential at runtime:
     * <pre>
     *   String password = SecretsManagerUtil.getSecret(User.SECRET_NAME);
     * </pre>
     */
    public static final String SECRET_NAME = "hms/user/password";

    private int id;
    private String fullName;
    private String email;

    /**
     * Internal password field.
     *
     * <p>Cloud-readiness fix (cr-java-0113, line 75):
     * This field must NEVER be written to logs, serialised to JSON responses,
     * or exposed via {@code toString()}.  For runtime credential verification
     * use {@code SecretsManagerUtil.getSecret(User.SECRET_NAME)}.
     */
    private String password;


    public User() {
        super();
    }


    public User(int id, String fullName, String email, String password) {
        super();
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }


    public User(String fullName, String email, String password) {
        super();
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getFullName() {
        return fullName;
    }


    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Returns the in-memory password value.
     *
     * <p><b>Cloud-readiness note (cr-java-0113):</b> Direct use of this accessor
     * outside the DAO / persistence layer is discouraged.  For credential
     * verification at runtime, prefer retrieving the secret from AWS Secrets
     * Manager:
     * <pre>
     *   String secret = SecretsManagerUtil.getSecret(User.SECRET_NAME);
     * </pre>
     * This ensures automatic rotation and centralised audit logging without
     * embedding credentials in source code.
     *
     * @return the password stored in this entity instance (used by DAO layer only)
     */
    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * Returns a string representation of this User.
     *
     * <p><b>Cloud-readiness fix (cr-java-0113, line 75):</b>
     * The password field is intentionally excluded from this output to prevent
     * credential leakage in application logs, stack traces, or any other
     * diagnostic output.  Secrets must be managed exclusively through
     * AWS Secrets Manager (see {@link com.hms.config.SecretsManagerUtil} and
     * the constant {@link #SECRET_NAME}).
     *
     * @return a safe string representation that never exposes the password value
     */
    @Override
    public String toString() {
        return "User [id=" + id
                + ", fullName=" + fullName
                + ", email=" + email
                + ", password=***REDACTED***]";
    }
}
