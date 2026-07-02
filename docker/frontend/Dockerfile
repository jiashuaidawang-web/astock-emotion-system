FROM node:20-alpine AS builder
WORKDIR /workspace/astock-frontend
ARG VITE_API_BASE_URL=
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
COPY astock-frontend/package*.json ./
RUN npm install
COPY astock-frontend/ ./
RUN npm run build

FROM nginx:1.27-alpine
COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /workspace/astock-frontend/dist /usr/share/nginx/html
EXPOSE 80

