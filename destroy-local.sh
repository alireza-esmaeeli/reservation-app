#!/usr/bin/env bash
set -euo pipefail

echo "Destroying"
docker compose -f deployments/docker-compose.yml down -v