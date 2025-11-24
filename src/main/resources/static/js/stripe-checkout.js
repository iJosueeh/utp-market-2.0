document.addEventListener("DOMContentLoaded", function () {
  const stripe = Stripe(
    "pk_test_51SWmrw2NRLNfewcp5pg83FGxLPyaN9IsRIX8fMFapW59WiiivAcnj2K8nBJJNaLAUqAwlfQrBeAd6QgTrfD1h9bq00QGbbasBb"
  ); // Reemplaza con tu llave pública
  const elements = stripe.elements();

  // Custom styling for the Stripe Element
  const style = {
    base: {
      color: "#32325d",
      fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
      fontSmoothing: "antialiased",
      fontSize: "16px",
      "::placeholder": {
        color: "#aab7c4",
      },
    },
    invalid: {
      color: "#dc3545", // Use site's danger color for errors
      iconColor: "#dc3545",
    },
  };

  // Create an instance of the card Element with the custom style.
  const card = elements.create("card", { style: style });

  // Add an instance of the card Element into the `card-element` <div>.
  if (document.getElementById("card-element")) {
    card.mount("#card-element");
  }

  const checkoutForm = document.getElementById("checkoutForm");
  const btnPagar = document.querySelector('button[type="submit"]');

  if (checkoutForm) {
    checkoutForm.addEventListener("submit", function (event) {
      const metodoPagoId = document.querySelector(
        'input[name="metodoPago"]:checked'
      ).value;

      // IDs 2 and 3 correspond to Credit/Debit cards
      if (metodoPagoId === "2" || metodoPagoId === "3") {
        event.preventDefault();

        if (btnPagar) {
          btnPagar.disabled = true;
          btnPagar.innerText = "Procesando...";
        }

        stripe
          .createPaymentMethod({
            type: "card",
            card: card,
            billing_details: {
              // You can add billing details here if collected in the form
              // name: document.getElementById('cardName').value,
            },
          })
          .then(function (result) {
            if (result.error) {
              // Show error to your customer
              console.error(result.error.message);
              // Display error in the card-errors div
              const errorDiv = document.getElementById("card-errors");
              if(errorDiv) {
                  errorDiv.textContent = result.error.message;
              }
              if (btnPagar) {
                btnPagar.disabled = false;
                btnPagar.innerText = "Finalizar Compra";
              }
            } else {
              // Send the token to your server.
              stripeTokenHandler(result.paymentMethod.id);
            }
          });
      }
    });
  }

  function stripeTokenHandler(token) {
    // Insert the token ID into the form so it gets submitted to the server
    const hiddenInput = document.getElementById("stripeToken");
    hiddenInput.value = token;

    // Submit the form
    checkoutForm.submit();
  }
});
