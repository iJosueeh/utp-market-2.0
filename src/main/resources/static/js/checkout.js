// Función para mostrar alertas de Bootstrap dinámicas
function mostrarAlerta(mensaje, tipo = "success") {
  const alertContainer = document.getElementById("dynamicAlertContainer");
  const alertId = "alert-" + Date.now();

  const alertHTML = `
        <div id="${alertId}" class="alert alert-${tipo} alert-dismissible fade show animate__animated animate__fadeInDown" role="alert">
            <i class="bi ${
              tipo === "success"
                ? "bi-check-circle-fill"
                : tipo === "warning"
                ? "bi-exclamation-circle-fill"
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

document.addEventListener("DOMContentLoaded", function () {
  const checkoutForm = document.getElementById("checkoutForm");
  const selectedCalleInput = document.getElementById("selectedCalle");
  const selectedDistritoInput = document.getElementById("selectedDistrito");
  const selectedMetodoPagoIdInput = document.getElementById(
    "selectedMetodoPagoId"
  );

  // Function to update hidden address inputs
  function updateAddressInputs() {
    const selectedAddressRadio = document.querySelector(
      'input[name="direccionEnvio"]:checked'
    );
    if (selectedAddressRadio) {
      selectedCalleInput.value = selectedAddressRadio.dataset.calle;
      selectedDistritoInput.value = selectedAddressRadio.dataset.distrito;
    }
  }

  // Function to update hidden payment method input
  function updatePaymentMethodInput() {
    const selectedMetodoPagoRadio = document.querySelector(
      'input[name="metodoPago"]:checked'
    );
    if (selectedMetodoPagoRadio) {
      selectedMetodoPagoIdInput.value = selectedMetodoPagoRadio.value;
    }
  }

  // Initial update on page load
  updateAddressInputs();
  updatePaymentMethodInput();

  // Add event listeners for address radio buttons
  document.querySelectorAll('input[name="direccionEnvio"]').forEach((radio) => {
    radio.addEventListener("change", function () {
      updateAddressInputs();
      mostrarAlerta("Dirección de envío actualizada", "success");
    });
  });

  // Add event listeners for payment method radio buttons
  document.querySelectorAll('input[name="metodoPago"]').forEach((radio) => {
    radio.addEventListener("change", function () {
      updatePaymentMethodInput();
      mostrarAlerta("Método de pago seleccionado", "success");
    });
  });

  // Form validation and submission
  checkoutForm.addEventListener("submit", async function (event) {
    event.preventDefault(); // Prevent default form submission

    // Validar dirección de envío
    if (!selectedCalleInput.value || !selectedDistritoInput.value) {
      mostrarAlerta("Por favor, selecciona una dirección de envío", "warning");
      return;
    }

    // Validar método de pago
    if (!selectedMetodoPagoIdInput.value) {
      mostrarAlerta("Por favor, selecciona un método de pago", "warning");
      return;
    }

    // Collect payment details based on selected method
    let paymentDetails = {};
    const metodoPagoId = selectedMetodoPagoIdInput.value;

    if (metodoPagoId === "2") {
      return;
    }

    mostrarAlerta("Procesando tu pedido... Por favor espera", "info");

    try {
      const response = await fetchAuth("/carrito/realizar-pago", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          calle: selectedCalleInput.value,
          distrito: selectedDistritoInput.value,
          metodoPagoId: parseInt(metodoPagoId),
          ...paymentDetails, // Spread payment details if any
        }),
      });

      if (response.ok) {
        const result = await response.json(); // Assuming backend returns JSON on success
        mostrarAlerta(
          result.message || "¡Pedido realizado con éxito!",
          "success"
        );
        window.location.href = result.redirectUrl || "/pedidos/confirmacion"; // Redirect to confirmation page
      } else {
        const errorData = await response.json(); // Assuming backend returns JSON on error
        mostrarAlerta(
          errorData.message || "Error al procesar el pedido.",
          "danger"
        );
      }
    } catch (error) {
      if (error.message !== "Sesión expirada") {
        // fetchAuth handles session expiry
        console.error("Error al finalizar la compra:", error);
        mostrarAlerta(
          "Error de conexión al procesar el pedido. Intenta de nuevo.",
          "danger"
        );
      }
    }
  });
});
