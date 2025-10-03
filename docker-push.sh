#!/usr/bin/env bash
set -euo pipefail

REGISTRY="${REGISTRY:?REGISTRY not set}"
IMAGE_NAME="${IMAGE_NAME:-sk085-springboot-practice}"
IMAGE_TAG="${IMAGE_TAG:-latest}"
REGISTRY_USER="${REGISTRY_USER:-}"
REGISTRY_PASSWORD="${REGISTRY_PASSWORD:-}"

FULL_IMAGE="${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"

if [[ -n "${REGISTRY_USER}" && -n "${REGISTRY_PASSWORD}" ]]; then
  echo "Logging in to ${REGISTRY}"
  echo "${REGISTRY_PASSWORD}" | docker login "${REGISTRY}" --username "${REGISTRY_USER}" --password-stdin
else
  echo "Skipping registry login (REGISTRY_USER or REGISTRY_PASSWORD not provided)"
fi

echo "Pushing ${FULL_IMAGE}"
docker push "${FULL_IMAGE}"
