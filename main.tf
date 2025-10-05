provider "aws" {
  region = "us-east-2"
}

############################
# 1. SSH Key Pair
############################

resource "tls_private_key" "main" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "aws_key_pair" "main" {
  key_name   = "ansible-key"
  public_key = tls_private_key.main.public_key_openssh
}

resource "local_file" "ansible_ssh_key" {
  content          = tls_private_key.main.private_key_pem
  filename         = "/Users/tejanaidu/ansible/keys/ansible-key.pem"
  file_permission  = "0400"
}

############################
# 2. Networking
############################

data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}


############################
# 3. Security Groups
############################

resource "aws_security_group" "frontend_sg" {
  vpc_id = data.aws_vpc.default.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Backend SG
resource "aws_security_group" "backend_sg" {
  vpc_id = data.aws_vpc.default.id

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.frontend_sg.id]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Worker SG
resource "aws_security_group" "worker_sg" {
  vpc_id = data.aws_vpc.default.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# DB SG (already exists → import later)
resource "aws_security_group" "db_sg" {
  vpc_id = data.aws_vpc.default.id

  # Allow Postgres access only from backend and worker
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [
      aws_security_group.backend_sg.id,
      aws_security_group.worker_sg.id
    ]
  }

  # Allow outbound traffic (for DB to send responses)
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

############################
# 4. EC2 Instances
############################

data "aws_ami" "amazon_linux_2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }

  filter {
    name   = "architecture"
    values = ["x86_64"]
  }

  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

}

# Choose the first default subnet
locals {
  default_subnet_id = data.aws_subnets.default.ids[0]
}

resource "aws_instance" "frontend" {
  ami           = data.aws_ami.amazon_linux_2023.id
  instance_type = var.ec2_type
  subnet_id     = local.default_subnet_id
  vpc_security_group_ids = [aws_security_group.frontend_sg.id]
  key_name      = aws_key_pair.main.key_name

  root_block_device {
    volume_size = 30
    volume_type = "gp3"
  }

  tags = { Name = "frontend-app" }
}

resource "aws_instance" "backend" {
  ami           = data.aws_ami.amazon_linux_2023.id
  instance_type = var.ec2_type
  subnet_id     = local.default_subnet_id
  vpc_security_group_ids = [aws_security_group.backend_sg.id]
  key_name      = aws_key_pair.main.key_name

  root_block_device {
    volume_size = 30
    volume_type = "gp3"
  }

  tags = { Name = "backend-app" }
}

resource "aws_instance" "worker" {
  ami           = data.aws_ami.amazon_linux_2023.id
  instance_type = var.ec2_type
  subnet_id     = local.default_subnet_id
  vpc_security_group_ids = [aws_security_group.worker_sg.id]
  key_name      = aws_key_pair.main.key_name

  root_block_device {
    volume_size = 30
    volume_type = "gp3"
  }

  tags = { Name = "worker-app" }
}


############################
# 5. Existing Resources (imported later)
############################

resource "aws_s3_bucket" "submission" {
  bucket = "candidate-code-submission"
}

resource "aws_sqs_queue" "request" {
  name = "code-submission-request"
  max_message_size = 1048576
}

resource "aws_sqs_queue" "response" {
  name = "code-results-response"
  max_message_size = 1048576
}

resource "aws_db_instance" "postgres" {
  identifier             = "coding-questions-db"
  vpc_security_group_ids = [aws_security_group.db_sg.id]

  # values must match your real DB, otherwise Terraform will drift
  instance_class     = "db.t4g.micro"
  engine             = "postgres"
  allocated_storage  = 20

  lifecycle {
    prevent_destroy = true
    ignore_changes  = all
  }
}