# DEPLOY

```
docker network create main-network
```
 
```
cd backend/mosvodokanal && docker compose build
```

```
cd devops/ && docker compose -f docker-compose.yml up -d && docker compose -f docker-compose-services.yaml up -d
```
