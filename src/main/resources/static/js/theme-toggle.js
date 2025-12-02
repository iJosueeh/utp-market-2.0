// Theme Toggle Functionality
// Maneja el cambio entre tema claro y oscuro

(function () {
  "use strict";

  // Constantes
  const THEME_KEY = "utp-market-theme";
  const THEME_DARK = "dark";
  const THEME_LIGHT = "light";

  /**
   * Obtiene el tema guardado en localStorage o el tema por defecto
   * @returns {string} El tema actual ('light' o 'dark')
   */
  function getSavedTheme() {
    const savedTheme = localStorage.getItem(THEME_KEY);
    return savedTheme || THEME_LIGHT;
  }

  /**
   * Guarda el tema en localStorage
   * @param {string} theme - El tema a guardar
   */
  function saveTheme(theme) {
    localStorage.setItem(THEME_KEY, theme);
  }

  /**
   * Aplica el tema al documento
   * @param {string} theme - El tema a aplicar
   */
  function applyTheme(theme) {
    if (theme === THEME_DARK) {
      document.documentElement.setAttribute("data-theme", "dark");
    } else {
      document.documentElement.removeAttribute("data-theme");
    }
  }

  /**
   * Actualiza el icono del botón de toggle
   * @param {string} theme - El tema actual
   */
  function updateToggleIcon(theme) {
    const toggleBtn = document.getElementById("theme-toggle-btn");
    if (!toggleBtn) return;

    const icon = toggleBtn.querySelector("i");
    if (!icon) return;

    if (theme === THEME_DARK) {
      icon.classList.remove("bi-moon-fill");
      icon.classList.add("bi-sun-fill");
      toggleBtn.setAttribute("title", "Cambiar a tema claro");
    } else {
      icon.classList.remove("bi-sun-fill");
      icon.classList.add("bi-moon-fill");
      toggleBtn.setAttribute("title", "Cambiar a tema oscuro");
    }
  }

  /**
   * Alterna entre tema claro y oscuro
   */
  function toggleTheme() {
    const currentTheme = getSavedTheme();
    const newTheme = currentTheme === THEME_DARK ? THEME_LIGHT : THEME_DARK;

    applyTheme(newTheme);
    saveTheme(newTheme);
    updateToggleIcon(newTheme);

    // Disparar evento personalizado para que otros componentes puedan reaccionar
    const event = new CustomEvent("themeChanged", {
      detail: { theme: newTheme },
    });
    document.dispatchEvent(event);
  }

  /**
   * Inicializa el toggle de tema
   */
  function initThemeToggle() {
    // Aplicar tema guardado inmediatamente (antes de DOMContentLoaded para evitar flash)
    const savedTheme = getSavedTheme();
    applyTheme(savedTheme);

    // Cuando el DOM esté listo, configurar el botón
    document.addEventListener("DOMContentLoaded", function () {
      updateToggleIcon(savedTheme);

      // Agregar event listener al botón de toggle
      const toggleBtn = document.getElementById("theme-toggle-btn");
      if (toggleBtn) {
        toggleBtn.addEventListener("click", function (e) {
          e.preventDefault();
          toggleTheme();
        });
      }
    });
  }

  // Inicializar
  initThemeToggle();

  // Exponer función global para uso externo si es necesario
  window.toggleTheme = toggleTheme;
})();
