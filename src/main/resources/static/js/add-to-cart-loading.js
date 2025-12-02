// Interceptar formularios de agregar al carrito y mostrar loading
document.addEventListener("DOMContentLoaded", function () {
  document
    .querySelectorAll('form[action*="/carrito/agregar"]')
    .forEach((form) => {
      form.addEventListener("submit", function (e) {
        e.preventDefault();

        Swal.fire({
          title: "Agregando al carrito...",
          html: "Por favor espera",
          allowOutsideClick: false,
          allowEscapeKey: false,
          didOpen: () => {
            Swal.showLoading();
          },
        });

        this.submit();
      });
    });
});
