# Imagem base enxuta (alpine)
FROM node:20-alpine

WORKDIR /app

# Copia só o package.json primeiro: o Docker reaproveita o cache
# desta camada enquanto as dependências não mudarem
COPY package*.json ./
RUN npm install --omit=dev

# Depois copia o código
COPY . .

EXPOSE 3000

# Roda como usuário não-root (boa prática de segurança)
USER node

CMD ["node", "server.js"]
