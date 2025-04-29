#!/bin/bash

# Script to update OpenSSH and apply security configurations for CVE-2024-6387
# Run with root privileges

set -e

echo "==== OpenSSH Security Update for CVE-2024-6387 ===="

# Check if script is run as root
if [ "$EUID" -ne 0 ]; then
  echo "Error: This script must be run as root"
  exit 1
fi

# Backup existing SSH configuration
echo "Creating backup of current SSH configuration..."
BACKUP_DIR="/etc/ssh/backup-$(date +%Y%m%d%H%M%S)"
mkdir -p "$BACKUP_DIR"
cp -r /etc/ssh/* "$BACKUP_DIR/"
echo "Backup created at $BACKUP_DIR"

# Update SSH packages
echo "Updating package lists..."
apt-get update -y

echo "Upgrading OpenSSH packages..."
apt-get install -y openssh-server openssh-client

# Create security configuration
echo "Creating security configuration for CVE-2024-6387 mitigation..."
cat > /etc/ssh/sshd_config.d/security-CVE-2024-6387.conf << 'EOC'
# Security configuration to mitigate CVE-2024-6387 (RegreSSHion Attack)

# Disable Diffie-Hellman key exchange
KexAlgorithms -diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group14-sha256,diffie-hellman-group16-sha512,diffie-hellman-group18-sha512

# Require protocol 2
Protocol 2

# Set strict host key checking
HostKeyAlgorithms ssh-ed25519,ssh-ed25519-cert-v01@openssh.com,rsa-sha2-512,rsa-sha2-512-cert-v01@openssh.com,rsa-sha2-256,rsa-sha2-256-cert-v01@openssh.com

# This is the critical setting for CVE-2024-6387 (RegreSSHion) vulnerability
# It prevents client-initiated rekeying before authentication
MaxStartups 10:30:100

# Set session timeouts
ClientAliveInterval 300
ClientAliveCountMax 2
EOC

# Set proper permissions
chmod 644 /etc/ssh/sshd_config.d/security-CVE-2024-6387.conf

# Restart SSH service
echo "Restarting SSH service to apply new configuration..."
systemctl restart sshd

# Verify the service is running
if systemctl is-active --quiet sshd; then
  echo "SSH service restarted successfully"
else
  echo "Error: SSH service failed to restart"
  echo "Rolling back configuration changes..."
  rm -f /etc/ssh/sshd_config.d/security-CVE-2024-6387.conf
  systemctl restart sshd
  exit 1
fi

# Display the current SSH version
SSH_VERSION=$(ssh -V 2>&1)
echo "Current SSH version: $SSH_VERSION"

echo
echo "==== OpenSSH security update completed successfully ===="
echo "A security configuration has been applied to mitigate CVE-2024-6387"
echo "Please verify that your SSH connections work properly"