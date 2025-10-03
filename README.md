# Spring Boot Practice Pipeline

This repository (`springboot_practice`) demonstrates a Jenkins + ArgoCD GitOps workflow for a Swagger-enabled Spring Boot application.

## End-to-end Flow

1. Developers push code changes to the `main` branch of `https://github.com/bongbubble/springboot_practice_pipeline.git`.
2. Jenkins builds the project, pushes the container image to `amdp-registry.skala-ai.com/skala25a`, and updates the Kubernetes manifests with the new image tag.
3. ArgoCD (or manual `kubectl apply`) syncs the manifests into the `skala-practice` namespace.

## Repository Layout

- `src/` – Spring Boot REST API with Swagger UI enabled (via Springdoc)
- `Dockerfile`, `docker-build.sh`, `docker-push.sh` – container build & push artefacts (image name defaults to `springboot-practice`)
- `k8s/` – Kustomize-based Kubernetes manifests (`k8s/base`, `k8s/overlays/dev`)
- `scripts/update-k8s-image.sh` – helper script to bump the image tag in Kustomize
- `Jenkinsfile` – declarative Jenkins pipeline definition tailored for the practice environment
- `argocd/application.yaml` – ArgoCD `Application` resource pointing at the dev overlay

## Prerequisites

- Jenkins agent runs on Linux with Docker CLI and kubectl available.
- Jenkins credentials:
  - `skala-github-id` (GitHub PAT) for repository checkout and pushes.
  - `skala-image-registry-id` (Docker registry credentials) for Harbor login.
- Confirm the registry/repository values in `Jenkinsfile`, `k8s/base/kustomization.yaml`, and `argocd/application.yaml` match your environment.

## Local Development

```bash
./mvnw spring-boot:run
# Swagger UI is available at http://localhost:8080/swagger/swagger-ui/index.html
```

## Local Container Build

```bash
./docker-build.sh
REGISTRY=amdp-registry.skala-ai.com IMAGE_NAME=skala25a/springboot-practice IMAGE_TAG=local ./docker-build.sh
```

## Kubernetes Manifests (dry run)

```bash
kubectl apply -k k8s/overlays/dev --dry-run=client
```

## Register with ArgoCD

```bash
kubectl apply -f argocd/application.yaml
```

Capture Jenkins pipeline logs and ArgoCD sync status for submission evidence as required by the assignment brief.
