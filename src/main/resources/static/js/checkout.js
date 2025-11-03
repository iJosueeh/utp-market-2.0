document.addEventListener('DOMContentLoaded', function() {
    const checkoutForm = document.getElementById('checkoutForm');
    const selectedCalleInput = document.getElementById('selectedCalle');
    const selectedDistritoInput = document.getElementById('selectedDistrito');
    const selectedMetodoPagoIdInput = document.getElementById('selectedMetodoPagoId');

    // Function to update hidden address inputs
    function updateAddressInputs() {
        const selectedAddressRadio = document.querySelector('input[name="direccionEnvio"]:checked');
        if (selectedAddressRadio) {
            selectedCalleInput.value = selectedAddressRadio.dataset.calle;
            selectedDistritoInput.value = selectedAddressRadio.dataset.distrito;
        }
    }

    // Function to update hidden payment method input
    function updatePaymentMethodInput() {
        const selectedMetodoPagoRadio = document.querySelector('input[name="metodoPago"]:checked');
        if (selectedMetodoPagoRadio) {
            selectedMetodoPagoIdInput.value = selectedMetodoPagoRadio.value;
        }
    }

    // Initial update on page load
    updateAddressInputs();
    updatePaymentMethodInput();

    // Add event listeners for address radio buttons
    document.querySelectorAll('input[name="direccionEnvio"]').forEach(radio => {
        radio.addEventListener('change', updateAddressInputs);
    });

    // Add event listeners for payment method radio buttons
    document.querySelectorAll('input[name="metodoPago"]').forEach(radio => {
        radio.addEventListener('change', updatePaymentMethodInput);
    });

    // Optional: Add form validation before submission
    checkoutForm.addEventListener('submit', function(event) {
        if (!selectedCalleInput.value || !selectedDistritoInput.value) {
            alert('Por favor, selecciona una dirección de envío.');
            event.preventDefault();
            return;
        }
        if (!selectedMetodoPagoIdInput.value) {
            alert('Por favor, selecciona un método de pago.');
            event.preventDefault();
            return;
        }
        // Add more specific validation for credit card details if that method is selected
        // For now, we only validate that a method is chosen.
    });
});