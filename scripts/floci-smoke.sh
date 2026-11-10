#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/../local/floci.env"
case "$AWS_ENDPOINT_URL" in http://*.local) ;; *) echo "BUKAN Floci, stop."; exit 1;; esac
aws sts get-caller-identity
aws s3 mb s3://platform-lab-smoke 2>/dev/null || true
echo "hello floci $(date -u +%FT%TZ)" > /tmp/smoke.txt
aws s3 cp /tmp/smoke.txt s3://platform-lab-smoke/smoke.txt
aws s3 cp s3://platform-lab-smoke/smoke.txt -
Q=$(aws sqs create-queue --queue-name smoke-q --query QueueUrl --output text)
aws sqs send-message --queue-url "$Q" --message-body '{"ping":1}' >/dev/null
aws sqs receive-message --queue-url "$Q" --query 'Messages[0].Body' --output text
echo "SMOKE OK"