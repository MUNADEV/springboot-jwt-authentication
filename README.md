# Spring Boot Security JWT 

## Description

This is a simple Spring Boot application that uses JWT for authentication and authorization with roles.

## Architecture

The application is divided into three layers: controller, service and repository.

```
src
|-- main
|   |-- java
|   |   |-- com.example.myapp
|   |   |   |-- controller       // Controladores RESTful
|   |   |   |-- model            // Modelos de datos
|   |   |   |-- repository       // Repositorios de acceso a datos
|   |   |   |-- service          // Capa de servicios
|   |   |-- resources
|   |   |   |-- application.yml  // Archivo de configuración de Spring
|-- test  
```


## Database Inserts

We need to insert the following data into the database:

```sql
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  CONSTRAINT username_length CHECK (LENGTH(username) > 0),
  CONSTRAINT password_length CHECK (LENGTH(password) > 0)
);

CREATE TABLE roles (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  CONSTRAINT name_length CHECK (LENGTH(name) > 0)
);

CREATE TABLE users_roles (
  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);
```

