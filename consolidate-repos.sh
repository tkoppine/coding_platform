#!/bin/bash

# Coding Platform Repository Consolidation Script
# This script consolidates all your coding platform repositories into a single monorepo

set -e

GITHUB_USERNAME="tkoppine"
NEW_REPO="coding-platform"

echo "🚀 Starting repository consolidation for $GITHUB_USERNAME..."

# Check if we're in the right directory
if [ ! -d ".git" ]; then
    echo "❌ This script must be run from inside the main coding-platform git repository"
    echo "Please run: git clone https://github.com/$GITHUB_USERNAME/$NEW_REPO.git && cd $NEW_REPO"
    exit 1
fi

# Repository mappings
declare -A REPOS=(
    ["backend"]="coding_platform_backend"
    ["frontend"]="Frontend"
    ["worker"]="coding_platform_worker"
    ["infrastructure/terraform"]="coding_platform_terraform"
    ["infrastructure/ansible"]="coding_platform_ansible"
)

echo "📦 Creating directory structure..."
mkdir -p backend frontend worker infrastructure/terraform infrastructure/ansible

# Function to migrate a repository
migrate_repo() {
    local target_dir="$1"
    local source_repo="$2"
    local temp_dir="temp_$source_repo"
    
    echo "🔄 Migrating $source_repo to $target_dir..."
    
    # Clone the source repository
    git clone "https://github.com/$GITHUB_USERNAME/$source_repo.git" "$temp_dir"
    
    # Copy files (excluding .git directory)
    rsync -av --exclude='.git' "$temp_dir/" "$target_dir/"
    
    # Clean up
    rm -rf "$temp_dir"
    
    echo "✅ Migrated $source_repo successfully"
}

# Migrate each repository
for target_dir in "${!REPOS[@]}"; do
    source_repo="${REPOS[$target_dir]}"
    migrate_repo "$target_dir" "$source_repo"
done

echo "📝 Creating project documentation..."

# Create main README
cat > README.md << 'EOF'
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
EOF

# Create docker-compose.yml for local development
cat > docker-compose.yml << 'EOF'
version: '3.8'

services:
  # Database
  postgres:
    image: postgres:15-alpine
    container_name: coding-platform-db
    environment:
      POSTGRES_DB: coding_platform
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - coding-platform-network

  # Redis for caching
  redis:
    image: redis:7-alpine
    container_name: coding-platform-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - coding-platform-network

  # Backend Service
  backend:
    build: 
      context: ./backend
      dockerfile: Dockerfile
    container_name: coding-platform-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/coding_platform
      SPRING_DATASOURCE_USERNAME: admin
      SPRING_DATASOURCE_PASSWORD: password
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
      WORKER_SERVICE_URL: http://worker:8081
    depends_on:
      - postgres
      - redis
    networks:
      - coding-platform-network
    restart: unless-stopped

  # Worker Service
  worker:
    build:
      context: ./worker
      dockerfile: Dockerfile
    container_name: coding-platform-worker
    ports:
      - "8081:8081"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
    depends_on:
      - redis
    networks:
      - coding-platform-network
    restart: unless-stopped

  # Frontend Service
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: coding-platform-frontend
    ports:
      - "3000:80"
    environment:
      REACT_APP_API_URL: http://localhost:8080
    depends_on:
      - backend
    networks:
      - coding-platform-network
    restart: unless-stopped

volumes:
  postgres_data:
  redis_data:

networks:
  coding-platform-network:
    driver: bridge
EOF

# Create .gitignore
cat > .gitignore << 'EOF'
# Dependencies
node_modules/
*/node_modules/

# Build outputs
build/
dist/
target/
*.jar
*.war

# Environment files
.env
.env.local
.env.*.local

# IDE files
.vscode/
.idea/
*.swp
*.swo

# OS generated files
.DS_Store
.DS_Store?
._*
.Spotlight-V100
.Trashes
ehthumbs.db
Thumbs.db

# Logs
logs/
*.log

# Terraform
*.tfstate
*.tfstate.*
.terraform/
.terraform.lock.hcl

# Ansible
*.retry

# Docker
.dockerignore
EOF

# Add all files to git
echo "📥 Adding files to git..."
git add .

# Create initial commit
echo "💾 Creating initial commit..."
git commit -m "Initial monorepo setup

- Consolidated coding_platform_backend into backend/
- Consolidated Frontend into frontend/ 
- Consolidated coding_platform_worker into worker/
- Consolidated coding_platform_terraform into infrastructure/terraform/
- Consolidated coding_platform_ansible into infrastructure/ansible/
- Added docker-compose.yml for local development
- Added comprehensive README.md
- Added project-wide .gitignore"

echo ""
echo "🎉 Repository consolidation completed successfully!"
echo ""
echo "📋 Next Steps:"
echo "1. Push to GitHub: git push origin main"
echo "2. Test local setup: docker-compose up -d"
echo "3. Archive old repositories (keep as backup)"
echo "4. Update any external references to point to new repo"
echo ""
echo "🌐 Your new monorepo structure:"
echo "├── backend/          (from coding_platform_backend)"
echo "├── frontend/         (from Frontend)"  
echo "├── worker/           (from coding_platform_worker)"
echo "└── infrastructure/"
echo "    ├── terraform/    (from coding_platform_terraform)"
echo "    └── ansible/      (from coding_platform_ansible)"
EOF