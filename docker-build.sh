#!/usr/bin/env bash
set -euo pipefail

REGISTRY="${REGISTRY:-}"
IMAGE_NAME="${IMAGE_NAME:-springboot-practice}"
IMAGE_TAG="${IMAGE_TAG:-latest}"
PLATFORM="${PLATFORM:-linux/amd64}"
DOCKER_BUILD_ARGS="${DOCKER_BUILD_ARGS:-}"

if [[ -n "${REGISTRY}" ]]; then
  FULL_IMAGE="${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
else
  FULL_IMAGE="${IMAGE_NAME}:${IMAGE_TAG}"
fi

echo "Building ${FULL_IMAGE} for ${PLATFORM}"

if [[ ! -f target/springboot-practice-0.0.1-SNAPSHOT.jar ]]; then
  echo "Packaging application..."
  ./mvnw -q -DskipTests package
fi

read -r -a EXTRA_ARGS <<< "${DOCKER_BUILD_ARGS}"

set -x
docker build \
  --platform "${PLATFORM}" \
  --tag "${FULL_IMAGE}" \
  "${EXTRA_ARGS[@]}" \
  .
set +x

echo "Docker image built: ${FULL_IMAGE}"
