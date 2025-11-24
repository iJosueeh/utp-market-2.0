document.addEventListener("DOMContentLoaded", function () {
  const checkoutForm = document.getElementById("checkoutForm");
  const btnPagar = document.querySelector('button[type="submit"]');

  if (checkoutForm) {
    checkoutForm.addEventListener("submit", function (e) {
      const metodoPagoId = document.querySelector(
        'input[name="metodoPago"]:checked'
      ).value;

      // IDs 2 and 3 correspond to Credit/Debit cards (adjust based on your DB)
      if (metodoPagoId === "2" || metodoPagoId === "3") {
        e.preventDefault(); // Prevent default submission

        // Disable button to prevent double clicks
        if (btnPagar) btnPagar.disabled = true;
        if (btnPagar) btnPagar.innerText = "Procesando...";

        // Configure Culqi
        Culqi.settings({
          title: "UTP Market",
          currency: "PEN",
          description: "Compra en UTP Market",
          amount: calculateTotalAmount(), // Implement this function to get total in cents
        });

        // Open Culqi Checkout
        Culqi.open();
      }
    });
  }
});

function culqi() {
  if (Culqi.token) {
    // Get the token ID
    const token = Culqi.token.id;
    const email = Culqi.token.email;

    // Set the token in the hidden field
    document.getElementById("culqiToken").value = token;

    // Submit the form
    const checkoutForm = document.getElementById("checkoutForm");
    checkoutForm.submit();
  } else {
    // Error handling
    console.error(Culqi.error);
    alert(Culqi.error.user_message);

    const btnPagar = document.querySelector('button[type="submit"]');
    if (btnPagar) btnPagar.disabled = false;
    if (btnPagar) btnPagar.innerText = "Finalizar Compra";
  }
}

function calculateTotalAmount() {
  // This function should extract the total amount from the page or a hidden field
  // and convert it to cents (integer).
  // Example: S/ 100.00 -> 10000

  // For now, let's try to get it from the displayed total or a data attribute
  // You might want to add a data-total attribute to your total element in HTML
  // Or parse the text content.

  // Let's assume there's a hidden input with the total or we parse the text
  // Ideally, pass the total in cents from the backend to a hidden input

  // Fallback parsing (risky if format changes)
  // Better: Add <input type="hidden" id="totalAmount" th:value="${total * 100}" /> in HTML

  // For this implementation, I'll look for a hidden input I'll add to checkout.html
  const totalInput = document.getElementById("totalAmountCents");
  if (totalInput) {
    return parseInt(totalInput.value);
  }
  return 0;
}
