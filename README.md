# 나만의 파이프라인 만들기

## 📌 프로젝트 개요
Jenkins + ArgoCD 기반으로 **Spring Boot 사용자 관리 애플리케이션**을 자동 빌드하고 Kubernetes 환경에 배포하는 파이프라인 프로젝트입니다.

## ⚙️ 파이프라인 구성
1. **Jenkins**: GitHub 커밋 감지 → Docker 이미지 빌드 → Harbor(`amdp-registry.skala-ai.com/skala25a`) 푸시  
2. **Jenkins 스크립트**: `k8s/overlays/dev` 매니페스트의 `image` 태그 자동 갱신  
3. **ArgoCD**: 변경된 Git 리포지토리를 감지하여 `skala-practice` 네임스페이스에 자동 동기화 및 배포

## 📂 디렉토리 구조
- `src/main/java` : Spring Boot 비즈니스 로직  
- `src/main/resources/static` : 빌드된 프론트엔드 정적 파일  
- `frontend/` : Vue 3 기반 프론트엔드 (Vite, `npm run build` 사용)  
- `Dockerfile`, `docker-build.sh`, `docker-push.sh` : 컨테이너 빌드 및 이미지 푸시 스크립트  
- `k8s/base`, `k8s/overlays/dev` : Kustomize 베이스 및 환경별 오버레이  
- `argocd/application.yaml` : ArgoCD 애플리케이션 정의  
- `Jenkinsfile`, `Jenkinsfile-CI.docker` : Jenkins CI/CD 파이프라인 스크립트  

## 🚀 배포 결과
- **ArgoCD Application**: `sk085-springboot-practice`  
- **상태(Status)**: ✅ Synced / Healthy  
- **배포 이미지**: `amdp-registry.skala-ai.com/skala25a/sk085-springboot-practice:1.0.0-8453d9bfebe3`  
- **서비스 화면**: `app_ui_result.png` 참고  