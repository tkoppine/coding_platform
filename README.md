# Coding Platform

A coding assessment platform that allows candidates to solve programming questions and submit their code for automated evaluation. The system uses AWS services for code execution and result processing.

## System Architecture

Based on the provided architecture diagram, this platform consists of three main servers running on AWS EC2 instances:

```
[Frontend Server] ---> [Backend Server] ---> [Worker Server]
      |                       |                      |
   (Candidate               (Questions              (Code
    Interface)               Controllers)           Execution)
                                |                      |
                          [RDS PostgreSQL]    [Docker Containers]
                                |                      |
                          [AWS S3 Bucket] <---> [SQS Queues]
```

### Components

**Frontend Server (EC2 Instance)**

- React application running in Docker
- Candidate interface for coding questions
- Submit button for code submissions

**Backend Server (EC2 Instance)**

- Spring Boot application running in Docker
- Questions Controller - serves coding questions from database
- Submission Controller - handles code submissions to SQS
- Results Controller - retrieves execution results from SQS

**Worker Server (EC2 Instance)**

- Spring Boot application running in Docker
- Processes code execution requests from SQS
- Runs code in isolated Docker containers (Python, Java)
- Sends execution results back via SQS

**AWS Services**

- RDS PostgreSQL - stores coding questions and results
- S3 Bucket - stores submitted code files
- SQS Request Queue - sends code execution requests to worker
- SQS Response Queue - receives execution results from worker

## Workflow

1. Candidate opens the frontend application
2. Frontend requests coding questions from Backend (Questions Controller)
3. Backend retrieves questions from RDS PostgreSQL database
4. Candidate writes code and clicks Submit button
5. Frontend sends code to Backend (Submission Controller)
6. Backend stores code file in S3 bucket
7. Backend sends execution request to SQS Request Queue
8. Worker server picks up request from SQS Request Queue
9. Worker downloads code file from S3 bucket
10. Worker executes code in appropriate Docker container (Python/Java)
11. Worker sends execution results to SQS Response Queue
12. Backend (Results Controller) retrieves results from SQS Response Queue
13. Backend stores results in RDS PostgreSQL database
14. Frontend displays execution results to candidate## Project Structure

```
coding-platform/
├── frontend/                    # React application
│   ├── src/                     # React source code
│   ├── public/                  # Static files
│   ├── package.json            # Node.js dependencies
│   └── Dockerfile              # Frontend container
│
├── backend/                     # Spring Boot backend
│   ├── src/main/java/          # Java source code
│   ├── src/main/resources/     # Configuration files
│   ├── pom.xml                 # Maven dependencies
│   └── Dockerfile              # Backend container
│
├── worker/                      # Spring Boot worker
│   ├── src/main/java/          # Java source code
│   ├── src/main/resources/     # Configuration files
│   ├── pom.xml                 # Maven dependencies
│   └── Dockerfile              # Worker container
│
├── infrastructure/              # Infrastructure setup
│   ├── terraform/              # AWS infrastructure
│   └── ansible/                # Server configuration
│
├── docker-compose.yml          # Local development
└── README.md                   # This file
```

## Technology Stack

**Frontend**

- React with TypeScript
- Next.js framework
- Running on port 3000

**Backend**

- Java 17
- Spring Boot
- PostgreSQL database
- AWS SQS and S3 integration
- Running on port 8080

**Worker**

- Java 17
- Spring Boot
- Docker containers for code execution
- AWS SQS and S3 integration
- Running on port 8081

**Infrastructure**

- AWS EC2 instances
- AWS RDS PostgreSQL
- AWS S3 bucket
- AWS SQS queues
- Docker containers

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- Maven 3.8 or higher
- Docker and Docker Compose
- AWS account with access to EC2, RDS, S3, and SQS

### Environment Configuration

Before running the application, you need to configure environment variables for AWS services and database connections.

#### 1. Backend Configuration

Create environment variables for the backend service in `backend/src/main/resources/application.properties`:

