package com.hms.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;

/**
 * Utility class for retrieving secrets from AWS Secrets Manager.
 *
 * Cloud-readiness fix (cr-java-0113 – Lack of Externalized Secrets):
 *   All application secrets (database passwords, API keys, encryption keys,
 *   authentication tokens, etc.) must be stored in AWS Secrets Manager and
 *   retrieved at runtime via this utility rather than being embedded in source
 *   code, property files, or local configuration files.
 *
 * Usage example:
 * <pre>
 *   String dbPassword = SecretsManagerUtil.getSecret("hms/db/password");
 * </pre>
 *
 * The AWS region is resolved from the environment variable AWS_REGION (with a
 * fallback to "us-east-1").  Ensure the EC2 instance / ECS task / Lambda
 * function running this application has an IAM role that grants
 * secretsmanager:GetSecretValue on the required secret ARNs.
 */
public class SecretsManagerUtil {

    /** Environment variable that controls the target AWS region. */
    private static final String AWS_REGION_ENV = "AWS_REGION";

    /** Default region used when AWS_REGION is not set. */
    private static final String DEFAULT_REGION = "us-east-1";

    private SecretsManagerUtil() {
        // Utility class – do not instantiate.
    }

    /**
     * Retrieves the plaintext value of the named secret from AWS Secrets Manager.
     *
     * @param secretName the name or ARN of the secret (e.g. "hms/db/password")
     * @return the secret string value
     * @throws RuntimeException if the secret cannot be retrieved
     */
    public static String getSecret(String secretName) {
        String region = System.getenv(AWS_REGION_ENV);
        if (region == null || region.isEmpty()) {
            region = DEFAULT_REGION;
        }

        AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .build();

        GetSecretValueRequest request = new GetSecretValueRequest()
                .withSecretId(secretName);

        GetSecretValueResult result = client.getSecretValue(request);

        // Secrets Manager returns either a SecretString or SecretBinary.
        // This application uses text-based secrets exclusively.
        if (result.getSecretString() != null) {
            return result.getSecretString();
        }

        throw new RuntimeException(
                "Secret '" + secretName + "' does not contain a string value. "
                + "Binary secrets are not supported by this utility.");
    }
}
