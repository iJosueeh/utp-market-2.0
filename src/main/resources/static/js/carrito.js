// Función para mostrar alertas
function mostrarAlerta(mensaje, tipo = "success") {
  const alertContainer = document.getElementById("dynamicAlertContainer");
  if (!alertContainer) return;

  const alertId = "alert-" + Date.now();
  const alertHTML = `
        <div id="${alertId}" class="alert alert-${tipo} alert-dismissible fade show animate__animated animate__fadeInDown" role="alert">
            <i class="bi ${
              tipo === "success"
                ? "bi-check-circle-fill"
                : "bi-exclamation-triangle-fill"
            } me-2"></i>
            <strong>${mensaje}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;

  alertContainer.insertAdjacentHTML("beforeend", alertHTML);
  window.scrollTo({ top: 0, behavior: "smooth" });

  setTimeout(() => {
    const alertElement = document.getElementById(alertId);
    if (alertElement) {
      alertElement.classList.add("animate__fadeOutUp");
      setTimeout(() => alertElement.remove(), 500);
    }
  }, 3000);
}

// Función para actualizar solo el resumen del pedido (más rápido)
function actualizarResumen(subtotal, total) {
  const subtotalDisplay = document.getElementById("order-summary-subtotal");
  const shippingDisplay = document.getElementById("order-summary-shipping");
  const totalDisplay = document.getElementById("order-summary-total");

  if (subtotalDisplay)
    subtotalDisplay.textContent = `S/ ${subtotal.toFixed(2)}`;
  if (totalDisplay) totalDisplay.textContent = `S/ ${total.toFixed(2)}`;
  if (shippingDisplay) {
    shippingDisplay.textContent = subtotal > 100 ? "GRATIS" : "S/ 10.00";
  }
}

// Función para actualizar un item específico del carrito
function actualizarItemCarrito(itemId, nuevaCantidad, nuevoSubtotal) {
  const itemCard = document
    .querySelector(`[data-item-id="${itemId}"]`)
    ?.closest(".card");
  if (!itemCard) return;

  // Actualizar cantidad en el input
  const quantityInput = itemCard.querySelector('input[type="number"]');
  if (quantityInput) quantityInput.value = nuevaCantidad;

  // Actualizar subtotal del item
  const subtotalText = itemCard.querySelector(".fw-bold");
  if (subtotalText)
    subtotalText.textContent = `Subtotal: S/ ${nuevoSubtotal.toFixed(2)}`;

  // Actualizar valores de los botones
  const btnMenos = itemCard.querySelector(
    'button[name="cantidad"]:first-of-type'
  );
  const btnMas = itemCard.querySelector('button[name="cantidad"]:last-of-type');
  if (btnMenos) {
    btnMenos.value = nuevaCantidad - 1;
    btnMenos.disabled = nuevaCantidad <= 1;
  }
  if (btnMas) btnMas.value = nuevaCantidad + 1;
}

// Event listener principal
document.addEventListener("DOMContentLoaded", function () {
  // Interceptar formularios de actualización de cantidad
  document
    .querySelectorAll('form[action*="/actualizar-cantidad"]')
    .forEach((form) => {
      form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const formData = new FormData(this);
        const itemId = formData.get("itemId");

        // Obtener la cantidad del botón que se clickeó (+ o -)
        const submitButton = e.submitter;
        const cantidad = submitButton
          ? parseInt(submitButton.value)
          : parseInt(formData.get("cantidad"));

        if (!cantidad || cantidad < 1 || isNaN(cantidad)) {
          console.error("Cantidad inválida:", cantidad);
          return;
        }

        try {
          const response = await fetch("/carrito/actualizar-cantidad-ajax", {
            method: "POST",
            headers: {
              "Content-Type": "application/x-www-form-urlencoded",
            },
            body: new URLSearchParams({ itemId, cantidad }),
          });

          if (response.redirected) {
            window.location.href = response.url;
            return;
          }

          if (response.ok) {
            const data = await response.json();
            if (data.success) {
              // Actualizar solo el item y el resumen (más rápido que recargar)
              actualizarItemCarrito(itemId, cantidad, data.itemSubtotal);
              actualizarResumen(data.subtotal, data.total);
              mostrarAlerta("Cantidad actualizada", "success");
            } else {
              mostrarAlerta(data.message || "Error al actualizar", "danger");
            }
          } else {
            mostrarAlerta("Error al actualizar cantidad", "danger");
          }
        } catch (error) {
          console.error("Error:", error);
          mostrarAlerta("Error de conexión", "danger");
        }
      });
    });

  // Interceptar formularios de eliminar
  document.querySelectorAll('form[action*="/eliminar/"]').forEach((form) => {
    form.addEventListener("submit", async function (e) {
      e.preventDefault();

      // Usar SweetAlert2 para confirmación moderna
      const result = await Swal.fire({
        title: "¿Eliminar producto?",
        text: "¿Estás seguro de eliminar este producto del carrito?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#dc3545",
        cancelButtonColor: "#6c757d",
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar",
      });

      if (!result.isConfirmed) return;

      // Mostrar loading mientras se elimina
      Swal.fire({
        title: "Eliminando producto...",
        html: "Por favor espera",
        allowOutsideClick: false,
        allowEscapeKey: false,
        didOpen: () => {
          Swal.showLoading();
        },
      });

      const url = this.action;

      try {
        const response = await fetch(url, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        });

        if (response.redirected) {
          window.location.href = response.url;
          return;
        }

        if (response.ok) {
          const data = await response.json();
          if (data.success) {
            // Si el carrito queda vacío, recargar para mostrar mensaje
            if (data.carritoItems.length === 0) {
              Swal.close();
              window.location.reload();
            } else {
              // Eliminar el item del DOM
              this.closest(".card").remove();
              actualizarResumen(data.subtotal, data.total);

              // Alerta de éxito con SweetAlert2
              Swal.fire({
                icon: "success",
                title: "¡Producto eliminado!",
                toast: true,
                position: "top-end",
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true,
              });
            }
          } else {
            Swal.fire({
              icon: "error",
              title: "Error",
              text: data.message || "Error al eliminar producto",
            });
          }
        } else {
          Swal.fire({
            icon: "error",
            title: "Error",
            text: "Error al eliminar producto",
          });
        }
      } catch (error) {
        console.error("Error:", error);
        Swal.fire({
          icon: "error",
          title: "Error de conexión",
          text: "No se pudo conectar con el servidor",
        });
      }
    });
  });
});
