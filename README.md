#### Este projeto funciona como um encurtador de URLs com API HTTP, persistência em MongoDB e comportamento de expiração para links temporários.

## O que o sistema faz
- Expõe o endpoint `POST /encurtar-url` para receber uma URL original e gerar uma URL curta.
- Valida a entrada para evitar criação de links inválidos (URL nula, vazia, malformada ou sem esquema HTTP/HTTPS).
- Gera um identificador curto único para cada URL e salva no banco com data de expiração.
- Retorna ao cliente a URL encurtada pronta para uso.
- Expõe o endpoint `GET /{id}` para resolver a URL curta e redirecionar para a URL original.
- Retorna `302 (FOUND)` com header `Location` quando o link está válido.
- Retorna `404 (NOT FOUND)` quando o id não existe.
- Retorna `410 (GONE)` quando o link já expirou.

## Regras de expiração
- Cada URL encurtada recebe `expiresAt` no momento da criação.
- O redirecionamento valida expiração em tempo de requisição (sem depender apenas do cleanup do banco).
- O MongoDB também remove documentos expirados via índice TTL, garantindo limpeza automática.
