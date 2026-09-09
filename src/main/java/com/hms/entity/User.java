package com.hms.entity;

import com.hms.util.AwsSecretsManagerUtil;

/**
 * User entity class.
 *
 * <p><strong>Cloud Readiness — AWS Secrets Manager Integration (cr-java-0113):</strong><br>
 * Passwords and other sensitive credentials must NEVER be embedded in source code,
 * property files, or exposed via logging/toString(). This class has been updated to:
 * <ul>
 *   <li>Exclude the {@code password} field from {@link #toString()} to prevent
 *       accidental credential leakage in application logs, stack traces, or debug output.</li>
 *   <li>Provide {@link #getPasswordFromSecretsManager(String)} as the recommended
 *       cloud-native way to retrieve a user's stored credential from AWS Secrets Manager
 *       at runtime, rather than holding a plaintext password in memory longer than needed.</li>
 * </ul>
 * All sensitive values (passwords, API keys, encryption keys) must be stored in
 * AWS Secrets Manager and retrieved via {@link AwsSecretsManagerUtil}.</p>
 */
public class User {

	private int id;
	private String fullName;
	private String email;
	/**
	 * The password field is retained for compatibility with existing DAO/servlet code
	 * that performs in-memory comparison after retrieval from the database.
	 * It must NEVER be written to logs, included in toString(), or serialised to
	 * any external system. For new integrations, prefer
	 * {@link #getPasswordFromSecretsManager(String)}.
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


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	/**
	 * Retrieves this user's password from AWS Secrets Manager at runtime.
	 *
	 * <p>The secret is expected to be a JSON object stored under the given
	 * {@code secretName} with a {@code "password"} key, for example:</p>
	 * <pre>
	 *   Secret name : hms/users/{email}/credentials
	 *   Secret value: {"password":"&lt;hashed-or-encrypted-value&gt;"}
	 * </pre>
	 *
	 * <p>This method replaces any pattern where a plaintext password would be
	 * embedded in source code or configuration files (rule cr-java-0113).</p>
	 *
	 * @param secretName the AWS Secrets Manager secret name or ARN that holds
	 *                   this user's credentials
	 * @return the password value retrieved from AWS Secrets Manager
	 * @throws RuntimeException if the secret cannot be retrieved or parsed
	 */
	public String getPasswordFromSecretsManager(String secretName) {
		return AwsSecretsManagerUtil.getSecretValue(secretName, "password");
	}


	/**
	 * Returns a string representation of this User.
	 *
	 * <p><strong>Security note (cr-java-0113):</strong> The {@code password} field is
	 * intentionally excluded to prevent accidental credential exposure in application
	 * logs, stack traces, or debug output. Credentials are managed via AWS Secrets
	 * Manager and must never be embedded or logged in plaintext.</p>
	 *
	 * @return string representation without sensitive fields
	 */
	@Override
	public String toString() {
		// PASSWORD IS INTENTIONALLY OMITTED — cr-java-0113 (Lack of Externalized Secrets)
		// Credentials must be managed via AWS Secrets Manager (AwsSecretsManagerUtil).
		// Never include passwords, tokens, or keys in toString(), logs, or debug output.
		return "User [id=" + id + ", fullName=" + fullName + ", email=" + email + "]";
	}
}
