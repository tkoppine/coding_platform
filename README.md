# Coding Platform

A comprehensive online coding platform that provides an integrated development environment for multiple programming languages with real-time collaboration, code execution, and project management capabilities.

## 🏗️ Architecture Overview

This is a microservices-based application consisting of:

- **Frontend**: React/TypeScript web application (`./frontend/`)
- **Backend**: Java Spring Boot REST API server (`./backend/`)
- **Worker**: Java-based code execution service (`./worker/`)
- **Infrastructure**: Terraform + Ansible for deployment (`./infrastructure/`)

## 📁 Project Structure

```
coding-platform/
├── frontend/                 # React TypeScript frontend application
├── backend/                  # Java Spring Boot backend service  
├── worker/                   # Java code execution worker service
├── infrastructure/           # Infrastructure as Code
│   ├── terraform/           # Infrastructure provisioning
│   └── ansible/             # Configuration management
├── docs/                     # Documentation
├── docker-compose.yml        # Local development setup
└── README.md                # This file
```

## 🚀 Quick Start

### Prerequisites
- **Node.js** (v18+)
- **Java** (v17+) 
- **Maven** (v3.8+)
- **Docker** & **Docker Compose**

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/tkoppine/coding-platform.git
   cd coding-platform
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Worker Service: http://localhost:8081

## 🔧 Services

### Frontend Service (Port: 3000)
- **Technology**: React 18 + TypeScript
- **Description**: Web-based IDE interface with real-time collaboration

### Backend Service (Port: 8080)  
- **Technology**: Java 17 + Spring Boot 3.x
- **Description**: Main API server handling user management and orchestration

### Worker Service (Port: 8081)
- **Technology**: Java 17 + Spring Boot 3.x  
- **Description**: Isolated code execution environment

## 🏗️ Infrastructure

### Terraform
- Cloud resource provisioning
- Networking and security setup
- Container orchestration

### Ansible
- Server configuration management
- Application deployment automation
- Environment setup

## 🚀 Deployment

### Development
```bash
docker-compose up -d
```

### Production
```bash
# Infrastructure provisioning
cd infrastructure/terraform
terraform init && terraform apply

# Application deployment  
cd ../ansible
ansible-playbook deploy.yml
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📞 Contact

**Teja Naidu Koppineni**
- GitHub: [@tkoppine](https://github.com/tkoppine)
- LinkedIn: [tejanaidukoppineni](https://www.linkedin.com/in/tejanaidukoppineni/)

## 📝 License

This project is licensed under the MIT License.
