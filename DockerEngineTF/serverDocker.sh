#!/usr/bin/bash

cd /home/username   # change to your user VM directory
exec 1>log.out 2>&1
echo in directory $PWD
echo logged in as  
whoami

echo "installing MariaDB..."
# sudo yum install mysql -y
sudo dnf install mariadb-server -y
sudo systemctl start mariadb
sudo systemctl status mariadb
sudo systemctl enable mariadb

# DB_ROOT_PASSWORD must be supplied through the environment.
if [ -z "$DB_ROOT_PASSWORD" ]; then
    echo "ERROR: DB_ROOT_PASSWORD is not set."
    exit 1
fi

echo "creating mysql_secure_installation.txt..."
touch mysql_secure_installation.txt
cat << `EOF` >> mysql_secure_installation.txt

n
Y
$DB_ROOT_PASSWORD
$DB_ROOT_PASSWORD
Y
Y
Y
Y
Y
`EOF`

echo "running mysql_secure_installation..."
sudo mysql_secure_installation < mysql_secure_installation.txt

# Remove the temporary file containing the password
rm -f mysql_secure_installation.txt

sudo dnf config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo dnf install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin -y
sudo systemctl start docker
# to automatically start docker on startup 
# https://docs.docker.com/engine/install/linux-postinstall/
sudo systemctl enable docker.service
sudo systemctl enable containerd.service

