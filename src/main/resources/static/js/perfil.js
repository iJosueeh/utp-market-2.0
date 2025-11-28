// Función para mostrar alertas de Bootstrap dinámicas
function mostrarAlerta(mensaje, tipo = "success") {
  const alertContainer = document.getElementById("dynamicAlertContainer");
  const alertId = "alert-" + Date.now();

  const alertHTML = `
        <div id="${alertId}" class="alert alert-${tipo} alert-dismissible fade show animate__animated animate__fadeInDown" role="alert">
            <i class="bi ${
              tipo === "success"
                ? "bi-check-circle-fill"
                : "bi-exclamation-triangle-fill"
            } me-2"></i>
            <strong>${mensaje}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;

  alertContainer.insertAdjacentHTML("beforeend", alertHTML);

  // Hacer scroll suave hacia la alerta
  alertContainer.scrollIntoView({ behavior: "smooth", block: "nearest" });

  // Auto-cerrar después de 5 segundos
  setTimeout(() => {
    const alertElement = document.getElementById(alertId);
    if (alertElement) {
      alertElement.classList.add("animate__fadeOutUp");
      setTimeout(() => {
        alertElement.remove();
      }, 500);
    }
  }, 5000);
}

// Función para agregar productos al carrito
async function agregarAlCarrito(button) {
  const productoId = button.dataset.productoId;

  const formData = new URLSearchParams();
  formData.append("productoId", productoId);
  formData.append("cantidad", 1);

  try {
    const response = await fetchAuth("/carrito/agregar", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: formData,
    });

    if (response.ok) {
      mostrarAlerta("¡Producto agregado al carrito exitosamente!", "success");
    } else {
      const text = await response.text();
      mostrarAlerta("Error al agregar al carrito: " + text, "danger");
    }
  } catch (error) {
    // El error de sesión expirada ya es manejado en fetchAuth, solo logueamos otros errores
    if (error.message !== "Sesión expirada") {
      console.error("Error en la petición de agregar al carrito:", error);
      mostrarAlerta("Error de conexión al agregar al carrito.", "danger");
    }
  }
}

// Función para agregar/quitar de favoritos
async function agregarAFavoritos(button) {
  const productoId = button.dataset.productoId;
  const icon = button.querySelector("i");

  try {
    const response = await fetchAuth(`/api/favoritos/toggle/${productoId}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
    });

    const data = await response.json();

    if (response.ok && data.success) {
      if (data.added) {
        icon.classList.remove("bi-heart");
        icon.classList.add("bi-heart-fill", "text-danger");
        mostrarAlerta("Producto agregado a favoritos", "success");
      } else {
        icon.classList.remove("bi-heart-fill", "text-danger");
        icon.classList.add("bi-heart");
        mostrarAlerta("Producto eliminado de favoritos", "info");
        // Recargar la página si estamos en la pestaña de favoritos
        const favoritosTab = document.getElementById("favoritos");
        if (favoritosTab && favoritosTab.classList.contains("active")) {
          setTimeout(() => location.reload(), 1000);
        }
      }
      console.log(data.message);
    } else {
      console.error("Error al actualizar favoritos:", data.message);
      mostrarAlerta(
        "Error al actualizar favoritos: " +
          (data.message || "Error desconocido"),
        "danger"
      );
    }
  } catch (error) {
    if (error.message !== "Sesión expirada") {
      console.error("Error en la petición de favoritos:", error);
      mostrarAlerta("Error de conexión al actualizar favoritos.", "danger");
    }
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const urlParams = new URLSearchParams(window.location.search);
  const activeTabName = urlParams.get("tab");

  // Manejo de pestañas activas
  if (activeTabName) {
    const perfilTabButton = document.getElementById("perfil-tab");
    const perfilTabContent = document.getElementById("perfil");

    if (perfilTabButton) {
      perfilTabButton.classList.remove("active");
      perfilTabButton.setAttribute("aria-selected", "false");
    }
    if (perfilTabContent) {
      perfilTabContent.classList.remove("show", "active");
    }

    const tabButton = document.getElementById(activeTabName + "-tab");
    const tabContent = document.getElementById(activeTabName);

    if (tabButton && tabContent) {
      tabButton.classList.add("active");
      tabButton.setAttribute("aria-selected", "true");
      tabContent.classList.add("show", "active");
    }
  }

  // Delegación de eventos para clicks (Favoritos y Carrito)
  document.body.addEventListener("click", function (event) {
    const target = event.target.closest("button");
    if (!target) return;

    if (target.classList.contains("add-to-favorites-btn")) {
      agregarAFavoritos(target);
    } else if (target.classList.contains("add-to-cart-btn")) {
      agregarAlCarrito(target);
    }
  });

  // Lógica para el modal de reseñas
  const editReviewModal = document.getElementById("editReviewModal");
  if (editReviewModal) {
    editReviewModal.addEventListener("show.bs.modal", function (event) {
      const button = event.relatedTarget;
      const reviewId = button.getAttribute("data-review-id");
      const reviewPuntaje = button.getAttribute("data-review-puntaje");
      const reviewComentario = button.getAttribute("data-review-comentario");

      const modalReviewId = editReviewModal.querySelector("#editReviewId");
      const modalEditComentario =
        editReviewModal.querySelector("#editComentario");
      const modalEditRatingStars =
        editReviewModal.querySelector("#editRatingStars");

      modalReviewId.value = reviewId;
      modalEditComentario.value = reviewComentario;

      modalEditRatingStars
        .querySelectorAll('input[name="puntaje"]')
        .forEach((radio) => {
          radio.checked = parseInt(radio.value) === parseInt(reviewPuntaje);
        });
    });
  }
});
