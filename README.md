# UTP Market ✨

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-green.svg)
![Maven](https://img.shields.io/badge/Build-Maven-orange.svg)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue.svg)
![Thymeleaf](https://img.shields.io/badge/Frontend-Thymeleaf-005F0F.svg)
![AWS S3](https://img.shields.io/badge/Cloud-AWS_S3-FF9900.svg)

## 🚀 Descripción

**UTP Market 2.0** es una innovadora plataforma de comercio electrónico diseñada específicamente para la comunidad universitaria. Facilita la compra y venta de productos y servicios relevantes para la vida estudiantil, desde materiales de estudio hasta artículos personales. Construida con la robustez de **Spring Boot**, la flexibilidad de **Thymeleaf** y la fiabilidad de **PostgreSQL**, esta aplicación ofrece una experiencia de usuario completa y segura.

## ✨ Características Principales

*   **Autenticación Segura:** 🔐 Sistema completo de registro, inicio de sesión y recuperación de contraseña para estudiantes y vendedores.
*   **Catálogo Dinámico de Productos:** 🛍️ Explora productos organizados por categorías, con potentes opciones de búsqueda y filtrado.
*   **Páginas de Detalle de Producto:** 📄 Información exhaustiva de cada artículo, incluyendo descripciones, imágenes de alta calidad y reseñas de usuarios.
*   **Carrito de Compras Intuitivo:** 🛒 Gestiona tus selecciones de productos de forma sencilla antes de finalizar tu compra.
*   **Historial de Pedidos Detallado:** 📦 Mantén un seguimiento de todas tus compras y el estado actual de tus pedidos.
*   **Gestión de Perfil Personalizado:** 👤 Actualiza tu información personal, detalles universitarios y preferencias de forma fácil.
*   **Lista de Favoritos:** ❤️ Guarda tus productos preferidos para acceder a ellos rápidamente en el futuro.
*   **Sistema de Reseñas y Valoraciones:** ⭐ Comparte tu experiencia y ayuda a otros usuarios con tus comentarios y puntuaciones.
*   **Asistencia con Chatbot:** 🤖 Un asistente virtual (integrado con Gemini) para resolver tus dudas y ofrecerte soporte.
*   **Almacenamiento en la Nube:** ☁️ Integración con AWS S3 para una gestión eficiente y escalable de las imágenes de los productos.

## 🛠️ Tecnologías Utilizadas

### Backend
*   **Java 17:** Lenguaje de programación principal.
*   **Spring Boot 3.5.6:** Framework para el desarrollo rápido de aplicaciones robustas.
*   **Spring Data JPA:** Simplifica la interacción con la base de datos.
*   **Spring Security Crypto:** Proporciona funcionalidades criptográficas para la seguridad de contraseñas.
*   **Maven:** Herramienta de automatización de construcción y gestión de dependencias.

### Frontend
*   **Thymeleaf:** Motor de plantillas moderno para la generación de vistas HTML.
*   **HTML5, CSS3:** Estructura y estilo de la interfaz de usuario.
*   **Bootstrap:** Framework CSS para un diseño responsivo y atractivo.
*   **JavaScript:** Para interactividad y funcionalidades dinámicas en el cliente.

### Base de Datos
*   **PostgreSQL:** Sistema de gestión de bases de datos relacional potente y de código abierto.

### Servicios Cloud
*   **AWS SDK para S3:** Para la integración con Amazon S3, utilizado para el almacenamiento de archivos estáticos como imágenes.

## 🚀 Primeros Pasos

### Prerrequisitos

Antes de comenzar, asegúrate de tener instaladas las siguientes herramientas:

*   **Java Development Kit (JDK) 17**
*   **Apache Maven**
*   **PostgreSQL**

### Configuración de la Base de Datos

1.  **Crea una base de datos PostgreSQL:** Puedes nombrarla `utp_market_db` o el nombre que prefieras.
2.  **Actualiza `application.properties`:** Modifica el archivo `src/main/resources/application.properties` con las credenciales de tu base de datos:

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/utp_market_db
    spring.datasource.username=tu_usuario_postgres
    spring.datasource.password=tu_contraseña_postgres
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```

### Ejecutar la Aplicación

1.  **Clona el repositorio:**
    ```bash
    git clone https://github.com/tu-usuario/utp-market-2.0.git
    cd utp-market-2.0
    ```
2.  **Compila el proyecto:**
    ```bash
    mvn clean install
    ```
3.  **Inicia la aplicación Spring Boot:**
    ```bash
    mvn spring-boot:run
    ```

Una vez iniciada, la aplicación estará accesible en tu navegador a través de `http://localhost:8080`.

## 📂 Estructura del Proyecto

El proyecto sigue una estructura estándar de Spring Boot, organizada para una clara separación de responsabilidades:

```
src/main/java/com/utpmarket.utp_market/
├── controllers/        // 🌐 Maneja las solicitudes HTTP y define los endpoints de la API.
├── models/             // 📊 Define las entidades de la base de datos (entity), DTOs y enums.
├── repository/         // 💾 Interfaces para la interacción con la base de datos utilizando Spring Data JPA.
└── services/           // ⚙️ Contiene la lógica de negocio y coordina las operaciones.

src/main/resources/
├── static/             // 🖼️ Archivos estáticos como CSS, JavaScript e imágenes.
├── templates/          // 🖥️ Plantillas HTML renderizadas por Thymeleaf.
└── application.properties // ⚙️ Archivo de configuración principal de la aplicación.
```

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Si deseas mejorar este proyecto, por favor, sigue estos pasos:

1.  Haz un fork del repositorio.
2.  Crea una nueva rama (`git checkout -b feature/nueva-funcionalidad`).
3.  Realiza tus cambios y haz commit (`git commit -m 'feat: Añadir nueva funcionalidad X'`).
4.  Sube tus cambios (`git push origin feature/nueva-funcionalidad`).
5.  Abre un Pull Request.

## 📄 Licencia

Este proyecto está bajo la licencia [MIT](https://opensource.org/licenses/MIT).

---
Made with ❤️ by iJosueeh, kath144, AlexanderSc21, KennySth, ian101710