#!/usr/bin/env bash
set -euo pipefail

KUSTOMIZATION_FILE="${1:-k8s/base/kustomization.yaml}"
IMAGE_NAME="${IMAGE_NAME:-amdp-registry.skala-ai.com/skala25a/springboot-practice}"
IMAGE_TAG="${IMAGE_TAG:-latest}"

if [[ ! -f "${KUSTOMIZATION_FILE}" ]]; then
  echo "kustomization file not found: ${KUSTOMIZATION_FILE}" >&2
  exit 1
fi

python3 - "$KUSTOMIZATION_FILE" "$IMAGE_NAME" "$IMAGE_TAG" <<'PY'
import sys
from pathlib import Path

path = Path(sys.argv[1])
image_name = sys.argv[2]
image_tag = sys.argv[3]

lines = path.read_text().splitlines()
updated_lines = []
current_image = None

for line in lines:
    stripped = line.strip()
    if stripped.startswith('- name:'):
        current_image = stripped.split(':', 1)[1].strip()
        updated_lines.append(line)
        continue
    if stripped.startswith('newTag:') and current_image == image_name:
        indent = line[:len(line) - len(stripped)]
        updated_lines.append(f"{indent}newTag: {image_tag}")
        current_image = None
        continue
    updated_lines.append(line)

if image_name not in '\n'.join(updated_lines):
    raise SystemExit(f"image {image_name} not found in {path}")

path.write_text('\n'.join(updated_lines) + '\n')
PY
