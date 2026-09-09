package com.hms.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for retrieving secrets from AWS Secrets Manager.
 *
 * <p>This class replaces hardcoded credentials, API keys, and encryption keys
 * embedded in source code or property files. All sensitive values (database
 * passwords, API tokens, encryption keys) must be stored in AWS Secrets Manager
 * and retrieved at runtime via this utility.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 *   String dbPassword = AwsSecretsManagerUtil.getSecretValue("hms/db/password", "password");
 * </pre>
 *
 * <p>The AWS region is resolved from the environment variable {@code AWS_REGION}
 * (defaulting to {@code us-east-1} if not set). Credentials are resolved via the
 * standard AWS credential provider chain (IAM role, environment variables, etc.).</p>
 *
 * <h3>Timeout environment variables (cr-java-0097)</h3>
 * <ul>
 *   <li>{@code AWS_SM_CONNECTION_TIMEOUT_MS} – TCP connect timeout for the Secrets Manager
 *       HTTP client in milliseconds (default: 5000).</li>
 *   <li>{@code AWS_SM_SOCKET_TIMEOUT_MS}     – Socket read timeout for the Secrets Manager
 *       HTTP client in milliseconds (default: 10000).</li>
 *   <li>{@code AWS_SM_REQUEST_TIMEOUT_MS}    – Total request timeout (including retries) in
 *       milliseconds (default: 15000).</li>
 *   <li>{@code AWS_SM_MAX_RETRIES}           – Maximum number of automatic retries on
 *       transient failures (default: 3).</li>
 * </ul>
 */
public class AwsSecretsManagerUtil {

    private static final String DEFAULT_REGION = "us-east-1";

    // Default timeout values (milliseconds) – tunable via environment variables.
    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 5_000;
    private static final int DEFAULT_SOCKET_TIMEOUT_MS     = 10_000;
    private static final int DEFAULT_REQUEST_TIMEOUT_MS    = 15_000;
    private static final int DEFAULT_MAX_RETRIES           = 3;

    /**
     * Retrieves the entire secret string for the given secret name from AWS Secrets Manager.
     *
     * <p>The AWS SDK client is configured with explicit connection, socket, and request
     * timeouts to prevent indefinite hangs in cloud environments with variable network
     * latency or transient service failures (cr-java-0097).</p>
     *
     * @param secretName the name or ARN of the secret in AWS Secrets Manager
     * @return the secret string value
     * @throws RuntimeException if the secret cannot be retrieved
     */
    public static String getSecret(String secretName) {
        String region = System.getenv("AWS_REGION") != null
                ? System.getenv("AWS_REGION")
                : DEFAULT_REGION;

        // --- Explicit timeout configuration (cr-java-0097) --------------------
        // Configure connection, socket, and request timeouts on the AWS SDK HTTP
        // client to prevent indefinite hangs when the Secrets Manager endpoint is
        // slow or unreachable in cloud environments.
        ClientConfiguration clientConfig = new ClientConfiguration()
                // TCP connect timeout: max ms to establish a connection to the endpoint.
                .withConnectionTimeout(getEnvInt("AWS_SM_CONNECTION_TIMEOUT_MS", DEFAULT_CONNECTION_TIMEOUT_MS))
                // Socket timeout: max ms to wait for data on an established connection.
                .withSocketTimeout(getEnvInt("AWS_SM_SOCKET_TIMEOUT_MS", DEFAULT_SOCKET_TIMEOUT_MS))
                // Request timeout: max ms for the entire request (including retries).
                .withRequestTimeout(getEnvInt("AWS_SM_REQUEST_TIMEOUT_MS", DEFAULT_REQUEST_TIMEOUT_MS))
                // Limit automatic retries to avoid excessive latency on persistent failures.
                .withMaxErrorRetry(getEnvInt("AWS_SM_MAX_RETRIES", DEFAULT_MAX_RETRIES));

        AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .withClientConfiguration(clientConfig)
                .build();

        GetSecretValueRequest request = new GetSecretValueRequest()
                .withSecretId(secretName);

        GetSecretValueResult result = client.getSecretValue(request);
        return result.getSecretString();
    }

    /**
     * Retrieves a specific key from a JSON-formatted secret stored in AWS Secrets Manager.
     *
     * <p>For example, if the secret {@code hms/db/credentials} contains:
     * {@code {"username":"root","password":"s3cr3t"}}, then calling
     * {@code getSecretValue("hms/db/credentials", "password")} returns {@code "s3cr3t"}.</p>
     *
     * @param secretName the name or ARN of the secret in AWS Secrets Manager
     * @param key        the JSON key whose value should be returned
     * @return the string value associated with the given key
     * @throws RuntimeException if the secret cannot be retrieved or the key is not found
     */
    public static String getSecretValue(String secretName, String key) {
        try {
            String secretJson = getSecret(secretName);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(secretJson);
            if (!node.has(key)) {
                throw new RuntimeException(
                        "Key '" + key + "' not found in secret '" + secretName + "'");
            }
            return node.get(key).asText();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse secret '" + secretName + "' for key '" + key + "'", e);
        }
    }

    // -----------------------------------------------------------------------
    // Utility helpers
    // -----------------------------------------------------------------------

    private static int getEnvInt(String name, int defaultValue) {
        try {
            String value = System.getenv(name);
            return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
