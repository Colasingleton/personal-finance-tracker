#!/bin/bash
echo "Deploying expense-tracker to Cloud Run..."

gcloud run deploy expense-tracker \
  --source . \
  --region us-east1 \
  --env-vars-file env.yaml \
  --add-cloudsql-instances boreal-coyote-463122-r3:us-east1:expense-tracker-db \
  --memory=1Gi \
  --timeout=900 \
  --allow-unauthenticated

echo "Deployment complete!"
