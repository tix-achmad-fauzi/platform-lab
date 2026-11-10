#!/usr/bin/env bash
set -euo pipefail
fail=0
for t in orb docker kubectl helm kustomize k9s aws jq terraform kind; do
  if command -v "$t" >/dev/null 2>&1; then
    printf '%-10s OK %s\n' "$t" "$(command -v "$t")"
  else
    printf '%-10s MISSING\n' "$t"; fail=1
  fi
done
docker info >/dev/null 2>&1 || { echo "docker daemon (OrbStack) tidak jalan"; fail=1; }
[ "$(docker context show)" = "orbstack" ] || { echo "docker context bukan orbstack"; fail=1; }
kubectl version --client
exit $fail