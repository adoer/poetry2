#!/bin/bash
set -e

DOMAIN="${DOMAIN:-ai.xerduo.com}"
EMAIL="${CERTBOT_EMAIL}"
DATA_PATH="./certbot"

if [ -z "$EMAIL" ]; then
    echo "Error: CERTBOT_EMAIL is not set. Please set it in .env file."
    exit 1
fi

if [ -d "$DATA_PATH/live/$DOMAIN" ]; then
    echo "SSL certificates already exist for $DOMAIN."
    exit 0
fi

echo "Creating dummy certificate for $DOMAIN..."
mkdir -p "$DATA_PATH/live/$DOMAIN"
openssl req -x509 -nodes -newkey rsa:4096 -days 1 \
    -keyout "$DATA_PATH/live/$DOMAIN/privkey.pem" \
    -out "$DATA_PATH/live/$DOMAIN/fullchain.pem" \
    -subj "/CN=localhost"

echo "Starting nginx..."
docker compose up -d nginx

echo "Removing dummy certificate..."
rm -rf "$DATA_PATH/live/$DOMAIN"

echo "Obtaining Let's Encrypt certificate for $DOMAIN..."
docker compose run --rm certbot certonly --webroot -w /var/www/certbot \
    --email "$EMAIL" \
    -d "$DOMAIN" \
    --rsa-key-size 4096 \
    --agree-tos \
    --force-renewal

echo "Reloading nginx..."
docker compose exec nginx nginx -s reload

echo "Done! SSL certificate obtained successfully for $DOMAIN."
