# 🔒 Security Configuration Guide

This document provides instructions for configuring sensitive information in the coding platform infrastructure.

## ⚠️ Important Security Notice

**NEVER commit sensitive information to version control!** This repository contains placeholder values that must be replaced with your actual configuration.

## 🔧 Configuration Steps

### 1. Ansible Secrets Configuration

#### File: `ansible/secrets.auto.yml`

Replace the following placeholder values:

```yaml
# Replace YOUR_ACCOUNT_ID with your AWS Account ID (12-digit number)
sqs_request_url: "https://sqs.YOUR_REGION.amazonaws.com/YOUR_ACCOUNT_ID/YOUR_REQUEST_QUEUE_NAME"

# Replace YOUR_REGION with your AWS region (e.g., us-east-1, us-west-2)
sqs_response_url: "https://sqs.YOUR_REGION.amazonaws.com/YOUR_ACCOUNT_ID/YOUR_RESPONSE_QUEUE_NAME"

# Replace with your unique S3 bucket name
s3_bucket_name: "your-unique-bucket-name"

# Replace with your RDS endpoint
db_endpoint: "your-db-endpoint.YOUR_REGION.rds.amazonaws.com:5432"

# Replace with your backend server's public IP
BACKEND_IP: "YOUR_BACKEND_SERVER_IP"
```

#### File: `ansible/secrets.yml`

This file contains encrypted secrets using Ansible Vault. To update:

```bash
# Decrypt and edit
ansible-vault edit infrastructure/ansible/secrets.yml

# Add your sensitive values like:
# db_password: "your_secure_database_password"
# jwt_secret: "your_jwt_secret_key"
# aws_access_key: "your_aws_access_key"
# aws_secret_key: "your_aws_secret_key"
```

### 2. Terraform Configuration

#### File: `terraform/main.tf`

Update the following sections:

```hcl
# Update the SSH key path
resource "local_file" "ansible_ssh_key" {
  filename = "/your/actual/path/to/ansible/keys/ansible-key.pem"
}

# Update S3 bucket name (must be globally unique)
resource "aws_s3_bucket" "submission" {
  bucket = "your-globally-unique-bucket-name"
}

# Update SQS queue names
resource "aws_sqs_queue" "request" {
  name = "your-submission-request-queue"
}

# Update database identifier
resource "aws_db_instance" "postgres" {
  identifier = "your-coding-platform-db"
}
```

### 3. Application Configuration

#### Backend Application Properties

Set environment variables for the backend service:

```bash
export DB_PASSWORD="your_secure_database_password"
export JWT_SECRET="your_jwt_secret_key"
export AWS_ACCESS_KEY_ID="your_aws_access_key"
export AWS_SECRET_ACCESS_KEY="your_aws_secret_key"
```

### 4. Environment Variables Setup

Create environment-specific configuration files:

#### Development Environment

```bash
# .env.development (DO NOT COMMIT)
DB_PASSWORD=dev_password
JWT_SECRET=dev_jwt_secret
AWS_ACCESS_KEY_ID=dev_aws_key
AWS_SECRET_ACCESS_KEY=dev_aws_secret
```

#### Production Environment

```bash
# .env.production (DO NOT COMMIT)
DB_PASSWORD=prod_secure_password
JWT_SECRET=prod_jwt_secret_256_bit
AWS_ACCESS_KEY_ID=prod_aws_key
AWS_SECRET_ACCESS_KEY=prod_aws_secret
```

## 🛡️ Security Best Practices

### 1. AWS IAM Configuration

- Create specific IAM users for different environments
- Use least-privilege principle
- Rotate access keys regularly
- Use AWS Secrets Manager for production

### 2. Database Security

- Use strong passwords (minimum 12 characters)
- Enable encryption at rest
- Restrict database access to application servers only
- Regular security updates

### 3. JWT Configuration

- Use a strong secret key (minimum 256 bits)
- Set appropriate token expiration times
- Implement token refresh mechanism
- Consider using asymmetric keys for production

### 4. Network Security

- Configure security groups with minimal required access
- Use VPC for network isolation
- Enable VPC Flow Logs
- Consider using AWS WAF for web application protection

## 🔄 Setup Checklist

- [ ] Replace all placeholder values in `secrets.auto.yml`
- [ ] Create and encrypt `secrets.yml` with Ansible Vault
- [ ] Update Terraform configuration with your resource names
- [ ] Configure environment variables for applications
- [ ] Set up IAM users and policies
- [ ] Create S3 bucket with appropriate permissions
- [ ] Set up SQS queues with proper access policies
- [ ] Configure RDS instance with security groups
- [ ] Test connectivity between all services
- [ ] Verify secrets are not exposed in logs

## 🚨 Emergency Procedures

### If Secrets Are Accidentally Exposed:

1. **Immediately rotate all exposed credentials**
2. **Review access logs for any unauthorized usage**
3. **Update all affected services with new credentials**
4. **Remove exposed commits from Git history if necessary**
5. **Review and strengthen security procedures**

### Contact Information

- Security issues: Create a private issue in the repository
- Emergency: Contact the development team immediately

---

**Remember: Security is everyone's responsibility!**
