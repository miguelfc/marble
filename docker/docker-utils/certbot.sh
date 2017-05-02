#!/bin/bash

CERT_DIR=/home/marble/certs
LIVE_DIR=$CERT_DIR/live/your.domain.com

mkdir $CERT_DIR && echo "$CERT_DIR recreated"

docker run --rm -it --net docker_default \
       -v $CERT_DIR:/etc/letsencrypt \
       certbot/certbot \
       certonly -d your.domain.com --agree-tos --email your@email.com  --manual --preferred-challenges dns 

openssl pkcs12 -export -in $LIVE_DIR/fullchain.pem \
                 -inkey $LIVE_DIR/privkey.pem \
                 -out ../core-config/keystore.p12 \
                 -name tomcat \
                 -CAfile $LIVE_DIR/chain.pem \
                 -caname root

