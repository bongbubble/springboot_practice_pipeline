pipeline {
  agent any

  environment {
    // === 사용자 수정 영역 ===
    GIT_URL              = 'https://github.com/bongbubble/springboot_practice_pipeline.git'
    GIT_BRANCH           = 'main'
    GIT_CREDENTIAL_ID    = 'skala-github-id'

    IMAGE_NAME           = 'sk085-springboot-practice'
    IMAGE_TAG_PREFIX     = '1.0.0'
    IMAGE_REGISTRY_URL   = 'amdp-registry.skala-ai.com'
    IMAGE_REGISTRY_PROJECT = 'skala25a'

    DOCKER_CREDENTIAL_ID = 'skala-image-registry-id'
    K8S_NAMESPACE        = 'skala-practice'
    KUSTOMIZATION_FILE   = 'k8s/base/kustomization.yaml'
    // =======================
  }

  options {
    disableConcurrentBuilds()
    timestamps()
  }

  stages {
    stage('Clone Repository') {
      steps {
        echo 'Clone Repository'
        git branch: "${GIT_BRANCH}", url: "${GIT_URL}", credentialsId: "${GIT_CREDENTIAL_ID}"
        sh 'ls -al'
      }
    }

    stage('Build with Maven') {
      steps {
        echo 'Build with Maven'
        sh './mvnw clean package'
      }
    }

    stage('Compute Image Meta') {
      steps {
        script {
          def hashcode = sh(script: "date +%s%N | sha256sum | cut -c1-12", returnStdout: true).trim()
          env.IMAGE_REGISTRY = "${env.IMAGE_REGISTRY_URL}/${env.IMAGE_REGISTRY_PROJECT}"
          env.FINAL_IMAGE_TAG = "${env.IMAGE_TAG_PREFIX}-${hashcode}"
          env.IMAGE_REF = "${env.IMAGE_REGISTRY}/${env.IMAGE_NAME}:${env.FINAL_IMAGE_TAG}"

          echo "IMAGE_REF: ${env.IMAGE_REF}"
        }
      }
    }

    stage('Image Build & Push (docker)') {
      steps {
        script {
          docker.withRegistry("https://${env.IMAGE_REGISTRY_URL}", "${DOCKER_CREDENTIAL_ID}") {
            def appImage = docker.build("${env.IMAGE_REF}", "--platform=linux/amd64 .")
            appImage.push()
          }
        }
      }
    }

    stage('Update Manifests') {
      steps {
        sh '''
          set -eux
          IMAGE_NAME_FULL="$IMAGE_REGISTRY/$IMAGE_NAME"
          IMAGE_TAG="$FINAL_IMAGE_TAG"

          IMAGE_NAME="$IMAGE_NAME_FULL" IMAGE_TAG="$IMAGE_TAG" ./scripts/update-k8s-image.sh "$KUSTOMIZATION_FILE"

          git add "$KUSTOMIZATION_FILE"
        '''
      }
    }

    stage('Commit & Push Manifests') {
      steps {
        script {
          def pending = sh(script: 'git status --porcelain', returnStdout: true).trim()
          if (pending) {
            withCredentials([usernamePassword(credentialsId: env.GIT_CREDENTIAL_ID, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN')]) {
              sh '''
                set -eux
                git config user.email "jenkins@local"
                git config user.name "jenkins"
                git commit -m "chore: update image tag to ${FINAL_IMAGE_TAG}" || true
                git config credential.helper store
                printf "https://${GIT_USER}:${GIT_TOKEN}@github.com\n" > ~/.git-credentials
                git pull --rebase origin ${GIT_BRANCH}
                git push origin HEAD:${GIT_BRANCH}
                rm -f ~/.git-credentials
              '''
            }
          } else {
            echo 'No manifest changes detected.'
          }
        }
      }
    }
  }
}