```properties
# These variables need to be set in your environment
DB_URL=jdbc:postgresql://your-rds-endpoint:5432/your-database-name
DB_USERNAME=your-database-username
DB_PASSWORD=your-database-password
QUEUE_URL=https://sqs.your-region.amazonaws.com/your-account-id/your-request-queue
RESULT_URL=https://sqs.your-region.amazonaws.com/your-account-id/your-response-queue
BUCKET_NAME=your-s3-bucket-name
```

#### 2. Worker Configuration

Set the same environment variables for the worker service in `worker/src/main/resources/application.properties`:

```properties
# These variables need to be set in your environment
QUEUE_URL=https://sqs.your-region.amazonaws.com/your-account-id/your-request-queue
RESULT_URL=https://sqs.your-region.amazonaws.com/your-account-id/your-response-queue
BUCKET_NAME=your-s3-bucket-name
```

#### 3. AWS Credentials

Set AWS credentials in your environment:

```bash
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
export AWS_REGION=your-aws-region
```

#### 4. Infrastructure Configuration

Update the Terraform configuration in `infrastructure/terraform/main.tf` with your actual values:

- Replace "YOUR_ACCOUNT_ID" with your AWS account ID
- Replace "YOUR_RDS_ENDPOINT" with your RDS endpoint
- Replace "YOUR_BUCKET_NAME" with your S3 bucket name
- Replace "YOUR_QUEUE_NAMES" with your SQS queue names

Update the Ansible configuration in `infrastructure/ansible/secrets.auto.yml` with your actual values:

- AWS credentials and region
- Database connection details
- SQS queue URLs
- S3 bucket name

### Local Development Setup

#### 1. Start Database (PostgreSQL)

You can use the provided docker-compose.yml for local development:

```bash
# Start only PostgreSQL for local development
docker-compose up -d postgres
```

Or set up your own PostgreSQL database and update the connection details.

#### 2. Start Backend Service

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will start on http://localhost:8080

#### 3. Start Worker Service

```bash
cd worker
mvn clean install
mvn spring-boot:run
```

The worker will start on http://localhost:8081

#### 4. Start Frontend Service

```bash
cd frontend
npm install
npm start
```

The frontend will start on http://localhost:3000

### Production Deployment

#### 1. Infrastructure Setup

Deploy AWS infrastructure using Terraform:

```bash
cd infrastructure/terraform
terraform init
terraform plan
terraform apply
```

This will create:

- EC2 instances for frontend, backend, and worker
- RDS PostgreSQL database
- S3 bucket for code storage
- SQS queues for request/response handling

#### 2. Server Configuration

Configure the servers using Ansible:

```bash
cd infrastructure/ansible
ansible-playbook -i hosts.ini playbook.yml
```

This will:

- Install Docker on all servers
- Deploy application containers
- Configure nginx and networking
- Set up monitoring and logging

#### 3. Application Deployment

Deploy using Docker on each server:

```bash
# On each server, pull and run the appropriate container
docker run -d -p 3000:3000 your-frontend-image
docker run -d -p 8080:8080 your-backend-image
docker run -d -p 8081:8081 your-worker-image
```

### Accessing the Application

After deployment:

- Frontend: http://your-frontend-server-ip:3000
- Backend API: http://your-backend-server-ip:8080
- Worker API: http://your-worker-server-ip:8081

### Important Notes

1. **Security**: All placeholder values in configuration files must be replaced with actual values
2. **AWS Setup**: Ensure your AWS account has sufficient permissions for EC2, RDS, S3, and SQS
3. **Database**: Make sure PostgreSQL database is accessible from backend server
4. **Networking**: Ensure security groups allow communication between services
5. **Monitoring**: Check application logs for any configuration errors

## Contributing

This is a personal project, but contributions are welcome if you're interested in helping improve it.

### How to Contribute

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Make your changes and test them
4. Commit your changes: `git commit -m 'Add your feature'`
5. Push to the branch: `git push origin feature/your-feature`
6. Open a Pull Request

### Code Guidelines

- Follow existing code style
- Test your changes before submitting
- Update documentation if needed

## Contact

**Developer**: Teja Naidu Koppineni

- GitHub: [@tkoppine](https://github.com/tkoppine)
- Email: tkoppine@asu.edu

For questions or issues, please create an issue on GitHub.
