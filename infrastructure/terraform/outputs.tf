############################
# EC2 Public IPs
############################

output "frontend_ip" {
  description = "Public IP of the frontend EC2 instance"
  value       = aws_instance.frontend.public_ip
}

output "backend_ip" {
  description = "Public IP of the backend EC2 instance"
  value       = aws_instance.backend.public_ip
}

output "worker_ip" {
  description = "Public IP of the worker EC2 instance"
  value       = aws_instance.worker.public_ip
}

############################
# RDS
############################

output "db_endpoint" {
  description = "RDS PostgreSQL endpoint"
  value       = aws_db_instance.postgres.endpoint
}

output "db_port" {
  description = "RDS PostgreSQL port"
  value       = aws_db_instance.postgres.port
}


############################
# Ansible Inventory (console output)
############################

output "ansible_inventory" {
  description = "Ansible inventory for all servers"
  value       = local_file.ansible_inventory.content
}

############################
# Local file: ansible/hosts
############################

resource "local_file" "ansible_inventory" {
  content = <<EOT
[frontend]
frontend-server ansible_host=${aws_instance.frontend.public_ip} ansible_user=ec2-user ansible_ssh_private_key_file=/Users/tejanaidu/ansible/keys/ansible-key.pem

[backend]
backend-server ansible_host=${aws_instance.backend.public_ip} ansible_user=ec2-user ansible_ssh_private_key_file=/Users/tejanaidu/ansible/keys/ansible-key.pem

[worker]
worker-server ansible_host=${aws_instance.worker.public_ip} ansible_user=ec2-user ansible_ssh_private_key_file=/Users/tejanaidu/ansible/keys/ansible-key.pem
EOT

  filename = "/Users/tejanaidu/ansible/hosts.ini"
}

resource "local_file" "ansible_secrets" {
  content = <<EOT
sqs_request_url: "${aws_sqs_queue.request.id}"
sqs_response_url: "${aws_sqs_queue.response.id}"
s3_bucket_name: "${aws_s3_bucket.submission.bucket}"
db_endpoint: "${aws_db_instance.postgres.endpoint}"

BACKEND_IP: "${aws_instance.backend.public_ip}"
EOT

  filename = "/Users/tejanaidu/ansible/secrets.auto.yml"
}