#!/usr/bin/env bash

PROJECT_URL="" # SSL 갱신이 필요한 도메인 URL 입력 필요
SSL_ROOT="/etc/letsencrypt/live/$PROJECT_URL"
HOME_ROOT="/home/ec2-user"

sudo certbot certificates
sudo certbot renew --dry-run
sudo certbot renew
sudo openssl pkcs12 -export -in $SSL_ROOT/fullchain.pem -inkey $SSL_ROOT/privkey.pem -out $SSL_ROOT/keystore.p12 -name recipestorage -CAfile $SSL_ROOT/chain.pem -caname root
sudo mv $SSL_ROOT/keystore.p12 $HOME_ROOT
sudo chmod 777 $HOME_ROOT/keystore.p12
sudo openssl base64 -in $HOME_ROOT/keystore.p12 -out $HOME_ROOT/keystore.txt
