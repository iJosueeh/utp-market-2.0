const filterForm = document.getElementById('filterForm');
const precioMinInput = document.getElementById('precioMin');
const precioMaxInput = document.getElementById('precioMax');
const rangePriceInput = document.getElementById('rangePrice');
const soloStockCheckbox = document.getElementById('soloStock');
const categoryCheckboxes = document.querySelectorAll('input[name="categoryId"]');
const sellerCheckboxes = document.querySelectorAll('input[name="sellerId"]');

// Pagination variables
let currentPage = 0; // Default to the first page
const pageSize = parseInt(document.body.getAttribute('data-page-size') || '12');
let totalProducts = parseInt(document.body.getAttribute('data-total-productos') || '0');
let totalPages = Math.ceil(totalProducts / pageSize);

function submitFilterForm() {
    const params = new URLSearchParams();

    // Get values from all filter controls
    if (soloStockCheckbox.checked) {
        params.set('inStockOnly', 'true');
    }
    params.set('minPrice', precioMinInput.value);
    params.set('maxPrice', precioMaxInput.value);

    categoryCheckboxes.forEach(cb => {
        if (cb.checked) {
            params.append('categoryId', cb.value);
        }
    });

    sellerCheckboxes.forEach(cb => {
        if (cb.checked) {
            params.append('sellerId', cb.value);
        }
    });

    const selectedMinRating = document.querySelector('input[name="minRating"]:checked');
    if (selectedMinRating) {
        params.set('minRating', selectedMinRating.value);
    }

    const searchTermInput = document.querySelector('input[name="searchTerm"]');
    if (searchTermInput && searchTermInput.value) {
        params.set('searchTerm', searchTermInput.value);
    }

    const sortBySelect = document.getElementById('sortBySelect');
    if (sortBySelect && sortBySelect.value) {
        params.set('sortBy', sortBySelect.value);
    }

    // Reset page to 0 when filters are applied
    params.set('page', '0');
    window.location.search = params.toString();
}

function limpiarFiltros() {
    precioMinInput.value = 0;
    precioMaxInput.value = 5000;
    rangePriceInput.value = 0;
    soloStockCheckbox.checked = false;
    categoryCheckboxes.forEach(cb => cb.checked = false);
    sellerCheckboxes.forEach(cb => cb.checked = false);
    // Submit the form after clearing filters
    submitFilterForm();
}

function mostrarAlertaCategoria(mensaje, tipo = 'success') {
    const favoriteNotificationAlert = document.getElementById('favoriteNotificationAlert');
    const favoriteNotificationMessage = document.getElementById('favoriteNotificationMessage');
    
    if (!favoriteNotificationAlert || !favoriteNotificationMessage) return;
    
    // Remover clases de tipo de alerta y animaciones previas
    favoriteNotificationAlert.classList.remove('d-none', 'alert-success', 'alert-danger', 'alert-info', 'alert-warning', 'animate__fadeOutUp');
    favoriteNotificationAlert.classList.add('alert-' + tipo, 'animate__fadeInDown');
    
    const iconClass = tipo === 'success' ? 'bi-check-circle-fill' : 
                      tipo === 'danger' ? 'bi-exclamation-triangle-fill' : 
                      tipo === 'info' ? 'bi-info-circle-fill' : 'bi-exclamation-circle-fill';
    
    favoriteNotificationMessage.innerHTML = `<i class="bi ${iconClass} me-2"></i>${mensaje}`;
    favoriteNotificationAlert.classList.remove('d-none');
    favoriteNotificationAlert.scrollIntoView({ behavior: 'smooth', block: 'center' });
    
    setTimeout(() => {
        favoriteNotificationAlert.classList.remove('animate__fadeInDown');
        favoriteNotificationAlert.classList.add('animate__fadeOutUp');
        setTimeout(() => {
            favoriteNotificationAlert.classList.add('d-none');
            favoriteNotificationAlert.classList.remove('animate__fadeOutUp');
        }, 500);
    }, 5000);
}

function agregarAlCarrito(button) {
    const productoId = button.dataset.productoId;
    
    const formData = new URLSearchParams();
    formData.append('productoId', productoId);
    formData.append('cantidad', 1);

    fetch('/carrito/agregar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData
    })
    .then(response => {
        if (response.ok) {
            mostrarAlertaCategoria('¡Producto agregado al carrito exitosamente!', 'success');
        } else {
            return response.text().then(text => {
                mostrarAlertaCategoria('Error al agregar al carrito: ' + text, 'danger');
            });
        }
    })
    .catch(error => {
        console.error('Error en la petición AJAX:', error);
        mostrarAlertaCategoria('Error de conexión al agregar al carrito.', 'danger');
    });
}

