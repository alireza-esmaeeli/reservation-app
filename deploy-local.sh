#!/usr/bin/env bash
set -euo pipefail

echo "Building container images"
mvn clean package jib:dockerBuild
echo "Deploying"
docker compose -f deployments/docker-compose.yml up -d