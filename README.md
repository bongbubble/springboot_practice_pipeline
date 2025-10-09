# 나만의 파이프라인 만들기

## 📌 프로젝트 개요
Jenkins + ArgoCD 기반으로 Spring Boot 사용자 관리 애플리케이션을 자동 빌드하고 Kubernetes에 배포하는 파이프라인입니다.

## ⚙️ 파이프라인 구성
1. **Jenkins**: GitHub 커밋 감지 → Docker 이미지 빌드 → Harbor(`amdp-registry.skala-ai.com/skala25a`) 푸시
2. **Jenkins 스크립트**: `k8s/overlays/dev` 매니페스트의 이미지 태그 갱신
3. **ArgoCD**: 변경된 Git 리포지토리를 동기화하여 `skala-practice` 네임스페이스에 자동 배포

## 📂 디렉토리 구조
- `src/` Spring Boot 애플리케이션
- `Dockerfile`, `docker-build.sh`, `docker-push.sh`
- `k8s/` Kustomize base & dev overlay
- `Jenkinsfile`
- `argocd/application.yaml`
- `screenshots/` 제출용 캡처 (예: `app_ui_result.png`)

## 배포 결과
- ArgoCD Application: **sk085-springboot-practice**
- Sync Status: ✅ Synced (Healthy)
- Latest Commit: `4aa17d7`
- Image: `amdp-registry.skala-ai.com/skala25a/sk085-springboot-practice:1.0.0-8453d9bfebe3`
- 서비스 화면: `screenshots/app_ui_result.png`

## 🔗 Git Repository
https://github.com/bongbubble/springboot_practice_pipeline.git
