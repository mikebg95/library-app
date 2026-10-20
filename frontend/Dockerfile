# Build context is the repository root: the build needs backend/.../openapi.yaml to generate the types.

# Build: generate types from the contract, type-check and bundle
FROM node:22-alpine AS build
WORKDIR /app
COPY backend/src/main/resources/openapi.yaml backend/src/main/resources/openapi.yaml
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend .
RUN npm run build

# Run: nginx serves the static files and proxies /api to the backend
FROM nginx:1.29-alpine
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/frontend/dist /usr/share/nginx/html
EXPOSE 80
