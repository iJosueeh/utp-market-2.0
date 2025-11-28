# UTP Market ✨

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-green.svg)
![Maven](https://img.shields.io/badge/Build-Maven-orange.svg)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue.svg)
![Thymeleaf](https://img.shields.io/badge/Frontend-Thymeleaf-005F0F.svg)
![Stripe](https://img.shields.io/badge/Payments-Stripe-6772E5.svg)

## 🚀 Descripción

**UTP Market** es una innovadora plataforma de comercio electrónico diseñada específicamente para la comunidad universitaria de la UTP. Facilita la compra y venta de productos y servicios relevantes para la vida estudiantil, desde materiales de estudio hasta artículos personales. Construida con la robustez de **Spring Boot**, la flexibilidad de **Thymeleaf** y la fiabilidad de **PostgreSQL**, esta aplicación ofrece una experiencia de usuario completa y segura.

## ✨ Características Principales

*   **Autenticación Híbrida Avanzada:** 🔐 Sistema robusto que combina JWT en Cookies HttpOnly para navegadores (mayor seguridad) y Headers para API.
*   **Experiencia de Usuario Mejorada:** 🎨 Redirección inteligente al expirar la sesión y renderizado condicional con Thymeleaf + Spring Security.
*   **Catálogo Dinámico de Productos:** 🛍️ Explora productos organizados por categorías, con potentes opciones de búsqueda y filtrado.
*   **Carrito de Compras Intuitivo:** 🛒 Añade, actualiza y elimina productos de tu carrito de forma sencilla antes de finalizar tu compra.
*   **Pasarela de Pagos Segura con Stripe:** 💳 Integración completa para procesar pagos con tarjetas de crédito y débito de forma segura.
*   **Panel de Administración:** 🛠️ Dashboard para administradores que permite gestionar usuarios, pedidos y productos.
*   **Gestión de Perfil Personalizado:** 👤 Actualiza tu información personal, detalles universitarios y preferencias de forma fácil.
*   **Historial de Pedidos Detallado:** 📦 Mantén un seguimiento de todas tus compras y el estado actual de tus pedidos.
*   **Lista de Favoritos:** ❤️ Guarda tus productos preferidos para acceder a ellos rápidamente en el futuro.
*   **Sistema de Reseñas y Valoraciones:** ⭐ Comparte tu experiencia y ayuda a otros usuarios con tus comentarios y puntuaciones.
*   **Asistencia con Chatbot (Gemini):** 🤖 Un asistente virtual integrado con la API de Google Gemini para resolver dudas.

## 🛠️ Tecnologías Utilizadas

### Backend
*   **Java 17:** Lenguaje de programación principal.
*   **Spring Boot 3.5.6:** Framework para el desarrollo rápido de aplicaciones robustas.
*   **Spring Security + JWT:** Implementación de seguridad moderna y sin estado (Stateless).
*   **Spring Data JPA (Hibernate):** Para el mapeo objeto-relacional y la interacción con la base de datos.
*   **Maven:** Herramienta de automatización de construcción y gestión de dependencias.

### Frontend
*   **Thymeleaf + Spring Security Extras:** Motor de plantillas con integración de seguridad para renderizado condicional.
*   **HTML5, CSS3, JavaScript:** Estructura, estilo e interactividad en el cliente.
*   **Bootstrap:** Framework CSS para un diseño responsivo y atractivo.

### Base de Datos
*   **PostgreSQL:** Sistema de gestión de bases de datos relacional potente y de código abierto.
*   **H2 Database:** Para pruebas de integración y desarrollo en memoria.

### Servicios de Terceros
*   **Stripe:** Para el procesamiento de pagos con tarjeta.
*   **Google Gemini:** API para la funcionalidad del chatbot.

## 🚀 Primeros Pasos

### Prerrequisitos

Antes de comenzar, asegúrate de tener instaladas las siguientes herramientas:

*   **Java Development Kit (JDK) 17** o superior.
*   **Apache Maven**.
*   **PostgreSQL**.

### Configuración

1.  **Clona el repositorio:**
    ```bash
    git clone https://github.com/tu-usuario/utp-market-2.0.git
    cd utp-market-2.0
    ```

2.  **Crea una base de datos PostgreSQL:** Puedes nombrarla `utp_market_db` o el nombre que prefieras.

3.  **Configura las variables de entorno:** El proyecto utiliza placeholders en `application.properties` para las claves sensibles. La forma recomendada es configurar estas variables en tu sistema operativo.

    ```bash
    # Para la base de datos
    export DB_URL="jdbc:postgresql://localhost:5432/utp_market_db"
    export DB_USERNAME="tu_usuario_postgres"
    export DB_PASSWORD="tu_contraseña_postgres"

    # Para los servicios
    export STRIPE_SECRET_KEY="sk_test_..."
    export GEMINI_API_KEY="..."
    ```
    *Como alternativa, puedes reemplazar los placeholders `${...}` directamente en el archivo `application.properties`.*

### Ejecutar la Aplicación

1.  **Compila el proyecto:**
    ```bash
    ./mvnw.cmd clean install
    ```
2.  **Inicia la aplicación Spring Boot:**
    ```bash
    ./mvnw.cmd spring-boot:run
    ```

Una vez iniciada, la aplicación estará accesible en tu navegador a través de `http://localhost:8080`.

## 📂 Estructura del Proyecto

El proyecto sigue una estructura estándar de Spring Boot, organizada para una clara separación de responsabilidades:

```
src/main/java/com/utpmarket.utp_market/
├── controllers/        // 🌐 Maneja las solicitudes HTTP y define los endpoints.
├── models/             // 📊 Define las entidades de la base de datos (entity), DTOs y enums.
├── repository/         // 💾 Interfaces para la interacción con la base de datos (Spring Data JPA).
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
