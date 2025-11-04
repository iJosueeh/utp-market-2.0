// Función para mostrar alertas de Bootstrap dinámicas
function mostrarAlerta(mensaje, tipo = 'success') {
    const alertContainer = document.getElementById('dynamicAlertContainer');
    const alertId = 'alert-' + Date.now();
    
    const alertHTML = `
        <div id="${alertId}" class="alert alert-${tipo} alert-dismissible fade show animate__animated animate__fadeInDown" role="alert">
            <i class="bi ${tipo === 'success' ? 'bi-check-circle-fill' : tipo === 'warning' ? 'bi-exclamation-circle-fill' : 'bi-exclamation-triangle-fill'} me-2"></i>
            <strong>${mensaje}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    
    alertContainer.insertAdjacentHTML('beforeend', alertHTML);
    
    // Hacer scroll suave hacia la alerta
    alertContainer.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    
    // Auto-cerrar después de 5 segundos
    setTimeout(() => {
        const alertElement = document.getElementById(alertId);
        if (alertElement) {
            alertElement.classList.add('animate__fadeOutUp');
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
        radio.addEventListener("change", function() {
            updateAddressInputs();
            mostrarAlerta('Dirección de envío actualizada', 'success');
        });
    });

    // Add event listeners for payment method radio buttons
    document.querySelectorAll('input[name="metodoPago"]').forEach((radio) => {
        radio.addEventListener("change", function() {
            updatePaymentMethodInput();
            mostrarAlerta('Método de pago seleccionado', 'success');
        });
    });

    // Form validation before submission
    checkoutForm.addEventListener("submit", function (event) {
        // Validar dirección de envío
        if (!selectedCalleInput.value || !selectedDistritoInput.value) {
            mostrarAlerta("Por favor, selecciona una dirección de envío", 'warning');
            event.preventDefault();
            return;
        }
        
        // Validar método de pago
        if (!selectedMetodoPagoIdInput.value) {
            mostrarAlerta("Por favor, selecciona un método de pago", 'warning');
            event.preventDefault();
            return;
        }

        // Validación específica para tarjeta de crédito
        if (selectedMetodoPagoIdInput.value === "2") {
            const cardName = document.getElementById("cardName")?.value;
            const cardNumber = document.getElementById("cardNumber")?.value;
            const expiryDate = document.getElementById("expiryDate")?.value;
            const cvv = document.getElementById("cvv")?.value;

            if (!cardName || !cardNumber || !expiryDate || !cvv) {
                mostrarAlerta("Por favor, completa todos los campos de la tarjeta de crédito", 'danger');
                event.preventDefault();
                return;
            }
            
            // Validación de número de tarjeta (básica)
            if (cardNumber && cardNumber.replace(/\s/g, '').length < 13) {
                mostrarAlerta("Número de tarjeta inválido. Debe tener al menos 13 dígitos", 'danger');
                event.preventDefault();
                return;
            }
            
            // Validación de CVV
            if (cvv && (cvv.length < 3 || cvv.length > 4)) {
                mostrarAlerta("CVV inválido. Debe tener 3 o 4 dígitos", 'danger');
                event.preventDefault();
                return;
            }
        } 
        // Validación específica para tarjeta de débito
        else if (selectedMetodoPagoIdInput.value === "3") {
            const debitCardName = document.getElementById("debitCardName")?.value;
            const debitCardNumber = document.getElementById("debitCardNumber")?.value;
            const debitExpiryDate = document.getElementById("debitExpiryDate")?.value;
            const debitCvv = document.getElementById("debitCvv")?.value;

            if (!debitCardName || !debitCardNumber || !debitExpiryDate || !debitCvv) {
                mostrarAlerta("Por favor, completa todos los campos de la tarjeta de débito", 'danger');
                event.preventDefault();
                return;
            }
            
            // Validación de número de tarjeta (básica)
            if (debitCardNumber && debitCardNumber.replace(/\s/g, '').length < 13) {
                mostrarAlerta("Número de tarjeta inválido. Debe tener al menos 13 dígitos", 'danger');
                event.preventDefault();
                return;
            }
            
            // Validación de CVV
            if (debitCvv && (debitCvv.length < 3 || debitCvv.length > 4)) {
                mostrarAlerta("CVV inválido. Debe tener 3 o 4 dígitos", 'danger');
                event.preventDefault();
                return;
            }
        }
        
        // Si todo es válido, mostrar mensaje de procesamiento
        mostrarAlerta("Procesando tu pedido... Por favor espera", 'success');
    });
});
