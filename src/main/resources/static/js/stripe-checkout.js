document.addEventListener("DOMContentLoaded", function () {
  // Configuración
  const publicKey =
    "pk_test_51SWmrw2NRLNfewcp5pg83FGxLPyaN9IsRIX8fMFapW59WiiivAcnj2K8nBJJNaLAUqAwlfQrBeAd6QgTrfD1h9bq00QGbbasBb";
  const radioName = "metodoPago";
  const cardFieldsId = "card-fields";

  // Elementos de Stripe
  let stripe = null;
  let elements = null;
  let cardNumber = null;
  let cardExpiry = null;
  let cardCvc = null;
  let isMounted = false;

  console.log("Stripe Checkout: Iniciando...");

  // 1. Cargar Script de Stripe
  const script = document.createElement("script");
  script.src = "https://js.stripe.com/v3/";
  script.async = true;
  script.onload = () => {
    console.log("Stripe Checkout: Script cargado");
    initializeStripe();
  };
  script.onerror = () => {
    console.error("Stripe Checkout: Error cargando script");
    alert("Error de conexión con la pasarela de pagos.");
  };
  document.head.appendChild(script);

  // 2. Inicializar Objetos Stripe
  function initializeStripe() {
    if (!window.Stripe) return;

    try {
      stripe = Stripe(publicKey);
      elements = stripe.elements();

      const style = {
        base: {
          color: "#32325d",
          fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
          fontSmoothing: "antialiased",
          fontSize: "16px",
          "::placeholder": { color: "#aab7c4" },
        },
        invalid: { color: "#dc3545", iconColor: "#dc3545" },
      };

      // Crear elementos
      cardNumber = elements.create("cardNumber", {
        style: style,
        showIcon: true,
      });
      cardExpiry = elements.create("cardExpiry", { style: style });
      cardCvc = elements.create("cardCvc", { style: style });

      // Intentar montar si corresponde
      checkAndMount();
    } catch (e) {
      console.error("Stripe Checkout: Error inicializando", e);
    }
  }

  // 3. Función para montar elementos
  function mountElements() {
    if (!cardNumber || isMounted) return;

    try {
      // Verificar que los contenedores existan en el DOM
      if (!document.getElementById("card-number")) return;

      console.log("Stripe Checkout: Montando elementos...");
      cardNumber.mount("#card-number");
      cardExpiry.mount("#card-expiry");
      cardCvc.mount("#card-cvc");
      isMounted = true;
      console.log("Stripe Checkout: Elementos montados exitosamente");
    } catch (e) {
      console.error("Stripe Checkout: Error al montar", e);
    }
  }

  // 4. Lógica de UI y Visibilidad
  function checkAndMount() {
    const selected = document.querySelector(
      `input[name="${radioName}"]:checked`
    );
    const cardFields = document.getElementById(cardFieldsId);

    if (!selected || !cardFields) return;

    if (selected.value === "2") {
      // Mostrar contenedor
      cardFields.style.display = "block";

      // Pequeño delay para asegurar renderizado antes de montar
      setTimeout(() => {
        mountElements();
      }, 100);
    } else {
      cardFields.style.display = "none";
    }
  }

  // 5. Listeners
  const radios = document.querySelectorAll(`input[name="${radioName}"]`);
  radios.forEach((radio) => {
    radio.addEventListener("change", checkAndMount);
  });

  // Ejecutar verificación inicial
  // Usamos setTimeout para asegurar que el DOM esté "settled"
  setTimeout(checkAndMount, 200);

  // 6. Manejo del Submit
  const checkoutForm = document.getElementById("checkoutForm");
  const btnPagar = document.querySelector('button[type="submit"]');

  if (checkoutForm) {
    checkoutForm.addEventListener("submit", function (event) {
      const selected = document.querySelector(
        `input[name="${radioName}"]:checked`
      );

      if (selected && selected.value === "2") {
        event.preventDefault();
        event.stopImmediatePropagation();

        if (!stripe || !cardNumber || !isMounted) {
          alert(
            "El sistema de pagos se está cargando. Por favor espera un momento."
          );
          // Intentar recuperar
          if (!stripe) initializeStripe();
          else checkAndMount();
          return;
        }

        if (btnPagar) {
          btnPagar.disabled = true;
          btnPagar.innerText = "Procesando...";
        }

        stripe
          .createPaymentMethod({
            type: "card",
            card: cardNumber,
            billing_details: {},
          })
          .then(function (result) {
            if (result.error) {
              const errorDiv = document.getElementById("card-errors");
              if (errorDiv) errorDiv.textContent = result.error.message;
              if (btnPagar) {
                btnPagar.disabled = false;
                btnPagar.innerText = "Finalizar Compra";
              }
            } else {
              const hiddenInput = document.getElementById("stripeToken");
              if (hiddenInput) hiddenInput.value = result.paymentMethod.id;
              checkoutForm.submit();
            }
          });
      }
    });
  }
});
