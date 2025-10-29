# Proyecto: Micro Twitter Serverless con Spring Boot, AWS y JWT

David Alfonso Barbosa Gómez

## Descripción
Este proyecto implementa una plataforma tipo Twitter, donde los usuarios pueden realizar publicaciones ("posts") de hasta 140 caracteres.
El sistema fue diseñado inicialmente como un monolito en Spring Boot, luego evolucionó a una arquitectura basada en microservicios utilizando AWS Lambda y autenticación con Cognito.

La aplicación cliente está desarrollada en JavaScript (frontend web) y desplegada en AWS S3, disponible públicamente en internet.

## Arquitectura del Sistema

### Etapa 1: Monolito Spring Boot

* Implementación inicial con las entidades:

  * Usuario

  * Post

  * Stream (Hilo global de publicaciones)

* API REST desarrollada en Spring Boot

* Seguridad agregada mediante JWT

* Pruebas locales con Postman y H2 Database

### Etapa 2: Arquitectura Serverless con AWS

* Separación del monolito en tres microservicios:

    * User Service (Lambda): manejo de usuarios y autenticación.

    * Post Service (Lambda): registro y consulta de publicaciones.

    * Stream Service (Lambda): consolidación del flujo de posts.

    <img width="1602" height="239" alt="imagen" src="https://github.com/user-attachments/assets/b1957af9-1ad0-46f6-a7da-6e89b62a2cf3" />

* AWS Cognito para la autenticación JWT y manejo de usuarios.

* DynamoDB como base de datos persistente para los posts y usuarios.
<img width="339" height="272" alt="imagen" src="https://github.com/user-attachments/assets/f965051c-ed45-4274-8657-0290e72a56d3" />


## Frontend

* Aplicación escrita en JavaScript/HTML/CSS

* Consume el API REST mediante fetch()

* Desplegada en AWS S3 (bucket público con hosting estático habilitado)

<img width="1035" height="168" alt="imagen" src="https://github.com/user-attachments/assets/1a7b890e-8afe-4fe1-8ee7-89283c8c7064" />

<img width="1558" height="226" alt="imagen" src="https://github.com/user-attachments/assets/b2b66434-8b97-44ce-9741-286b3c41e861" />

### Pantallas 

Login

<img width="642" height="142" alt="imagen" src="https://github.com/user-attachments/assets/14d5b589-d4c2-4614-9586-74832f5ea13b" />

Feed y Post

<img width="634" height="304" alt="imagen" src="https://github.com/user-attachments/assets/bb1e5910-d498-4ed9-92a0-66b86a62c0a5" />


Bucket website endpoint http://microtwitter-frontends3.s3-website-us-east-1.amazonaws.com

## Seguridad

* Autenticación y autorización mediante JWT

* Integración con AWS Cognito

* El token se envía en el encabezado Authorization en cada petición

### Users Cognito
<img width="1575" height="192" alt="imagen" src="https://github.com/user-attachments/assets/4773c5e4-9cee-420e-b504-054cb3f8bc73" />