function agregarAFavoritos(button) {
    const isLoggedIn = document.body.getAttribute('data-is-logged-in') === 'true';
    const loginAlert = document.getElementById('loginAlert');
    const favoriteNotificationAlert = document.getElementById('favoriteNotificationAlert');
    const favoriteNotificationMessage = document.getElementById('favoriteNotificationMessage');

    // Hide any previous alerts with animation
    if (!loginAlert.classList.contains('d-none')) {
        loginAlert.classList.add('animate__fadeOutUp');
        setTimeout(() => {
            loginAlert.classList.add('d-none');
            loginAlert.classList.remove('animate__fadeOutUp');
        }, 500);
    }
    if (!favoriteNotificationAlert.classList.contains('d-none')) {
        favoriteNotificationAlert.classList.add('animate__fadeOutUp');
        setTimeout(() => {
            favoriteNotificationAlert.classList.add('d-none');
            favoriteNotificationAlert.classList.remove('animate__fadeOutUp');
        }, 500);
    }

    if (!isLoggedIn) {
        loginAlert.classList.remove('d-none', 'animate__fadeOutUp');
        loginAlert.classList.add('animate__fadeInDown');
        loginAlert.scrollIntoView({ behavior: 'smooth', block: 'center' });
        
        setTimeout(() => {
            loginAlert.classList.remove('animate__fadeInDown');
            loginAlert.classList.add('animate__fadeOutUp');
            setTimeout(() => {
                loginAlert.classList.add('d-none');
                loginAlert.classList.remove('animate__fadeOutUp');
            }, 500);
        }, 5000);
        return;
    }

    const productoId = button.dataset.productoId;
    const icon = button.querySelector('i');

    fetch(`/api/favoritos/toggle/${productoId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(errorData => {
                throw new Error(errorData.message || 'Error desconocido');
            });
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            const iconClass = data.added ? 'bi-check-circle-fill' : 'bi-info-circle-fill';
            
            if (data.added) {
                icon.classList.remove('bi-heart');
                icon.classList.add('bi-heart-fill', 'text-danger');
                favoriteNotificationAlert.classList.remove('alert-danger', 'alert-info');
                favoriteNotificationAlert.classList.add('alert-success');
            } else {
                icon.classList.remove('bi-heart-fill', 'text-danger');
                icon.classList.add('bi-heart');
                favoriteNotificationAlert.classList.remove('alert-success', 'alert-danger');
                favoriteNotificationAlert.classList.add('alert-info');
            }
            
            favoriteNotificationMessage.innerHTML = `<i class="bi ${iconClass} me-2"></i>${data.message}`;
            favoriteNotificationAlert.classList.remove('d-none', 'animate__fadeOutUp');
            favoriteNotificationAlert.classList.add('animate__fadeInDown');
            favoriteNotificationAlert.scrollIntoView({ behavior: 'smooth', block: 'center' });

            setTimeout(() => {
                favoriteNotificationAlert.classList.remove('animate__fadeInDown');
                favoriteNotificationAlert.classList.add('animate__fadeOutUp');
                setTimeout(() => {
                    favoriteNotificationAlert.classList.add('d-none');
                    favoriteNotificationAlert.classList.remove('animate__fadeOutUp');
                }, 500);
            }, 3000);

            console.log(data.message);
        } else {
            console.error('Error al actualizar favoritos:', data.message);
            favoriteNotificationAlert.classList.remove('alert-success', 'alert-info');
            favoriteNotificationAlert.classList.add('alert-danger');
            favoriteNotificationMessage.innerHTML = `<i class="bi bi-exclamation-triangle-fill me-2"></i>${data.message}`;
            favoriteNotificationAlert.classList.remove('d-none', 'animate__fadeOutUp');
            favoriteNotificationAlert.classList.add('animate__fadeInDown');
            favoriteNotificationAlert.scrollIntoView({ behavior: 'smooth', block: 'center' });
            
            setTimeout(() => {
                favoriteNotificationAlert.classList.remove('animate__fadeInDown');
                favoriteNotificationAlert.classList.add('animate__fadeOutUp');
                setTimeout(() => {
                    favoriteNotificationAlert.classList.add('d-none');
                    favoriteNotificationAlert.classList.remove('animate__fadeOutUp');
                }, 500);
            }, 5000);
        }
    })
    .catch(error => {
        console.error('Error en la petición AJAX de favoritos:', error);
        favoriteNotificationAlert.classList.remove('alert-success', 'alert-info');
        favoriteNotificationAlert.classList.add('alert-danger');
        favoriteNotificationMessage.innerHTML = `<i class="bi bi-exclamation-triangle-fill me-2"></i>Error de conexión: ${error.message}`;
        favoriteNotificationAlert.classList.remove('d-none', 'animate__fadeOutUp');
        favoriteNotificationAlert.classList.add('animate__fadeInDown');
        favoriteNotificationAlert.scrollIntoView({ behavior: 'smooth', block: 'center' });
        
        setTimeout(() => {
            favoriteNotificationAlert.classList.remove('animate__fadeInDown');
            favoriteNotificationAlert.classList.add('animate__fadeOutUp');
            setTimeout(() => {
                favoriteNotificationAlert.classList.add('d-none');
                favoriteNotificationAlert.classList.remove('animate__fadeOutUp');
            }, 500);
        }, 5000);
    });
}

// Function to check favorite status for a product
function checkFavoriteStatus(productId, iconElement) {
    const isLoggedIn = document.body.getAttribute('data-is-logged-in') === 'true';
    console.log(`checkFavoriteStatus: Checking productId ${productId}, isLoggedIn: ${isLoggedIn}`);
    if (!isLoggedIn) return; // No need to check if not logged in

    fetch(`/api/favoritos/isFavorito/${productId}`)
        .then(response => response.json())
        .then(data => {
            console.log(`checkFavoriteStatus: Response for productId ${productId}:`, data);
            if (data.success && data.isFavorito) {
                iconElement.classList.remove('bi-heart');
                iconElement.classList.add('bi-heart-fill');
                iconElement.classList.add('text-danger');
            } else {
                iconElement.classList.remove('bi-heart-fill');
                iconElement.classList.remove('text-danger');
                iconElement.classList.add('bi-heart');
            }
        })
        .catch(error => {
            console.error('Error al verificar estado de favoritos:', error);
        });
}

// --- Frontend Pagination Logic ---
function renderPaginationControls() {
    const paginationContainer = document.getElementById('pagination-controls');
    if (!paginationContainer || totalPages <= 1) {
        if (paginationContainer) paginationContainer.innerHTML = ''; // Clear if no pagination needed
        return;
    }

    let paginationHtml = `
        <nav>
            <ul class="pagination justify-content-center">
                <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
                    <a class="page-link" href="#" data-page="${currentPage - 1}" tabindex="-1">Anterior</a>
                </li>
    `;

    for (let i = 0; i < totalPages; i++) {
        paginationHtml += `
            <li class="page-item ${currentPage === i ? 'active' : ''}">
                <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
            </li>
        `;
    }

    paginationHtml += `
                <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                    <a class="page-link" href="#" data-page="${currentPage + 1}">Siguiente</a>
                </li>
            </ul>
        </nav>
    `;
    paginationContainer.innerHTML = paginationHtml;

    // Add event listeners to new pagination links
    paginationContainer.querySelectorAll('.page-link').forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault();
            const newPage = parseInt(this.dataset.page);
            if (newPage >= 0 && newPage < totalPages) {
                displayProductsForPage(newPage);
            }
        });
    });
}

function displayProductsForPage(pageNumber) {
    currentPage = pageNumber;
    const productCards = document.querySelectorAll('.product-card');
    const startIndex = currentPage * pageSize;
    const endIndex = startIndex + pageSize;

    productCards.forEach((card, index) => {
        if (index >= startIndex && index < endIndex) {
            card.style.display = 'block'; // Show product card
        } else {
            card.style.display = 'none'; // Hide product card
        }
    });

    renderPaginationControls(); // Re-render pagination to update active state
    window.scrollTo({ top: 0, behavior: 'smooth' }); // Scroll to top
}


document.addEventListener('DOMContentLoaded', function() {
    const precioMinInput = document.getElementById('precioMin');
    const precioMaxInput = document.getElementById('precioMax');
    const rangePriceInput = document.getElementById('rangePrice');
    const soloStockCheckbox = document.getElementById('soloStock');
    const categoryCheckboxes = document.querySelectorAll('input[name="categoryId"]');
    const sellerCheckboxes = document.querySelectorAll('input[name="sellerId"]');
    const ratingRadioButtons = document.querySelectorAll('input[name="minRating"]');
    const sortBySelect = document.getElementById('sortBySelect');
    const searchTermInput = document.querySelector('input[name="searchTerm"]'); // Get the search input

    // Initialize pagination variables from data attributes
    totalProducts = parseInt(document.body.getAttribute('data-total-productos') || '0');
    totalPages = Math.ceil(totalProducts / pageSize);

    // Sincronizar slider con input max
    rangePriceInput.addEventListener('input', function() {
        precioMaxInput.value = this.value;
    });

    // Sincronizar input max con slider
    precioMaxInput.addEventListener('input', function() {
        if (parseInt(this.value) > parseInt(precioMinInput.value)) {
            rangePriceInput.value = this.value;
        } else {
            rangePriceInput.value = precioMinInput.value;
            this.value = precioMinInput.value;
        }
    });

    // Sincronizar input min con slider (opcional, si el slider controla el max)
    precioMinInput.addEventListener('input', function() {
        if (parseInt(this.value) > parseInt(precioMaxInput.value)) {
            precioMaxInput.value = this.value;
            rangePriceInput.value = this.value;
        }
    });

    // Event listeners para enviar el formulario automáticamente al cambiar los filtros
    soloStockCheckbox.addEventListener('change', submitFilterForm);
    precioMinInput.addEventListener('change', submitFilterForm);
    precioMaxInput.addEventListener('change', submitFilterForm);
    rangePriceInput.addEventListener('change', submitFilterForm); // Submit when slider is released

    categoryCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', submitFilterForm);
    });

    sellerCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', submitFilterForm);
    });

    ratingRadioButtons.forEach(radio => {
        radio.addEventListener('change', submitFilterForm);
    });

    // Add event listener for the search term input
    if (searchTermInput) {
        searchTermInput.addEventListener('change', submitFilterForm);
    }

    // Restore selected sort option using data attribute
    if (sortBySelect) {
        const currentSortByValue = sortBySelect.getAttribute('data-current-sort-by');
        if (currentSortByValue) {
            for (let i = 0; i < sortBySelect.options.length; i++) {
                if (sortBySelect.options[i].value === currentSortByValue) {
                    sortBySelect.options[i].selected = true;
                    break;
                }
            }
        }
        // New event listener for sortBySelect
        sortBySelect.addEventListener('change', function() {
            const params = new URLSearchParams(window.location.search);
            params.set('sortBy', this.value);
            // Preserve other filter parameters
            params.set('inStockOnly', soloStockCheckbox.checked);
            params.set('minPrice', precioMinInput.value);
            params.set('maxPrice', precioMaxInput.value);
            params.set('searchTerm', document.querySelector('input[name="searchTerm"]').value);

            // Handle multiple categoryIds
            const selectedCategoryIds = Array.from(categoryCheckboxes)
                .filter(cb => cb.checked)
                .map(cb => cb.value);
            params.delete('categoryId'); // Clear existing ones
            selectedCategoryIds.forEach(id => params.append('categoryId', id));

            // Handle multiple sellerIds
            const selectedSellerIds = Array.from(sellerCheckboxes)
                .filter(cb => cb.checked)
                .map(cb => cb.value);
            params.delete('sellerId'); // Clear existing ones
            selectedSellerIds.forEach(id => params.append('sellerId', id));

            // Handle minRating
            const selectedMinRating = document.querySelector('input[name="minRating"]:checked');
            if (selectedMinRating) {
                params.set('minRating', selectedMinRating.value);
            } else {
                params.delete('minRating');
            }

            // Preserve current page when sorting
            params.set('page', currentPage.toString());

            window.location.search = params.toString();
        });
    }

    // Check favorite status for all products on page load
    document.querySelectorAll('.product-card').forEach(card => {
        const favoriteButton = card.querySelector('button[onclick="agregarAFavoritos(this)"]');
        if (favoriteButton) {
            const productId = favoriteButton.dataset.productoId;
            const iconElement = favoriteButton.querySelector('i');
            checkFavoriteStatus(productId, iconElement);
        }
    });

    // Initial render of products and pagination
    const urlParams = new URLSearchParams(window.location.search);
    currentPage = parseInt(urlParams.get('page') || '0');
    displayProductsForPage(currentPage);
});