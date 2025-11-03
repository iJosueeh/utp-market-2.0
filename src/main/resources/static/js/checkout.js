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
        radio.addEventListener("change", updateAddressInputs);
    });

    // Add event listeners for payment method radio buttons
    document.querySelectorAll('input[name="metodoPago"]').forEach((radio) => {
        radio.addEventListener("change", updatePaymentMethodInput);
    });

    // Optional: Add form validation before submission
    checkoutForm.addEventListener("submit", function (event) {
        if (!selectedCalleInput.value || !selectedDistritoInput.value) {
            alert("Por favor, selecciona una dirección de envío.");
            event.preventDefault();
            return;
        }
        if (!selectedMetodoPagoIdInput.value) {
            alert("Por favor, selecciona un método de pago.");
            event.preventDefault();
            return;
        }

        // Specific validation for credit card if selected
        if (selectedMetodoPagoIdInput.value === "2") {
            // Assuming 2 is the ID for Credit Card
            const cardName = document.getElementById("cardName").value;
            const cardNumber = document.getElementById("cardNumber").value;
            const expiryDate = document.getElementById("expiryDate").value;
            const cvv = document.getElementById("cvv").value;

            if (!cardName || !cardNumber || !expiryDate || !cvv) {
                alert("Por favor, completa todos los campos de la tarjeta de crédito.");
                event.preventDefault();
                return;
            }
            // Further regex validation can be added here if needed
        } else if (selectedMetodoPagoIdInput.value === "3") {
            // Assuming 3 is the ID for Debit Card
            const debitCardName = document.getElementById("debitCardName").value;
            const debitCardNumber = document.getElementById("debitCardNumber").value;
            const debitExpiryDate = document.getElementById("debitExpiryDate").value;
            const debitCvv = document.getElementById("debitCvv").value;

            if (!debitCardName || !debitCardNumber || !debitExpiryDate || !debitCvv) {
                alert("Por favor, completa todos los campos de la tarjeta de débito.");
                event.preventDefault();
                return;
            }
        }
    });
});
