#!/bin/bash

# Create certs directory if it doesn't exist
mkdir -p certs

# Generate private key
openssl genrsa -out ./certs/cert.key 2048

# Generate self-signed certificate
openssl req -new -x509 -key ./certs/cert.key -out ./certs/cert.crt -days 365 -subj "/CN=localhost"
